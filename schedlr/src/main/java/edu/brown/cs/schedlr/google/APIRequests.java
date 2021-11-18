package edu.brown.cs.schedlr.google;

import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.calendar.model.FreeBusyCalendar;
import com.google.api.services.calendar.model.FreeBusyRequest;
import com.google.api.services.calendar.model.FreeBusyRequestItem;
import com.google.api.services.calendar.model.FreeBusyResponse;
import com.google.api.services.calendar.model.TimePeriod;
import edu.brown.cs.schedlr.algorithm.AssignedTaskBlock;
import edu.brown.cs.schedlr.database.Query;
import edu.brown.cs.schedlr.database.Singleton;
import edu.brown.cs.schedlr.run.User;

import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Manages API requests to Google.
 */
public class APIRequests {
  private static final String CREDENTIALS_FILE_PATH =
      System.getProperty("user.dir") + "/credentials.json";
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  private static final String APPLICATION_NAME = "schedlr";

  /**
   * Gets the busy blocks for all of a user's calendars.
   *
   * @param accessToken the access token
   * @param start       the start of the range
   * @param end         the end of the range
   * @return a List of Time Periods that contain a Start and End for that block
   * @throws IOException
   * @throws GeneralSecurityException
   */
  public static List<TimePeriod> getBusy(String accessToken, DateTime start, DateTime end)
      throws IOException, GeneralSecurityException {
    Calendar service = getService(accessToken);

    /* gets list of calendars to get free/busy information from. FIXME: should have user preference for calendar? */
    CalendarList calendarList = service.calendarList().list().execute();
    List<CalendarListEntry> items = calendarList.getItems();
    List<FreeBusyRequestItem> ids = new ArrayList<>();
    for (CalendarListEntry item : items) {
      ids.add(new FreeBusyRequestItem().setId(item.getId()));
    }

    /* makes free busy request */
    FreeBusyRequest request = new FreeBusyRequest()
        .setTimeMin(start)
        .setTimeMax(end)
        .setItems(ids);
    FreeBusyResponse response = service.freebusy().query(request).execute();

    /* accumulates busy blocks */
    List<TimePeriod> busyBlocks = new ArrayList<>();
    for (FreeBusyCalendar cal : response.getCalendars().values()) {
      busyBlocks.addAll(cal.getBusy());
    }
    return busyBlocks;
  }

  /**
   * Creates a calendar for the user based on the access token and returns the calendarID.
   * @param accessToken - the accessToken of the user
   * @return
   * @throws IOException
   * @throws GeneralSecurityException
   */
  public static String createCalendar(String accessToken)
      throws IOException, GeneralSecurityException {
    Singleton database = Singleton.getInstance();
    Query query = database.getQuery();
    Calendar service = getService(accessToken);
    com.google.api.services.calendar.model.Calendar
        calendar = new com.google.api.services.calendar.model.Calendar()
        .setSummary("Schedlr")
        .setTimeZone("America/New_York");
    // FIXME: Replace timeZone
    com.google.api.services.calendar.model.Calendar
        createdCalendar = service.calendars().insert(calendar).execute();
    return createdCalendar.getId();
  }

  /**
   * Adds all database events to google calendar for a user.
   * @param user - the user
   * @param accessToken - the access token of the user
   * @throws SQLException
   * @throws GeneralSecurityException
   * @throws IOException
   */
  public static void addAllEvents(User user, String accessToken)
      throws SQLException, GeneralSecurityException, IOException {
    Query query = Singleton.getInstance().getQuery();
    List<AssignedTaskBlock> tasks = query.allScheduledTaskBlocks(user);

    Calendar service = getService(accessToken);

    // FIXME: DateTime might not work as expected.
    for (AssignedTaskBlock task : tasks) {
      Event toAdd = new Event()
          .setSummary(task.getTitle());

      EventDateTime start = new EventDateTime()
          .setDateTime(new DateTime(task.getStartTime()))
          .setTimeZone("America/New_York");
      toAdd.setStart(start);

      EventDateTime end = new EventDateTime()
          .setDateTime(new DateTime(task.getEndTime()))
          .setTimeZone("America/New_York");
      toAdd.setEnd(end);

      String calendarID = user.getCalendarID();
      service.events().insert(calendarID, toAdd).execute();
    }
  }

  /**
   * Deletes all events from our shared calendar.
   * @param user - the user
   * @param accessToken - the access token of the user.
   * @throws GeneralSecurityException
   * @throws IOException
   */
  public static void clearCalendar(User user, String accessToken)
      throws GeneralSecurityException, IOException {
    Calendar service = getService(accessToken);
    String pageToken = null;
    while (true) {
      Events events = service.events().list(user.getCalendarID()).setPageToken(pageToken).execute();
      List<Event> items = events.getItems();
      for (Event event : items) {
        service.events().delete(user.getCalendarID(), event.getId()).execute();
      }
      pageToken = events.getNextPageToken();
      if (pageToken == null) {
        break;
      }
    }
  }

  /**
   * Verifies the one-time authentication code to login a user.
   *
   * @param code - a string, the authentication code
   * @return a GoogleTokenResponse
   * @throws IOException
   */
  public static GoogleTokenResponse getTokenResponse(String code) throws IOException {
    GoogleClientSecrets clientSecrets =
        GoogleClientSecrets
            .load(JacksonFactory.getDefaultInstance(), new FileReader(CREDENTIALS_FILE_PATH));

    return new GoogleAuthorizationCodeTokenRequest(
        new NetHttpTransport(),
        JacksonFactory.getDefaultInstance(),
        "https://oauth2.googleapis.com/token",
        clientSecrets.getDetails().getClientId(),
        clientSecrets.getDetails().getClientSecret(),
        code,
        "http://localhost:3000")
        .execute();
  }

  public static Calendar getService(String accessToken)
      throws GeneralSecurityException, IOException {
    GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
        .setApplicationName(APPLICATION_NAME)
        .build();
  }
}
