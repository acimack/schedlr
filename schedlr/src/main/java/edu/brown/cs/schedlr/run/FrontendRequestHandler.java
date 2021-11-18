package edu.brown.cs.schedlr.run;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.schedlr.algorithm.AlgorithmUtil;
import edu.brown.cs.schedlr.algorithm.AssignedTaskBlock;
import edu.brown.cs.schedlr.algorithm.Task;
import edu.brown.cs.schedlr.database.Query;
import edu.brown.cs.schedlr.database.Singleton;
import edu.brown.cs.schedlr.google.APIRequests;
import org.json.JSONObject;
import spark.ExceptionHandler;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handlers for frontend requests made in Main.
 */
public class FrontendRequestHandler {

  private static final Gson GSON = new Gson();
  private static final Singleton DATABASE = Singleton.getInstance();

  /**
   * Display an error page when an exception occurs in the server.
   */
  public static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(500);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }

  /**
   * Set the daily working times for a specific user in the database.
   */
  public static class SetDailyTimesHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());

      int start = data.getInt("startMin");
      int end = data.getInt("endMin");

      String currUserID = request.cookie("EXTERNAL_ID");
      if (Schedlr.checkForLogin(currUserID)) {
        System.out.println("There is no current user");
        Map<String, Object> variables = ImmutableMap.of();
        return GSON.toJson(variables);
      }

      Schedlr.setDailyTimes(currUserID, start, end);

      Map<String, Object> variables = ImmutableMap.of("success", true);
      return GSON.toJson(variables);
    }
  }


  /**
   * Handles adding a task (without scheduling it) to a user's list of tasks.
   */
  public static class AddTaskHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      String title = data.getString("title");
      int dur = data.getInt("duration");
      String deadlineStr = data.getString("deadline");

      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      Date deadline = sdf.parse(deadlineStr);
      List<String> taskList = new ArrayList<>();
      taskList.add(title);

      taskList.add(Integer.toString(dur));

      taskList.add(deadlineStr);

      Collection<Map<String, String>> schedReturn = new ArrayList<>();
      Collection<Map<String, String>> unschedReturn = new ArrayList<>();

      Collection<Map<String, String>> noTimeReturn = new ArrayList<>();
      Collection<Map<String, String>> someTimeReturn = new ArrayList<>();

      String currUserID = request.cookie("EXTERNAL_ID");
      if (Schedlr.checkForLogin(currUserID)) {
        System.out.println("There is no current user");
        Map<String, Object> variables =
                ImmutableMap.of("unscheduledTaskList", unschedReturn, "scheduledTaskListAll", schedReturn,"scheduledTaskListSome", someTimeReturn,"scheduledTaskListNone", noTimeReturn);
        return GSON.toJson(variables);
      }

      Schedlr.addTask(currUserID, title, deadline, dur);

      Map<String, Object> variables = ImmutableMap.of();
      return GSON.toJson(variables);
    }
  }

  /**
   * Handles retrieving the tasks associated with a user.
   */
  public static class GetTasksHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {

      Collection<Map<String, String>> schedReturn = new ArrayList<>();
      Collection<Map<String, String>> unschedReturn = new ArrayList<>();
      Collection<Map<String, String>> noTimeReturn = new ArrayList<>();
      Collection<Map<String, String>> someTimeReturn = new ArrayList<>();

      String currUserID = request.cookie("EXTERNAL_ID");
      if (Schedlr.checkForLogin(currUserID)) {
        System.out.println("There is no current user");
        Map<String, Object> variables = ImmutableMap.of("unscheduledTaskList",
            unschedReturn, "scheduledTaskList", schedReturn);
        return GSON.toJson(variables);
      }

      //handle task lists
      //shed
      List<Task> scheduledTaskListAll = Schedlr.allScheduledAllTimeTasks(currUserID);
      generateTaskLists(schedReturn, scheduledTaskListAll);

      // some sched
      List<Task> scheduledTaskListSome = Schedlr.allScheduledSomeTimeTasks(currUserID);
      generateTaskLists(someTimeReturn, scheduledTaskListSome);

      // none sched
      List<Task> scheduledTaskListNone = Schedlr.allScheduledNotTimeTasks(currUserID);
      generateTaskLists(noTimeReturn, scheduledTaskListNone);

      //unsched
      unschedReturn = new ArrayList<>();
      List<Task> unscheduledTaskList = Schedlr.allUnscheduledTasks(currUserID);

      generateTaskLists(unschedReturn, unscheduledTaskList);

      Map<String, Object> variables =
          ImmutableMap.of("unscheduledTaskList", unschedReturn, "scheduledTaskListAll",
                  schedReturn,"scheduledTaskListSome", someTimeReturn,"scheduledTaskListNone",
                  noTimeReturn);
      return GSON.toJson(variables);
    }

    private void generateTaskLists(Collection<Map<String, String>> schedReturn, List<Task> scheduledTaskListAll) {
      for (Task t : scheduledTaskListAll) {
        Map<String, String> thisTaskInfo = new HashMap<>();
        thisTaskInfo.put("id", String.valueOf(t.getTaskID()));
        thisTaskInfo.put("title", String.valueOf(t.getTitle()));
        thisTaskInfo.put("completion", Boolean.toString(t.isComplete()));
        thisTaskInfo.put("deadline", t.getDeadline().toString());
        thisTaskInfo.put("duration", String.valueOf(t.getTimeToComplete()));

        schedReturn.add(thisTaskInfo);
      }
    }
  }


  /**
   * Handles retrieving a newly generated schedule for user including,
   * scheduling unscheduled tasks.
   */
  public static class ScheduleHandler implements Route {
    private int counter = 0;

    @Override
    public Object handle(Request request, Response response) throws Exception {

      List<AssignedTaskBlock> scheduleList;

      String currUserID = request.cookie("EXTERNAL_ID");
      String accessToken = request.cookie("ACCESS_TOKEN");
      Collection<Map<String, String>> toReturn = new ArrayList<>();
      if (Schedlr.checkForLogin(currUserID)) {
        System.out.println("There is no current user");
        Map<String, Object> variables = ImmutableMap.of("schedule", toReturn.toArray());
        return GSON.toJson(variables);
      }
      Query query = Singleton.getInstance().getQuery();
      APIRequests.clearCalendar(query.selectUser(currUserID), accessToken);
      scheduleList = Schedlr.generateSchedule(currUserID, accessToken);

      APIRequests.addAllEvents(query.selectUser(currUserID), accessToken);

      toReturn = createAssignedTaskCollection(scheduleList);
      Map<String, Object> variables = ImmutableMap.of("schedule", toReturn.toArray());
      return GSON.toJson(variables);
    }
  }

  /**
   * Handles retrieving the existing schedule for user.
   */
  public static class AlreadyScheduledHandler implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {

      List<AssignedTaskBlock> scheduleList;
      String currUserID = request.cookie("EXTERNAL_ID");
      Collection<Map<String, String>> toReturn = new ArrayList<>();
      if (Schedlr.checkForLogin(currUserID)) {
        System.out.println("There is no current user");
        Map<String, Object> variables = ImmutableMap.of("schedule", toReturn.toArray());
        return GSON.toJson(variables);
      }

      scheduleList = Schedlr.getAssignedTasks(currUserID);

      toReturn = createAssignedTaskCollection(scheduleList);

      Map<String, Object> variables = ImmutableMap.of("schedule", toReturn.toArray());
      return GSON.toJson(variables);
    }
  }


  /**
   * Turns an assignedBlockList into a collection.
   * @param taskList - the task list
   * @return the collection
   */
  public static Collection<Map<String, String>> createAssignedTaskCollection(
      List<AssignedTaskBlock> taskList) {
    Collection<Map<String, String>> toReturn = new ArrayList<>();

    for (AssignedTaskBlock t : taskList) {
      Map<String, String> thisTaskInfo = new HashMap<>();
      thisTaskInfo.put("id", String.valueOf(t.getTaskID()));
      thisTaskInfo.put("deadline", t.getTaskDeadline().toString());
      thisTaskInfo.put("start", "" + t.getStartTime().toString());
      thisTaskInfo.put("end", "" + t.getEndTime().toString());
      thisTaskInfo.put("title", t.getTitle());
      thisTaskInfo.put("completion", String.valueOf(t.isComplete()));
      toReturn.add(thisTaskInfo);
    }
    return toReturn;
  }


  /**
   * Handles toggling completion of assigned tasks.
   */
  public static class ToggleAssignedCompletionHandler implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {

      SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy");

      JSONObject data = new JSONObject(request.body());
      JSONObject taskInfo = data.getJSONObject("taskInfo");

      int id = Integer.parseInt(taskInfo.getString("id"));
      Date deadline = sdf.parse(taskInfo.getString("deadline"));
      Date start = sdf.parse(taskInfo.getString("start"));
      Date end = sdf.parse(taskInfo.getString("end"));
      String title = taskInfo.getString("title");
      String completion = taskInfo.getString("completion");

      boolean completionBool = false;
      if (completion.equals("true")) {
        completionBool = true;
      }


      AssignedTaskBlock tempBlock =
          new AssignedTaskBlock(id, deadline, start, end, false, title, completionBool);
      String currUserID = request.cookie("EXTERNAL_ID");
      if (Schedlr.checkForLogin(currUserID)) {
        Map<String, Object> variables = ImmutableMap.of();
        return GSON.toJson(variables);
      }

      Schedlr.toggleAssignedTaskCompletion(currUserID, tempBlock);
      Schedlr.updateTaskEstimatedTime(currUserID, tempBlock);

      Map<String, Object> variables = ImmutableMap.of();
      return GSON.toJson(variables);
    }
  }

  /**
   * Handles toggling completion of tasks.
   */
  public static class ToggleTaskCompletionHandler implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {

      SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy");

      JSONObject data = new JSONObject(request.body());
      JSONObject taskInfo = data.getJSONObject("taskInfo");

      int id = Integer.parseInt(taskInfo.getString("id"));
      int duration = Integer.parseInt(taskInfo.getString("duration"));
      Date deadline = sdf.parse(taskInfo.getString("deadline"));
      String title = taskInfo.getString("title");
      String completion = taskInfo.getString("completion");

      boolean completionBool = false;
      if (completion.equals("true")) {
        completionBool = true;
      }

      Task tempBlock = new Task(id, duration, deadline, title, completionBool);
      String currUserID = request.cookie("EXTERNAL_ID");
      if (Schedlr.checkForLogin(currUserID)) {
        Map<String, Object> variables = ImmutableMap.of();
        return GSON.toJson(variables);
      }

      Schedlr.toggleTaskCompletion(currUserID, tempBlock);

      Map<String, Object> variables = ImmutableMap.of();
      return GSON.toJson(variables);
    }
  }


  /**
   * Handles deleting tasks.
   */
  public static class DeleteTaskHandler implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {

      JSONObject data = new JSONObject(request.body());
      JSONObject taskInfo = data.getJSONObject("taskInfo");

      int id = Integer.parseInt(taskInfo.getString("id"));

      String currUserID = request.cookie("EXTERNAL_ID");
      if (Schedlr.checkForLogin(currUserID)) {
        Map<String, Object> variables = ImmutableMap.of();
        return GSON.toJson(variables);
      }

      Schedlr.deleteTask(currUserID, id);

      Map<String, Object> variables = ImmutableMap.of();
      return GSON.toJson(variables);
    }
  }

  /**
   * Handles modifying tasks.
   */
  public static class ModifyTaskHandler implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {

      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

      JSONObject data = new JSONObject(request.body());
      JSONObject taskInfo = data.getJSONObject("taskInfo");
      int id = Integer.parseInt(taskInfo.getString("id"));
      String title = taskInfo.getString("newTitle");
      int dur = taskInfo.getInt("newDuration");
      Date deadline = sdf.parse(taskInfo.getString("newDeadline"));


      String currUserID = request.cookie("EXTERNAL_ID");
      if (Schedlr.checkForLogin(currUserID)) {
        Map<String, Object> variables = ImmutableMap.of();
        return GSON.toJson(variables);
      }

      Task newTask = new Task(id, dur, deadline, title);

      Schedlr.modifyTask(currUserID, newTask);

      Map<String, Object> variables = ImmutableMap.of();
      return GSON.toJson(variables);
    }
  }

  /**
   * Handles getting a users start and end times.
   */
  public static class GetDailyTimesHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {

      String currUserID = request.cookie("EXTERNAL_ID");
      if (Schedlr.checkForLogin(currUserID)) {
        Map<String, Object> variables = ImmutableMap.of();
        return GSON.toJson(variables);
      }

      User user = Schedlr.getUser(currUserID);

      int start = user.getStart();
      int end = user.getEnd();

      Map<String, Object> variables = ImmutableMap.of("start", start, "end", end);
      return GSON.toJson(variables);
    }
  }

  /**
   * Handler for user logins.
   */
  public static class LoginHandler implements Route {
    @Override
    public Object handle(Request request, Response response) {
      try {
        Query query = DATABASE.getQuery();

        JSONObject data = new JSONObject(request.body());

        if (!data.has("code")) {
          System.out.println("ERROR: Failed to login");
          return null;
        }

        String code = data.getString("code");

        GoogleTokenResponse tokenResponse = APIRequests.getTokenResponse(code);
        GoogleIdToken identifier = tokenResponse.parseIdToken();
        GoogleIdToken.Payload payload = identifier.getPayload();

        String salt = query.getSaltElse(payload.getSubject());
        byte[] hashed =
            AlgorithmUtil.saltAndHash(payload.getSubject(), AlgorithmUtil.fromHex(salt));
        String externalID = AlgorithmUtil.toHex(hashed);

        response.cookie("EXTERNAL_ID", externalID);
        response.cookie("ACCESS_TOKEN", tokenResponse.getAccessToken());
        response.cookie("REFRESH_TOKEN", tokenResponse.getRefreshToken());

        User currentUser = query.selectUser(externalID);

        if (currentUser == null) {
          currentUser = new User(
              externalID,
              (String) payload.get("name"),
              payload.getEmail(),
              APIRequests.createCalendar(tokenResponse.getAccessToken()));
          query.addUser(currentUser);
          return GSON
              .toJson(ImmutableMap.of("onboard", true, "token", tokenResponse.getAccessToken()));
        } else {
          APIRequests.clearCalendar(currentUser, tokenResponse.getAccessToken());
          return GSON
              .toJson(ImmutableMap.of("onboard", false, "token", tokenResponse.getAccessToken()));
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }
  }
}
