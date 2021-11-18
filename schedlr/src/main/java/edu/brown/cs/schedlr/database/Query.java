package edu.brown.cs.schedlr.database;

import edu.brown.cs.schedlr.algorithm.AlgorithmUtil;
import edu.brown.cs.schedlr.algorithm.AssignedTaskBlock;
import edu.brown.cs.schedlr.algorithm.Task;
import edu.brown.cs.schedlr.run.User;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Query {
  private static Connection conn = null;

  /**
   * Initializes the query class.
   *
   * @param filename - the name of the database file.
   * @throws SQLException if unable to initialize database
   * @throws ClassNotFoundException if unable to setup the file
   */
  public Query(String filename) throws SQLException, ClassNotFoundException {
    Class.forName("org.sqlite.JDBC");
    String urlToDB = "jdbc:sqlite:" + filename;
    conn = DriverManager.getConnection(urlToDB);
    Statement stat = conn.createStatement();
    stat.executeUpdate("PRAGMA foreign_keys=ON;");
  }

  /**
   * Finds the salt associated with a subject, if there's no salt, generates one and adds it to db.
   *
   * @param subject - the payload subject from the google user.
   * @return a string, the salt of the user or the newly created salt.
   * @throws SQLException - if it is unable to select from salts or insert into salts.
   */
  public String getSaltElse(String subject) throws SQLException {
    String salt;
    PreparedStatement prep = conn.prepareStatement("SELECT salt FROM salts "
        + "WHERE SUBJECT = ?");
    prep.setString(1, subject);
    ResultSet rs = prep.executeQuery();
    if (rs.next()) {
      System.out.println("Found salt");
      salt = rs.getString(1);
    } else {
      System.out.println("Subject DNE");
      salt = AlgorithmUtil.toHex(AlgorithmUtil.generateSalt());
      prep = conn.prepareStatement("INSERT INTO salts (SUBJECT, salt) "
          + "VALUES (?, ?)");
      prep.setString(1, subject);
      prep.setString(2, salt);
      prep.executeUpdate();
    }
    rs.close();
    prep.close();
    return salt;
  }

  /**
   * Adds a user to the database.
   *
   * @param user - the user to be added.
   * @throws SQLException if we can't add the user.
   */
  public void addUser(User user) throws SQLException {
    PreparedStatement prep = conn.prepareStatement(
        "INSERT INTO users (EXTERNAL_ID, name, email, start, end, CALENDAR_ID) "
            + "VALUES (?, ?, ?, ?, ?, ?);");
    prep.setString(1, user.getExternalId());
    prep.setString(2, user.getUsername());
    prep.setString(3, user.getEmail());
    prep.setInt(4, 480);
    prep.setInt(5, 720);
    prep.setString(6, user.getCalendarID());
    prep.executeUpdate();
    prep.close();
  }

  /**
   * Deletes a user from the database.
   *
   * @param user - the user to be deleted.
   * @throws SQLException if we can't delete the user.
   */
  public void deleteUser(User user) throws SQLException {
    PreparedStatement prep = conn.prepareStatement("DELETE FROM users "
        + "WHERE EXTERNAL_ID = ?");
    prep.setString(1, user.getExternalId());
    prep.executeUpdate();
    prep.close();
  }

  /**
   * Given a user, updates it with new entries.
   *
   * @param user - the user to be updated.
   * @throws SQLException if we can't update the user.
   */
  public void updateUser(User user) throws SQLException {
    PreparedStatement prep = conn.prepareStatement("UPDATE users "
        + "SET EXTERNAL_ID = ?, "
        + "name = ?, "
        + "email = ?, "
        + "start = ?, "
        + "end = ?, "
        + "CALENDAR_ID = ? "
        + "WHERE (EXTERNAL_ID = ?);");
    prep.setString(1, user.getExternalId());
    prep.setString(2, user.getUsername());
    prep.setString(3, user.getEmail());
    prep.setInt(4, user.getStart());
    prep.setInt(5, user.getEnd());
    prep.setString(6, user.getCalendarID());
    prep.setString(7, user.getExternalId());
    prep.executeUpdate();
    prep.close();
  }

  /**
   * Finds the user based on their google id.
   *
   * @param id - a string, the id attached to a google user (payload.getSubject()).
   * @return - the user if they already exist in the database, null otherwise.
   * @throws SQLException if we can't query for the user.
   */
  public User selectUser(String id) throws SQLException {
    PreparedStatement prep = conn.prepareStatement("SELECT DISTINCT * FROM users "
        + "WHERE users.EXTERNAL_ID = ?;");
    prep.setString(1, id);
    ResultSet rs = prep.executeQuery();
    if (rs.next()) {
      return new User(
          rs.getString(1),
          rs.getString(2),
          rs.getString(3),
          rs.getInt(4),
          rs.getInt(5),
          rs.getString(6));
    }
    rs.close();
    prep.close();
    return null;
  }

  /**
   * Updates the daily times of a user.
   *
   * @param user  - the user to update.
   * @param start - the start time.
   * @param end   - the end time.
   * @throws SQLException if we can't update the user in the users table.
   */
  public void updateDailyTimes(User user, int start, int end) throws SQLException {
    PreparedStatement prep = conn.prepareStatement("UPDATE users "
        + "SET start = ?, end = ? WHERE EXTERNAL_ID = ?");
    prep.setInt(1, start);
    prep.setInt(2, end);
    prep.setString(3, user.getExternalId());
    prep.executeUpdate();
    prep.close();
  }

  /**
   * Given a result set, makes a list of tasks from that set.
   *
   * @param rs - the result set containing all the columns from the task table.
   * @return a list of tasks.
   * @throws SQLException if the list of tasks can't be created.
   */
  public List<Task> makeTasks(ResultSet rs) throws SQLException {
    List<Task> toReturn = new ArrayList<>();
    while (rs.next()) {
      Task toAdd = new Task(
          rs.getInt(1),
          rs.getInt(3),
          //Number of years since 1900?
          new Date(rs.getLong(4)),
          rs.getString(5),
          rs.getInt(6) == 1
      );
      toReturn.add(toAdd);
    }
    return toReturn;
  }

  /**
   * Adding a task based on info.
   *
   * @param user      - the user to add a task to.
   * @param duration  - duration of the task.
   * @param deadline  - deadline of the task.
   * @param title     - title of the task.
   * @param completed - whether or not the task has been completed.
   * @throws SQLException if we can't add the task.
   */
  public void addTask(User user, int duration, Date deadline, String title, int completed)
      throws SQLException {
    PreparedStatement prep = conn.prepareStatement(
        "INSERT INTO tasks (EXTERNAL_ID, duration, deadline, title, completed, scheduled)"
            + " VALUES (?, ?, ?, ?, ?, 0);");
    prep.setString(1, user.getExternalId());
    prep.setInt(2, duration);
    prep.setLong(3, deadline.getTime());
    prep.setString(4, title);
    prep.setInt(5, completed);
    prep.executeUpdate();
    prep.close();
  }

  /**
   * Sets all of the tasks in the task database to be marked as scheduled.
   *
   * @param user - the user of the tasks.
   * @throws SQLException if unable to execute SQL
   */
  public void setAllTasksSchedule(User user) throws SQLException {
    PreparedStatement prep = conn.prepareStatement("UPDATE tasks "
        + "SET scheduled = 1 WHERE tasks.EXTERNAL_ID = ? AND completed = 0;");
    prep.setString(1, user.getExternalId());
    prep.executeUpdate();
    prep.close();
  }

  public void setCompletedTasksUnscheduled(User user) throws SQLException {
    PreparedStatement prep = conn.prepareStatement("UPDATE tasks "
        + "SET scheduled = 0 WHERE completed = 1 AND tasks.EXTERNAL_ID = ?;");
    prep.setString(1, user.getExternalId());
    prep.executeUpdate();
    prep.close();
  }

  /**
   * Given a task id, deletes the tasks from the database.
   *
   * @param user   - the user of the task.
   * @param taskID - the id of the task.
   * @throws SQLException if the task cannot be deleted.
   */
  public void deleteTask(User user, int taskID) throws SQLException {
    PreparedStatement prep = conn.prepareStatement("DELETE FROM tasks "
        + "WHERE id = ? AND tasks.EXTERNAL_ID = ?;");
    prep.setInt(1, taskID);
    prep.setString(2, user.getExternalId());
    prep.executeUpdate();
    prep.close();
  }

  /**
   * Given a task id, deletes the scheduled tasks from the database.
   *
   * @param user   - the user of the task.
   * @param taskID - the id of the task.
   * @throws SQLException if the task cannot be deleted.
   */
  public void deleteAllSubtasks(User user, int taskID) throws SQLException {
    PreparedStatement prep = conn.prepareStatement("DELETE FROM scheduled "
        + "WHERE task_id = ? AND scheduled.EXTERNAL_ID = ?;");
    prep.setInt(1, taskID);
    prep.setString(2, user.getExternalId());
    prep.executeUpdate();
    prep.close();
  }

  /**
   * Given a task, updates its entry in the database.
   *
   * @param user - the user the task belongs to
   * @param task - a task to update.
   * @throws SQLException if the task can't be updated.
   */
  public void updateTask(User user, Task task) throws SQLException {
    // This assumes that the externalID for a task doesnt change
    PreparedStatement prep = conn.prepareStatement("UPDATE tasks "
        + "SET duration = ?, "
        + "deadline = ?, "
        + "title = ?, "
        + "completed = ? "
        + "WHERE id = ? AND EXTERNAL_ID = ?;");
    prep.setInt(1, task.getTimeToComplete());
    prep.setLong(2, task.getDeadline().getTime());
    prep.setString(3, task.getTitle());
    prep.setInt(4, task.isComplete() ? 1 : 0);
    prep.setInt(5, task.getTaskID());
    prep.setString(6, user.getExternalId());
    prep.executeUpdate();
    prep.close();
  }

  /**
   * Given a user, finds all the tasks associated w/ that user.
   *
   * @param user - a user.
   * @return a list of tasks.
   * @throws SQLException - if we can't select the tasks.
   */
  public List<Task> allTasks(User user) throws SQLException {
    PreparedStatement prep = conn.prepareStatement("SELECT DISTINCT * FROM tasks "
        + "WHERE tasks.EXTERNAL_ID = ?;");
    prep.setString(1, user.getExternalId());
    ResultSet rs = prep.executeQuery();
    List<Task> toReturn = makeTasks(rs);
    rs.close();
    prep.close();
    return toReturn;
  }

  /**
   * Given a user, finds all the incomplete tasks associated w/ that user.
   *
   * @param user - a user.
   * @return a list of tasks.
   * @throws SQLException if we can't find the tasks.
   */
  public List<Task> allIncompleteTasks(User user) throws SQLException {
    PreparedStatement prep = conn.prepareStatement("SELECT DISTINCT * FROM tasks "
        + "WHERE tasks.EXTERNAL_ID = ? AND tasks.completed = 0;");
    prep.setString(1, user.getExternalId());
    ResultSet rs = prep.executeQuery();
    List<Task> toReturn = makeTasks(rs);
    rs.close();
    prep.close();
    return toReturn;
  }

  /**
   * Finds all of the tasks with a specified id.
   *
   * @param user - the user the task belongs to.
   * @param id   - the id of the task.
   * @return a task.
   * @throws SQLException if we can't select the task.
   */
  public Task getTaskByID(User user, int id) throws SQLException {
    PreparedStatement prep = conn.prepareStatement("SELECT DISTINCT * FROM tasks "
        + "WHERE tasks.EXTERNAL_ID = ? AND tasks.id = ?;");
    prep.setString(1, user.getExternalId());
    prep.setInt(2, id);
    ResultSet rs = prep.executeQuery();
    List<Task> taskList = makeTasks(rs);
    if (taskList.size() < 1) {
      return null;
    }
    return taskList.get(0);
  }

  /**
   * Updates the duration of a task in the database.
   *
   * @param user    - the user the task belongs to.
   * @param id      - the id of the task.
   * @param newTime - the new duration of the task.
   * @throws SQLException if the task can't be updated.
   */
  public void updateTaskTime(User user, int id, int newTime) throws SQLException {
    PreparedStatement prep = conn.prepareStatement("UPDATE tasks "
        + "SET duration = ? "
        + "WHERE id = ? AND EXTERNAL_ID = ?;");
    prep.setInt(1, newTime);
    prep.setInt(2, id);
    prep.setString(3, user.getExternalId());
    prep.executeUpdate();
    prep.close();
  }

  /**
   * Updates a task to be completed.
   *
   * @param user - the user of the block.
   * @param task - the task of the block.
   * @throws SQLException if the task can't be updated.
   */
  public void updateTaskCompleted(User user, Task task) throws SQLException {
    PreparedStatement prep = conn.prepareStatement("UPDATE tasks "
        + "SET completed = 1 "
        + "WHERE id = ? AND EXTERNAL_ID = ?;");
    prep.setInt(1, task.getTaskID());
    prep.setString(2, user.getExternalId());
    prep.executeUpdate();
    prep.close();
  }

  /**
   * Updates a task to be incomplete.
   *
   * @param user - the user of the block.
   * @param task - the task of the block.
   * @throws SQLException if the task can't be updated.
   */
  public void updateTaskIncomplete(User user, Task task) throws SQLException {
    PreparedStatement prep = conn.prepareStatement("UPDATE tasks "
        + "SET completed = 0 "
        + "WHERE id = ? AND EXTERNAL_ID = ?;");
    prep.setInt(1, task.getTaskID());
    prep.setString(2, user.getExternalId());
    prep.executeUpdate();
    prep.close();
  }


  /**
   * Given a user, finds all the scheduled tasks for that user. As of right now, partially scheduled
   * tasks are considered scheduled.
   *
   * @param user - the user we want to find tasks for.
   * @return a list of tasks, these are tasks that have been scheduled.
   * @throws SQLException if the tasks can't be selected.
   */
  public List<Task> allScheduledTasks(User user) throws SQLException, ParseException {
    PreparedStatement prep = conn.prepareStatement("SELECT DISTINCT * FROM tasks "
        + "WHERE id IN (SELECT task_id FROM scheduled)"
        + "AND EXTERNAL_ID = ?;");
    prep.setString(1, user.getExternalId());
    ResultSet rs = prep.executeQuery();
    List<Task> toReturn = makeTasks(rs);
    rs.close();
    prep.close();
    return toReturn;
  }

  /**
   * Given a user, finds all the unscheduled tasks for that user.
   *
   * @param user - the user we want to find tasks for.
   * @return a list of tasks, these are tasks that have not been scheduled yet.
   * @throws SQLException if the tasks can't be selected.
   */
  public List<Task> allUnscheduledTasks(User user) throws SQLException, ParseException {
    PreparedStatement prep = conn.prepareStatement("SELECT DISTINCT * FROM tasks "
            + "WHERE id NOT IN (SELECT task_id FROM scheduled)"
            + "AND scheduled = 0 AND EXTERNAL_ID = ? AND completed = 0;");
    prep.setString(1, user.getExternalId());
    ResultSet rs = prep.executeQuery();
    List<Task> toReturn = makeTasks(rs);
    rs.close();
    prep.close();
    return toReturn;
  }

  /**
   * Finds all of the tasks that have been scheduled that there does not have time to complete.
   *
   * @param user - the user.
   * @return the list of tasks
   * @throws SQLException if unable to find tasks
   */
  public List<Task> allScheduledTasksNotTime(User user) throws SQLException {
    PreparedStatement prep = conn.prepareStatement("SELECT DISTINCT * FROM tasks "
        + "WHERE id NOT IN (SELECT task_id FROM scheduled) "
        + "AND EXTERNAL_ID = ? AND scheduled = 1 AND completed = 0;");
    prep.setString(1, user.getExternalId());
    ResultSet rs = prep.executeQuery();
    List<Task> toReturn = makeTasks(rs);
    rs.close();
    prep.close();
    return toReturn;
  }

  /**
   * Finds all of the tasks that have been scheduled that there is some time to complete.
   *
   * @param user - the user.
   * @return the list of tasks
   * @throws SQLException if unable to find tasks
   */
  public List<Task> allScheduledTasksSomeTime(User user) throws SQLException {
    PreparedStatement prep = conn.prepareStatement("SELECT DISTINCT * FROM tasks "
        + "WHERE (duration > (SELECT sum((end - start) / 60000) FROM scheduled "
        + "WHERE task_id = tasks.id) AND tasks.EXTERNAL_ID = ?) AND completed = 0;");
    prep.setString(1, user.getExternalId());
    ResultSet rs = prep.executeQuery();
    List<Task> toReturn = makeTasks(rs);
    rs.close();
    prep.close();
    return toReturn;
  }

  /**
   * Finds all of the tasks that have been scheduled with sufficient time.
   *
   * @param user - the user.
   * @return the list of tasks
   * @throws SQLException if unable to find tasks
   */
  public List<Task> allScheduledTasksAllTime(User user) throws SQLException {
    PreparedStatement prep = conn.prepareStatement("SELECT DISTINCT * FROM tasks "
        + "WHERE (duration <= (SELECT sum((end - start) / 60000) FROM scheduled "
        + "WHERE task_id = tasks.id) AND tasks.EXTERNAL_ID = ?) OR completed = 1;");
    prep.setString(1, user.getExternalId());
    ResultSet rs = prep.executeQuery();
    List<Task> toReturn = makeTasks(rs);
    rs.close();
    prep.close();
    return toReturn;
  }

  /**
   * Add a scheduled task.
   *
   * @param user  - the user this task belongs to.
   * @param block - an assigned task block.
   * @throws SQLException if the entry in the scheduled table can't be inserted.
   */
  public void addScheduled(User user, AssignedTaskBlock block) throws SQLException {
    PreparedStatement prep = conn.prepareStatement(
        "INSERT INTO scheduled"
            + "(task_id, EXTERNAL_ID, deadline, start, end, EVENT_ID, completed, title) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
    prep.setInt(1, block.getTaskID());
    prep.setString(2, user.getExternalId());
    prep.setLong(3, block.getTaskDeadline().getTime());
    prep.setLong(4, block.getStartTime().getTime());
    prep.setLong(5, block.getEndTime().getTime());
    prep.setString(6, null);
    prep.setInt(7, 0);
    prep.setString(8, block.getTitle());
    prep.executeUpdate();
    prep.close();
  }

  /**
   * Get all scheduled task blocks.
   *
   * @param user - the user to get the tasks for.
   * @return a list of AssignedTaskBlocks.
   * @throws SQLException if we can't select the scheduled task blocks.
   */
  public List<AssignedTaskBlock> allScheduledTaskBlocks(User user) throws SQLException {
    PreparedStatement prep = conn.prepareStatement(
        "SELECT task_id, deadline, start, end, title, completed FROM scheduled "
            + "WHERE EXTERNAL_ID = ?;");
    prep.setString(1, user.getExternalId());
    ResultSet rs = prep.executeQuery();
    List<AssignedTaskBlock> toReturn = makeAssignedTaskBlocks(rs);
    rs.close();
    prep.close();
    return toReturn;
  }

  /**
   * Given a result set, makes a list of assignedTaskBlocks from that set.
   *
   * @param rs - the result set containing all the columns from the scheduled table.
   * @return a list of tasks.
   * @throws SQLException if the list of tasks can't be created.
   */
  public List<AssignedTaskBlock> makeAssignedTaskBlocks(ResultSet rs) throws SQLException {
    List<AssignedTaskBlock> toReturn = new ArrayList<>();
    while (rs.next()) {
      AssignedTaskBlock toAdd = new AssignedTaskBlock(
          rs.getInt(1),
          new Date(rs.getLong(2)),
          //Number of years since 1900?
          new Date(rs.getLong(3)),
          new Date(rs.getLong(4)),
          false,
          rs.getString(5),
          rs.getInt(6) == 1
      );
      toReturn.add(toAdd);
    }
    return toReturn;
  }

  /**
   * Updates a scheduled block to be completed.
   *
   * @param user - the user of the block.
   * @param task - the task of the block.
   * @throws SQLException if the scheduled task block can't be updated.
   */
  public void updateScheduledBlockCompleted(User user, AssignedTaskBlock task) throws SQLException {

    PreparedStatement prep = conn.prepareStatement("UPDATE scheduled "
        + "SET completed = 1 "
        + "WHERE task_id = ? AND start = ? AND end = ? AND EXTERNAL_ID = ?;");
    prep.setInt(1, task.getTaskID());
    prep.setLong(2, task.getStartTime().getTime());
    prep.setLong(3, task.getEndTime().getTime());
    prep.setString(4, user.getExternalId());
    prep.executeUpdate();
    prep.close();
  }

  /**
   * Updates a scheduled block to be completed.
   *
   * @param user - the user of the block.
   * @param task - the task of the block.
   * @throws SQLException if the scheduled block can't be updated.
   */
  public void updateAllScheduledBlocksCompleted(User user, Task task) throws SQLException {
    PreparedStatement prep = conn.prepareStatement("UPDATE scheduled "
        + "SET completed = 1 "
        + "WHERE task_id = ? AND EXTERNAL_ID = ?;");
    prep.setInt(1, task.getTaskID());
    prep.setString(2, user.getExternalId());
    prep.executeUpdate();
    prep.close();
  }

  /**
   * Updates a scheduled block to be completed.
   *
   * @param user - the user of the block.
   * @param task - the task of the block.
   * @throws SQLException if the scheduled block can't be updated.
   */
  public void updateAllScheduledBlocksIncomplete(User user, Task task) throws SQLException {
    PreparedStatement prep = conn.prepareStatement("UPDATE scheduled "
        + "SET completed = 0 "
        + "WHERE task_id = ? AND EXTERNAL_ID = ?;");
    prep.setInt(1, task.getTaskID());
    prep.setString(2, user.getExternalId());
    prep.executeUpdate();
    prep.close();
  }


  /**
   * Updates a scheduled block to be incomplete.
   *
   * @param user - the user of the block.
   * @param task - the task of the block.
   * @throws SQLException if the scheduled block can't be updated.
   */
  public void updateScheduledBlockIncomplete(User user, AssignedTaskBlock task)
      throws SQLException {
    PreparedStatement prep = conn.prepareStatement("UPDATE scheduled "
        + "SET completed = 0 "
        + "WHERE task_id = ? AND start = ? AND end = ? AND EXTERNAL_ID = ?;");
    prep.setInt(1, task.getTaskID());
    prep.setLong(2, task.getStartTime().getTime());
    prep.setLong(3, task.getEndTime().getTime());
    prep.setString(4, user.getExternalId());
    prep.executeUpdate();
    prep.close();
  }

  /**
   * Clears all of a users scheduled tasks.
   *
   * @param user - the user to clear.
   * @throws SQLException if the scheduled table can't be cleared.
   */
  public void clearScheduledTasks(User user) throws SQLException {
    PreparedStatement prep = conn.prepareStatement("DELETE FROM scheduled WHERE EXTERNAL_ID=?");
    prep.setString(1, user.getExternalId());
    prep.executeUpdate();
  }

  /**
   * Clears all the tables in the database.
   *
   * @throws SQLException if the database can't be cleared.
   */
  public void clearAll() throws SQLException {
    PreparedStatement prep = conn.prepareStatement("DELETE FROM users;");
    prep.executeUpdate();
    prep = conn.prepareStatement("DELETE FROM tasks;");
    prep.executeUpdate();
    prep = conn.prepareStatement("DELETE FROM scheduled;");
    prep.executeUpdate();
    prep = conn.prepareStatement("DELETE FROM salts;");
    prep.executeUpdate();
    prep.close();
  }

  public void close() throws SQLException {
    conn.close();
  }
}
