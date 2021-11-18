package edu.brown.cs.schedlr.run;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.TimePeriod;
import edu.brown.cs.schedlr.algorithm.AssignedTaskBlock;
import edu.brown.cs.schedlr.algorithm.BusyBlock;
import edu.brown.cs.schedlr.algorithm.Schedule;
import edu.brown.cs.schedlr.algorithm.ScheduleConstants;
import edu.brown.cs.schedlr.algorithm.Task;
import edu.brown.cs.schedlr.database.Query;
import edu.brown.cs.schedlr.database.Singleton;
import edu.brown.cs.schedlr.google.APIRequests;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Class containing methods used by frontend handlers in FrontendRequestHandler.
 */
public final class Schedlr {

  /**
   * Static instance of database.
   */
  private static final Query DATABASE = Singleton.getInstance().getQuery();

  /**
   * Constructor for Schedlr.
   */
  private Schedlr() {
  }
  /**
   * Generates a user's schedule.
   * @param userID String representing a user's ID within the database.
   * @param accessToken String representing access token used for API requests.
   * @return List of AssignedTaskBlocks representing the user's schedule
   * @throws SQLException if unable to parse SQL
   * @throws ParseException if unable to parse dates
   */
  public static List<AssignedTaskBlock> generateSchedule(String userID, String accessToken)
          throws SQLException, ParseException {

    User user = DATABASE.selectUser(userID);

    DATABASE.clearScheduledTasks(user);

    // todo: from database sort by deadline -- should be an array list
    List<Task> tasks = DATABASE.allIncompleteTasks(user);

    if (tasks.isEmpty()) {
      DATABASE.setAllTasksSchedule(user);
      DATABASE.setCompletedTasksUnscheduled(user);
      return new ArrayList<>();
    }

    Date start = new Date();

    //  todo: don't sort if sorted in db
    tasks.sort(Comparator.comparing(Task::getDeadline));
    Date end = tasks.get(tasks.size() - 1).getDeadline();

    List<TimePeriod> timePeriods;
    try {
      timePeriods = APIRequests.getBusy(accessToken, new DateTime(start), new DateTime(end));
    } catch (IOException | GeneralSecurityException e) {
      // todo: error handling
      e.printStackTrace();
      timePeriods = new ArrayList<>();
    }

    List<BusyBlock> busyBlocks = timePeriodsToBusyBlocks(timePeriods);

    int dailyStart = user.getStart();
    int dailyEnd = user.getEnd();

    Schedule sched = new Schedule(tasks, busyBlocks, dailyStart, dailyEnd);
    List<AssignedTaskBlock> blocks = sched.getAssignedTaskBlocks();
    List<AssignedTaskBlock> toReturn = new ArrayList<>();
    for (AssignedTaskBlock block : blocks) {
      if (!block.getIsBreak()) {
        toReturn.add(block);
        DATABASE.addScheduled(user, block);
      }
    }

    DATABASE.setAllTasksSchedule(user);
    DATABASE.setCompletedTasksUnscheduled(user);

    return toReturn;
  }

  /**
   * Gets a user's current scheduled tasks without generating a new schedule.
   * @param userID String representing a user's ID within the database.
   * @return List of AssignedTaskBlocks representing the user's schedule
   * @throws SQLException if unable to parse SQL
   */
  public static List<AssignedTaskBlock> getAssignedTasks(String userID) throws SQLException {
    User user = DATABASE.selectUser(userID);
    return DATABASE.allScheduledTaskBlocks(user);
  }

  /**
   * Adds a new task to a user's set of tasks within the database.
   * @param userID String, a user's ID within the database.
   * @param taskName String, the title of the task to be added.
   * @param deadline Date, the date the task must be completed by.
   * @param duration int, the number of minutes the task will take to complete.
   * @throws SQLException if unable to parse SQL
   */
  public static void addTask(String userID, String taskName, Date deadline, int duration)
      throws SQLException {
    //select user
    User user = DATABASE.selectUser(userID);
    DATABASE.addTask(user, duration, deadline, taskName, 0);
  }

  /**
   * Toggle the completion of an AssignedTaskBlock.
   * @param userID String, the user's ID
   * @param block AssignedTaskBlock, the block being completed/un-completed
   * @throws SQLException if unable to parse SQL
   */
  public static void toggleAssignedTaskCompletion(String userID, AssignedTaskBlock block)
      throws SQLException {
    User user = DATABASE.selectUser(userID);
    if (block.isComplete()) {
      DATABASE.updateScheduledBlockIncomplete(user, block);
    } else {
      DATABASE.updateScheduledBlockCompleted(user, block);
    }
  }

  /**
   * Update the estimated task time of a specific block.
   * @param userID String, the user's ID
   * @param block AssignedTaskBlock, the block being modified
   * @throws SQLException if unable to parse SQL
   */
  public static void updateTaskEstimatedTime(String userID, AssignedTaskBlock block)
      throws SQLException {
    User user = DATABASE.selectUser(userID);

    Date end = block.getEndTime();
    Date start = block.getStartTime();

    long millisDif = end.getTime() - start.getTime();
    long minutes = millisDif / ScheduleConstants.ONE_MINUTE_IN_MILLIS;

    Task origTask = DATABASE.getTaskByID(user, block.getTaskID());
    if (origTask != null) {
      int completionTime = origTask.getTimeToComplete();
      int newTime;
      if (!block.isComplete()) {
        newTime = completionTime - Math.toIntExact(minutes);
      } else {
        newTime = completionTime + Math.toIntExact(minutes);
      }
      DATABASE.updateTaskTime(user, origTask.getTaskID(), newTime);
    }
    origTask = DATABASE.getTaskByID(user, block.getTaskID());
    if (origTask != null) {
      if (origTask.getTimeToComplete() == 0) {
        DATABASE.updateTaskCompleted(user, origTask);
      } else {
        DATABASE.updateTaskIncomplete(user, origTask);
      }
    }
  }

  /**
   * Set the daily working hour range of a new user.
   * @param userID String, the user ID
   * @param start int, the start time from midnight
   * @param end int, the end time from midnight
   * @throws SQLException if unable to parse SQL
   */
  public static void setDailyTimes(String userID, int start, int end) throws SQLException {
    User user = DATABASE.selectUser(userID);

    DATABASE.updateDailyTimes(user, start, end);
  }

  /**
   * Toggle the completion of a Task.
   * @param userID String, the user's ID
   * @param task Task, the task being completed/un-completed
   * @throws SQLException if unable to parse SQL
   */
  public static void toggleTaskCompletion(String userID, Task task) throws SQLException {
    User user = DATABASE.selectUser(userID);

    if (task.isComplete()) {
      DATABASE.updateTaskIncomplete(user, task);
      DATABASE.updateAllScheduledBlocksIncomplete(user, task);
    } else {
      DATABASE.updateTaskCompleted(user, task);
      DATABASE.updateAllScheduledBlocksCompleted(user, task);
    }
  }

  /**
   * Delete a task from the user's task list.
   * @param userID String, the user's ID
   * @param taskID int, the taskID
   * @throws SQLException if unable to parse SQL
   */
  public static void deleteTask(String userID, int taskID) throws SQLException {
    User user = DATABASE.selectUser(userID);
    DATABASE.deleteTask(user, taskID);
    DATABASE.deleteAllSubtasks(user, taskID);
  }

  /**
   * Creates a list BusyBlock given unavailable TimePeriods on user's GCal.
   * @param timePeriods List of TimePeriods, occupied times on the users GCal
   * @return List of BusyBlocks representing the times cannot be scheduled
   */
  public static List<BusyBlock> timePeriodsToBusyBlocks(List<TimePeriod> timePeriods) {
    List<BusyBlock> busyBlocks = new ArrayList<>();
    for (TimePeriod timePeriod : timePeriods) {
      DateTime start = timePeriod.getStart();
      DateTime end = timePeriod.getEnd();

      long startInMillis = start.getValue();
      Date eventStart = new Date(startInMillis);

      long endInMillis = end.getValue();
      Date eventEnd = new Date(endInMillis);

      BusyBlock eventTask = new BusyBlock(eventStart, eventEnd);
      busyBlocks.add(eventTask);
    }
    return busyBlocks;
  }

  /**
   * Update a task.
   * @param userID String, the user's ID
   * @param newTask Task, the updated version of the task
   * @throws SQLException if unable to parse SQL
   */
  public static void modifyTask(String userID, Task newTask) throws SQLException {
    User user = DATABASE.selectUser(userID);
    DATABASE.updateTask(user, newTask);
  }

  /**
   * Check to see if a user is logged in.
   * @param userID String, the user ID
   * @return true if a user is logged in, false otherwise
   * @throws SQLException if unable to parse SQL
   */
  public static boolean checkForLogin(String userID) throws SQLException {
    return DATABASE.selectUser(userID) == null;
  }

  /**
   * Return a list of all a user's scheduled tasks.
   * @param userID String, the user ID
   * @return List of Tasks, the user's scheduled tasks
   * @throws SQLException if unable to parse SQL
   * @throws ParseException if unable to parse Dates
   */
  public static List<Task> allScheduledTasks(String userID) throws SQLException, ParseException {
    User user = DATABASE.selectUser(userID);
    return DATABASE.allScheduledTasks(user);
  }

  /**
   * Returns a list of all the users scheduled tasks that they have enough time to complete.
   *
   * @param userID - the users ID.
   * @return the list of tasks.
   * @throws SQLException if unable to parse SQL
   * @throws ParseException if unable to parse dates.
   */
  public static List<Task> allScheduledAllTimeTasks(String userID) throws SQLException, ParseException {
    User user = DATABASE.selectUser(userID);
    return DATABASE.allScheduledTasksAllTime(user);
  }

  /**
   * Returns a list of all the users scheduled tasks that they have some time to complete.
   *
   * @param userID - the users ID.
   * @return the list of tasks.
   * @throws SQLException if unable to parse SQL
   */
  public static List<Task> allScheduledSomeTimeTasks(String userID) throws SQLException {
    User user = DATABASE.selectUser(userID);
    return DATABASE.allScheduledTasksSomeTime(user);
  }

  /**
   * Returns a list of all the users scheduled tasks that they do not have enough time to complete.
   *
   * @param userID - the users ID.
   * @return the list of tasks.
   * @throws SQLException if unable to parse SQL
   */
  public static List<Task> allScheduledNotTimeTasks(String userID) throws SQLException {
    User user = DATABASE.selectUser(userID);
    return DATABASE.allScheduledTasksNotTime(user);
  }

  /**
   * Return a list of a user's unscheduled tasks.
   * @param userID String, the user ID
   * @return List of Tasks, the unscheduled user tasks
   * @throws SQLException if unable to parse SQL
   * @throws ParseException if unable to parse dates
   */
  public static List<Task> allUnscheduledTasks(String userID) throws SQLException, ParseException {
    User user = DATABASE.selectUser(userID);
    return DATABASE.allUnscheduledTasks(user);
  }

  /**
   * Returns a user from a userID.
   *
   * @param userID - the user ID
   * @return the User
   * @throws SQLException if unabel to parse SQL.
   */
  public static User getUser(String userID) throws SQLException {
    return DATABASE.selectUser(userID);
  }
}
