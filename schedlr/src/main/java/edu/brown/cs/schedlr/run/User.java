package edu.brown.cs.schedlr.run;

import java.util.HashMap;
import java.util.Objects;

/**
 * Class representing the user object.
 */
public class User {
  private String externalId;
  private String username;
  private String email;
  private int start;
  private int end;
  private String calendarID;
  private HashMap<String, String> userPreferences;

  /**
   * Constructor for user class.
   * @param id - unique external id
   * @param username - the username of the user
   * @param email - the email of the user
   * @param start - an int representing when a user starts their day
   * @param end - an int representing when a user ends their day
   * @param calendarID - the calendarID of the user, given by google
   */
  public User(String id, String username, String email, int start, int end, String calendarID) {
    this.externalId = id;
    this.username = username;
    this.email = email;
    this.start = start;
    this.end = end;
    this.calendarID = calendarID;
    this.userPreferences = new HashMap<>();
    userPreferences.put("wakeTime", "");
    userPreferences.put("bedTime", "");
    userPreferences.put("deadTime", "");
    userPreferences.put("wakeTime", "");
    userPreferences.put("productiveHours", "");
  }

  /**
   * Constructor to make a user before we know what their preferences are.
   * @param id - unique external id
   * @param username - username of the user
   * @param email - email of the user
   * @param calendarID - calendarID of the user, given by google
   */
  public User(String id, String username, String email, String calendarID) {
    this.externalId = id;
    this.username = username;
    this.email = email;
    this.calendarID = calendarID;
    this.userPreferences = new HashMap<>();
    userPreferences.put("wakeTime", "");
    userPreferences.put("bedTime", "");
    userPreferences.put("deadTime", "");
    userPreferences.put("wakeTime", "");
    userPreferences.put("productiveHours", "");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return start == user.start && end == user.end && externalId.equals(user.externalId) &&
        Objects.equals(username, user.username) &&
        Objects.equals(email, user.email) &&
        Objects.equals(calendarID, user.calendarID);
  }

  @Override
  public int hashCode() {
    return Objects.hash(externalId, username, email, start, end, calendarID);
  }

  public String getUsername() {
    return this.username;
  }

  public String getEmail() {
    return this.email;
  }

  public String getExternalId() {
    return this.externalId;
  }

  public String getCalendarID() {
    return this.calendarID;
  }

  public int getStart() {
    return this.start;
  }

  public int getEnd() {
    return this.end;
  }
}
