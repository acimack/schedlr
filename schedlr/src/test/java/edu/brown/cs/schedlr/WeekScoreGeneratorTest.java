package edu.brown.cs.schedlr;

import edu.brown.cs.schedlr.algorithm.*;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static edu.brown.cs.schedlr.algorithm.ScheduleConstants.ONE_MINUTE_IN_MILLIS;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

public class WeekScoreGeneratorTest {

  @Test
  public void testGenerateWeekScore() {

    // create dates:
    Calendar calendar = Calendar.getInstance();
    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    try {

      Date today = formatter.parse(formatter.format(calendar.getTime()));
      calendar.add(Calendar.DAY_OF_YEAR, 1);
      Date tomorrow = formatter.parse(formatter.format(calendar.getTime()));
      calendar.add(Calendar.DAY_OF_YEAR, 1);
      Date tomorrow2 = formatter.parse(formatter.format(calendar.getTime()));
      calendar.add(Calendar.DAY_OF_YEAR, 1);
      Date tomorrow3 = formatter.parse(formatter.format(calendar.getTime()));
      calendar.add(Calendar.DAY_OF_YEAR, 1);
      Date tomorrow4 = formatter.parse(formatter.format(calendar.getTime()));
      calendar.add(Calendar.DAY_OF_YEAR, 1);
      Date tomorrow5 = formatter.parse(formatter.format(calendar.getTime()));
      calendar.add(Calendar.DAY_OF_YEAR, 1);

      Date today8am = new Date(today.getTime() + (480 * ONE_MINUTE_IN_MILLIS));
      Date today830am = new Date(today.getTime() + (510 * ONE_MINUTE_IN_MILLIS));
      Date today9am = new Date(today.getTime() + (540 * ONE_MINUTE_IN_MILLIS));
      Date today10am = new Date(today.getTime() + (600 * ONE_MINUTE_IN_MILLIS));
      Date today5pm = new Date(today.getTime() + (1020 * ONE_MINUTE_IN_MILLIS));

      Date tomorrow8am = new Date(tomorrow.getTime() + (480 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow9am = new Date(tomorrow.getTime() + (540 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow10am = new Date(tomorrow.getTime() + (600 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow2pm = new Date(tomorrow.getTime() + (840 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow5pm = new Date(tomorrow.getTime() + (1020 * ONE_MINUTE_IN_MILLIS));

      Date tomorrow28am = new Date(tomorrow2.getTime() + (480 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow2815am = new Date(tomorrow2.getTime() + (495 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow29am = new Date(tomorrow2.getTime() + (540 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow210am = new Date(tomorrow2.getTime() + (600 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow22pm = new Date(tomorrow2.getTime() + (840 * ONE_MINUTE_IN_MILLIS));

      Date tomorrow38am = new Date(tomorrow3.getTime() + (480 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow312pm = new Date(tomorrow3.getTime() + (720 * ONE_MINUTE_IN_MILLIS));

      Date tomorrow4830am = new Date(tomorrow4.getTime() + (510 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow42pm = new Date(tomorrow4.getTime() + (840 * ONE_MINUTE_IN_MILLIS));

      Date tomorrow5830am = new Date(tomorrow5.getTime() + (510 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow52pm = new Date(tomorrow5.getTime() + (840 * ONE_MINUTE_IN_MILLIS));


      // test 1:
      List<Task> taskBlockList = new LinkedList<>();
      List<BusyBlock> eventBlockList = new LinkedList<>();

      BusyBlock event1 = new BusyBlock(today8am, today10am);
      BusyBlock event2 = new BusyBlock(tomorrow9am, tomorrow10am);
      BusyBlock event3 = new BusyBlock(tomorrow29am, tomorrow210am);

      eventBlockList.add(event1);
      eventBlockList.add(event2);
      eventBlockList.add(event3);

      taskBlockList.add(new Task(1,30, tomorrow3, "task 1"));
      taskBlockList.add(new Task(2,30, tomorrow3, "task 2"));

      ScheduleInfo sched = new ScheduleInfo(taskBlockList, eventBlockList, 480, 600);

      List<Integer> tasksPerDay = new LinkedList<>();
      tasksPerDay.add(0);
      tasksPerDay.add(1);
      tasksPerDay.add(2);

      int freeTimeRemaining = 0;
      if (!sched.getDailyFreeTime().isEmpty()) {
        freeTimeRemaining = sched.getDailyFreeTime().get(0);
      }
      int taskTimeRemaining = sched.getTotalTaskTimeInitial();

      WeekScore expected = new WeekScore(tasksPerDay, 0.0);
      WeekScore out = WeekScoreGenerator.generateWeekScore(0, freeTimeRemaining, taskTimeRemaining, 0, sched);

      assertEquals(expected.getScore(), out.getScore());
      assertEquals(expected.getTasksPerDay(), out.getTasksPerDay());


      // test 2:
      taskBlockList = new LinkedList<>();
      eventBlockList = new LinkedList<>();

      BusyBlock event4 = new BusyBlock(today9am, today5pm);
      BusyBlock event5 = new BusyBlock(tomorrow9am, tomorrow5pm);

      eventBlockList.add(event4);
      eventBlockList.add(event5);

      taskBlockList.add(new Task(1, 60, tomorrow5, "task 1"));
      taskBlockList.add(new Task(2,130, tomorrow5, "task 2"));
      taskBlockList.add(new Task(3, 80, tomorrow5, "task 3"));

      tasksPerDay = new LinkedList<>();
      tasksPerDay.add(0);
      tasksPerDay.add(0);
      tasksPerDay.add(2);
      tasksPerDay.add(3);
      tasksPerDay.add(4);

      sched = new ScheduleInfo(new LinkedList<>(taskBlockList), new LinkedList<>(eventBlockList), 540, 1020);
      expected = new WeekScore(tasksPerDay, 0.68301270189);

      freeTimeRemaining = 0;
      if (!sched.getDailyFreeTime().isEmpty()) {
        freeTimeRemaining = sched.getDailyFreeTime().get(0);
      }
      taskTimeRemaining = sched.getTotalTaskTimeInitial();

      out = WeekScoreGenerator.generateWeekScore(0, freeTimeRemaining, taskTimeRemaining, 0, sched);

      assertEquals(expected.getScore(), out.getScore(), .00000001);
      assertEquals(expected.getTasksPerDay(), out.getTasksPerDay());


      // test 3:
      taskBlockList = new LinkedList<>();
      eventBlockList = new LinkedList<>();

      BusyBlock event6 = new BusyBlock(today8am, today830am);
      BusyBlock event7 = new BusyBlock(today830am, today9am);
      BusyBlock event8 = new BusyBlock(tomorrow8am, tomorrow9am);

      eventBlockList.add(event6);
      eventBlockList.add(event7);
      eventBlockList.add(event8);

      taskBlockList.add(new Task(1, 30, tomorrow4, "task 1"));
      taskBlockList.add(new Task(2, 30, tomorrow4, "task 2"));

      tasksPerDay = new LinkedList<>();
      tasksPerDay.add(0);
      tasksPerDay.add(0);
      tasksPerDay.add(1);
      tasksPerDay.add(2);

      sched = new ScheduleInfo(new LinkedList<>(taskBlockList), new LinkedList<>(eventBlockList), 480, 540);
      expected = new WeekScore(tasksPerDay, 0.0);

      freeTimeRemaining = 0;
      if (!sched.getDailyFreeTime().isEmpty()) {
        freeTimeRemaining = sched.getDailyFreeTime().get(0);
      }
      taskTimeRemaining = sched.getTotalTaskTimeInitial();

      out = WeekScoreGenerator.generateWeekScore(0, freeTimeRemaining, taskTimeRemaining, 0, sched);

      assertEquals(expected.getScore(), out.getScore(), .00000001);
      assertEquals(expected.getTasksPerDay(), out.getTasksPerDay());


      // test 4:
      taskBlockList = new LinkedList<>();
      eventBlockList = new LinkedList<>();

      BusyBlock allDayToday = new BusyBlock(today8am, today5pm);
      BusyBlock event9 = new BusyBlock(tomorrow9am, tomorrow2pm);
      BusyBlock event10 = new BusyBlock(tomorrow28am, tomorrow22pm);
      BusyBlock event11 = new BusyBlock(tomorrow38am, tomorrow312pm);
      BusyBlock event12 = new BusyBlock(tomorrow4830am, tomorrow42pm);
      BusyBlock event13 = new BusyBlock(tomorrow5830am, tomorrow52pm);

      eventBlockList.add(allDayToday);
      eventBlockList.add(event9);
      eventBlockList.add(event10);
      eventBlockList.add(event11);
      eventBlockList.add(event12);
      eventBlockList.add(event13);

      taskBlockList.add(new Task(1, 30, tomorrow2, "task 1"));
      taskBlockList.add(new Task(2,15, tomorrow2, "task 2"));
      taskBlockList.add(new Task(3, 100, tomorrow4, "task 3"));
      taskBlockList.add(new Task(4,15, tomorrow4, "task 4"));
      taskBlockList.add(new Task(5,30, tomorrow5, "task 5"));

      tasksPerDay = new LinkedList<>();
      tasksPerDay.add(0);
      tasksPerDay.add(2);
      tasksPerDay.add(0);
      tasksPerDay.add(4);
      tasksPerDay.add(5);

      sched = new ScheduleInfo(new LinkedList<>(taskBlockList), new LinkedList<>(eventBlockList), 480, 840);
      expected = new WeekScore(tasksPerDay, 0.9334596211);

      freeTimeRemaining = 0;
      if (!sched.getDailyFreeTime().isEmpty()) {
        freeTimeRemaining = sched.getDailyFreeTime().get(0);
      }
      taskTimeRemaining = sched.getTotalTaskTimeInitial();

      out = WeekScoreGenerator.generateWeekScore(0, freeTimeRemaining, taskTimeRemaining, 0, sched);

      assertEquals(expected.getScore(), out.getScore(), .00000001);
      assertEquals(expected.getTasksPerDay(), out.getTasksPerDay());


      // task 5:
      taskBlockList = new LinkedList<>();
      eventBlockList = new LinkedList<>();

      eventBlockList.add(allDayToday);
      eventBlockList.add(event9);
      eventBlockList.add(event10);
      eventBlockList.add(event11);
      eventBlockList.add(event12);
      eventBlockList.add(event13);

      taskBlockList.add(new Task(1,30, tomorrow2, "task 1"));
      taskBlockList.add(new Task(2,15, tomorrow2, "task 2"));
      taskBlockList.add(new Task(3,15, tomorrow3, "task 3"));
      taskBlockList.add(new Task(4,100, tomorrow4, "task 4"));
      taskBlockList.add(new Task(5, 15, tomorrow4, "task 5"));
      taskBlockList.add(new Task(6, 30, tomorrow5, "task 6"));

      tasksPerDay = new LinkedList<>();
      tasksPerDay.add(0);
      tasksPerDay.add(3);
      tasksPerDay.add(0);
      tasksPerDay.add(5);
      tasksPerDay.add(6);

      sched = new ScheduleInfo(new LinkedList<>(taskBlockList), new LinkedList<>(eventBlockList), 480, 840);
      expected = new WeekScore(tasksPerDay, 0.44223732088);

      freeTimeRemaining = 0;
      if (!sched.getDailyFreeTime().isEmpty()) {
        freeTimeRemaining = sched.getDailyFreeTime().get(0);
      }
      taskTimeRemaining = sched.getTotalTaskTimeInitial();

      out = WeekScoreGenerator.generateWeekScore(0, freeTimeRemaining, taskTimeRemaining, 0, sched);

      assertEquals(expected.getScore(), out.getScore(), .00000001);
      assertEquals(expected.getTasksPerDay(), out.getTasksPerDay());


      // test 6:
      taskBlockList = new LinkedList<>();
      eventBlockList = new LinkedList<>();

      eventBlockList.add(allDayToday);
      eventBlockList.add(event9);
      eventBlockList.add(event10);
      eventBlockList.add(event11);
      eventBlockList.add(event12);
      eventBlockList.add(event13);

      taskBlockList.add(new Task(1, 45, tomorrow2, "task 1"));
      taskBlockList.add(new Task(2, 15, tomorrow2, "task 2"));
      taskBlockList.add(new Task(3, 100, tomorrow4, "task 3"));
      taskBlockList.add(new Task(4, 50, tomorrow5, "task 4"));

      tasksPerDay = new LinkedList<>();
      tasksPerDay.add(0);
      tasksPerDay.add(2);
      tasksPerDay.add(0);
      tasksPerDay.add(4);
      tasksPerDay.add(5);

      sched = new ScheduleInfo(new LinkedList<>(taskBlockList), new LinkedList<>(eventBlockList), 480, 840);
      expected = new WeekScore(tasksPerDay, 0.0);

      freeTimeRemaining = 0;
      if (!sched.getDailyFreeTime().isEmpty()) {
        freeTimeRemaining = sched.getDailyFreeTime().get(0);
      }
      taskTimeRemaining = sched.getTotalTaskTimeInitial();

      out = WeekScoreGenerator.generateWeekScore(0, freeTimeRemaining, taskTimeRemaining, 0, sched);

      assertEquals(expected.getScore(), out.getScore(), .00000001);
      assertEquals(expected.getTasksPerDay(), out.getTasksPerDay());


      // test 7:
      taskBlockList = new LinkedList<>();
      eventBlockList = new LinkedList<>();

      eventBlockList.add(allDayToday);
      eventBlockList.add(event9);
      eventBlockList.add(event10);
      eventBlockList.add(event11);
      eventBlockList.add(event12);
      eventBlockList.add(event13);

      taskBlockList.add(new Task(1,45, tomorrow2, "task 1"));
      taskBlockList.add(new Task(2,15, tomorrow2, "task 2"));
      taskBlockList.add(new Task(3,100, tomorrow4, "task 3"));
      taskBlockList.add(new Task(4,60, tomorrow5, "task 4"));

      tasksPerDay = new LinkedList<>();
      tasksPerDay.add(0);
      tasksPerDay.add(2);
      tasksPerDay.add(0);
      tasksPerDay.add(4);
      tasksPerDay.add(5);

      sched = new ScheduleInfo(new LinkedList<>(taskBlockList), new LinkedList<>(eventBlockList), 480, 840);
      expected = new WeekScore(tasksPerDay, Double.POSITIVE_INFINITY);

      freeTimeRemaining = 0;
      if (!sched.getDailyFreeTime().isEmpty()) {
        freeTimeRemaining = sched.getDailyFreeTime().get(0);
      }
      taskTimeRemaining = sched.getTotalTaskTimeInitial();

      out = WeekScoreGenerator.generateWeekScore(0, freeTimeRemaining, taskTimeRemaining, 0, sched);

      assertEquals(expected.getScore(), out.getScore(), .00000001);
      assertEquals(expected.getTasksPerDay(), out.getTasksPerDay());


      // test 8:
      taskBlockList = new LinkedList<>();
      eventBlockList = new LinkedList<>();

      BusyBlock event14 = new BusyBlock(tomorrow2815am, tomorrow29am);

      eventBlockList.add(allDayToday);
      eventBlockList.add(event14);

      taskBlockList.add(new Task(1,75, tomorrow3, "task 1"));

      tasksPerDay = new LinkedList<>();
      tasksPerDay.add(0);
      tasksPerDay.add(1);
      tasksPerDay.add(2);

      sched = new ScheduleInfo(new LinkedList<>(taskBlockList), new LinkedList<>(eventBlockList), 480, 540);
      expected = new WeekScore(tasksPerDay, 0.0);

      freeTimeRemaining = 0;
      if (!sched.getDailyFreeTime().isEmpty()) {
        freeTimeRemaining = sched.getDailyFreeTime().get(0);
      }
      taskTimeRemaining = sched.getTotalTaskTimeInitial();

      out = WeekScoreGenerator.generateWeekScore(0, freeTimeRemaining, taskTimeRemaining, 0, sched);

      assertEquals(expected.getScore(), out.getScore(), .00000001);
      assertEquals(expected.getTasksPerDay(), out.getTasksPerDay());

    } catch (ParseException e) {
      fail();
    }
  }
}
