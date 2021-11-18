package edu.brown.cs.schedlr;

import edu.brown.cs.schedlr.algorithm.AssignedTaskBlock;
import edu.brown.cs.schedlr.algorithm.Task;
import edu.brown.cs.schedlr.database.Query;
import edu.brown.cs.schedlr.database.Singleton;
import edu.brown.cs.schedlr.run.User;
import org.checkerframework.checker.units.qual.A;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class QueryTest {
  private final Singleton database = Singleton.getInstance();
  private Query query = null;

  public QueryTest() { }

  @Before
  public void open() throws SQLException, ClassNotFoundException {
    database.setQuery(new Query("data/test.sqlite3"));
    query = database.getQuery();
    query.clearAll();
  }

  @After
  public void close() throws SQLException {
    query.clearAll();
    query.close();
  }

  @Test
  public void testGetSaltElse() throws SQLException {
    String newSalt = query.getSaltElse("test");
    String sameSalt = query.getSaltElse("test");
    assertEquals(newSalt, sameSalt);
    String differentSalt = query.getSaltElse("test2");
    assertNotEquals(newSalt, differentSalt);
    assertNotEquals(sameSalt,differentSalt);
    query.clearAll();
  }

  @Test
  public void testAddUser() throws SQLException {
    User user = new User("test_id", "test_name", "test@email", 480, 720, "test_cal_ID");
    query.addUser(user);
    User toTest = query.selectUser(user.getExternalId());
    assertEquals(user, toTest);
  }

  @Test
  public void testDeleteUser() throws SQLException {
    User user = new User("test_id", "test_name", "test@email", 480, 720, "test_cal_ID");
    query.addUser(user);
    User toTest = query.selectUser(user.getExternalId());
    assertEquals(user, toTest);

    query.deleteUser(user);
    toTest = query.selectUser(user.getExternalId());
    assertNotEquals(user, toTest);
    assertNull(toTest);
  }

  @Test
  public void testUpdateUser() throws SQLException {
    User user = new User("test_id", "test_name", "test@email", 480, 720, "test_cal_ID");
    query.addUser(user);
    User toTest = query.selectUser(user.getExternalId());
    assertEquals(user, toTest);

    User toUpdate = new User("test_id", "new_name", "test@email", 480, 720, "test_cal_ID");
    query.updateUser(toUpdate);
    toTest = query.selectUser(user.getExternalId());
    assertNotEquals(user, toTest);
    assertEquals("new_name", toTest.getUsername());
  }

  @Test
  public void testSelectUser() throws SQLException {
    User user = new User("test_id", "test_name", "test@email", 480, 720, "test_cal_ID");
    query.addUser(user);
    User user2 = new User("test_id2", "test_name2", "test@email2", 480, 720, "test_cal_ID2");
    query.addUser(user2);
    User toTest = query.selectUser(user.getExternalId());
    assertEquals(user, toTest);
    toTest = query.selectUser(user2.getExternalId());
    assertEquals(user2, toTest);
  }

  @Test
  public void testUpdateDailyTimes() throws SQLException {
    User user = new User("test_id", "test_name", "test@email", 480, 720, "test_cal_ID");
    query.addUser(user);
    query.updateDailyTimes(user, 0, 0);
    User toTest = query.selectUser(user.getExternalId());
    assertEquals(0, toTest.getStart());
    assertEquals(0, toTest.getEnd());
  }

  @Test
  public void testAddTask() throws SQLException {
    User user = new User("test_id", "test_name", "test@email", 480, 720, "test_cal_ID");
    query.addUser(user);
    User user2 = new User("test_id2", "test_name2", "test@email2", 480, 720, "test_cal_ID2");
    query.addUser(user2);
    query.addTask(user, 60, new Date(2001, 3, 30), "TEST", 0);
    List<Task> tasks = query.allTasks(user);
    List<Task> empty = query.allTasks(user2);
    assertEquals(1, tasks.size());
    assertEquals("TEST", tasks.get(0).getTitle());
    assertEquals(60, tasks.get(0).getTimeToComplete());
    query.addTask(user, 59, new Date(2001, 3, 30), "TEST2", 0);
    tasks = query.allTasks(user);
    assertEquals(2, tasks.size());
    assertTrue(empty.isEmpty());
  }

  @Test
  public void testDeleteTask() throws SQLException {
    User user = new User("test_id", "test_name", "test@email", 480, 720, "test_cal_ID");
    query.addUser(user);
    User user2 = new User("test_id2", "test_name2", "test@email2", 480, 720, "test_cal_ID2");
    query.addUser(user2);
    query.addTask(user, 60, new Date(2001, 3, 30), "TEST", 0);
    List<Task> tasks = query.allTasks(user);
    List<Task> empty = query.allTasks(user2);
    assertEquals(1, tasks.size());
    assertEquals("TEST", tasks.get(0).getTitle());
    assertEquals(60, tasks.get(0).getTimeToComplete());
    query.addTask(user, 59, new Date(2001, 3, 30), "TEST2", 0);
    tasks = query.allTasks(user);
    assertEquals(2, tasks.size());
    query.deleteTask(user, tasks.get(0).getTaskID());
    tasks = query.allTasks(user);
    assertEquals(1, tasks.size());
    query.deleteTask(user2, 0);
    empty = query.allTasks(user2);
    assertTrue(empty.isEmpty());
  }

  @Test
  public void testUpdateTask() throws SQLException {
    User user = new User("test_id", "test_name", "test@email", 480, 720, "test_cal_ID");
    query.addUser(user);
    query.addTask(user, 60, new Date(2001, 3, 30), "TEST", 0);
    List<Task> tasks = query.allTasks(user);
    query.updateTask(user, new Task(tasks.get(0).getTaskID(), 30, new Date(2001, 3, 30), "TEST"));
    tasks = query.allTasks(user);
    assertEquals(30, tasks.get(0).getTimeToComplete());
  }

  @Test
  public void testGetTaskById() throws SQLException {
    User user = new User("test_id", "test_name", "test@email", 480, 720, "test_cal_ID");
    query.addUser(user);
    query.addTask(user, 60, new Date(2001, 3, 30), "TEST", 0);
    List<Task> tasks = query.allTasks(user);
    Task toTest = query.getTaskByID(user, tasks.get(0).getTaskID());
    assertEquals(tasks.get(0).getTitle(), toTest.getTitle());
  }

  @Test
  public void testUpdateTaskTime() throws SQLException {
    User user = new User("test_id", "test_name", "test@email", 480, 720, "test_cal_ID");
    query.addUser(user);
    query.addTask(user, 60, new Date(2001, 3, 30), "TEST", 0);
    List<Task> tasks = query.allTasks(user);
    query.updateTaskTime(user, tasks.get(0).getTaskID(), 30);
    tasks = query.allTasks(user);
    assertEquals(30, tasks.get(0).getTimeToComplete());
  }

  @Test
  public void testUpdateTaskCompleted() throws SQLException {
    User user = new User("test_id", "test_name", "test@email", 480, 720, "test_cal_ID");
    query.addUser(user);
    query.addTask(user, 60, new Date(2001, 3, 30), "TEST", 0);
    List<Task> tasks = query.allTasks(user);
    query.updateTaskCompleted(user, tasks.get(0));
    tasks = query.allTasks(user);
    assertTrue(tasks.get(0).isComplete());
  }

  @Test
  public void testUpdateTaskIncomplete() throws SQLException {
    User user = new User("test_id", "test_name", "test@email", 480, 720, "test_cal_ID");
    query.addUser(user);
    query.addTask(user,
        60, new Date(2001, 3, 30),
        "TEST",
        1);
    List<Task> tasks = query.allTasks(user);
    query.updateTaskIncomplete(user, tasks.get(0));
    tasks = query.allTasks(user);
    assertFalse(tasks.get(0).isComplete());
  }

  @Test
  public void testAllIncompleteTasks() throws SQLException {
    User user = new User("test_id", "test_name", "test@email", 480, 720, "test_cal_ID");
    query.addUser(user);
    query.addTask(user,
        60, new Date(2001, 3, 30),
        "TEST",
        1);
    query.addTask(user,
        60, new Date(2001, 3, 30),
        "TEST2",
        0);
    List<Task> tasks = query.allIncompleteTasks(user);
    assertEquals(1, tasks.size());
    assertEquals("TEST2", tasks.get(0).getTitle());
  }

  @Test
  public void testAllUnscheduledTasks() throws SQLException, ParseException {
    User user = new User("test_id", "test_name", "test@email", 480, 720, "test_cal_ID");
    query.addUser(user);
    query.addTask(user,
        60, new Date(2001, 3, 30),
        "TEST",
        1);
    query.addTask(user,
        60, new Date(2001, 3, 30),
        "TEST2",
        0);
    query.setAllTasksSchedule(user);
    List<Task> tasks = query.allUnscheduledTasks(user);
    assertEquals(0, tasks.size());
  }

  @Test
  public void testAddScheduled() throws SQLException {
    User user = new User("test_id", "test_name", "test@email", 480, 720, "test_cal_ID");
    query.addUser(user);
    query.addTask(user, 60, new Date(2001, 3, 30), "TEST", 1);
    List<Task> tasks = query.allTasks(user);
    AssignedTaskBlock block1 = new AssignedTaskBlock(
        tasks.get(0).getTaskID(),
        new Date(2001, 3, 30),
        new Date(2001, 4, 1),
        new Date(2001, 4, 2),
        false,"test_block",
        false);
    AssignedTaskBlock block2 = new AssignedTaskBlock(
        tasks.get(0).getTaskID(),
        new Date(2001, 3, 30),
        new Date(2001, 4, 1),
        new Date(2001, 4, 2),
        false,"test_block2",
        false);
    query.addScheduled(user, block1);
    List<AssignedTaskBlock> blocks = query.allScheduledTaskBlocks(user);
    assertEquals(1, blocks.size());
    assertEquals("test_block", blocks.get(0).getTitle());
    query.addScheduled(user, block2);
    blocks = query.allScheduledTaskBlocks(user);
    assertEquals(2, blocks.size());
  }
}
