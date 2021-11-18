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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TaskTimeAssignerTest {

  @Test
  public void testTaskTimeAssigner() {
    // create dates:
    Calendar calendar = Calendar.getInstance();
    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    try {

      Date today = formatter.parse(formatter.format(calendar.getTime()));
      calendar.add(Calendar.DAY_OF_YEAR, -1);
      Date yesterday = formatter.parse(formatter.format(calendar.getTime()));
      calendar.add(Calendar.DAY_OF_YEAR, 2);
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

      Date yesterday8am = new Date(yesterday.getTime() +  (480 * ONE_MINUTE_IN_MILLIS));
      Date yesterday9am = new Date(yesterday.getTime() +  (540 * ONE_MINUTE_IN_MILLIS));

      Date today8am = new Date(today.getTime() + (480 * ONE_MINUTE_IN_MILLIS));
      Date today830am = new Date(today.getTime() + (510 * ONE_MINUTE_IN_MILLIS));
      Date today9am = new Date(today.getTime() + (540 * ONE_MINUTE_IN_MILLIS));
      Date today10am = new Date(today.getTime() + (600 * ONE_MINUTE_IN_MILLIS));
      Date today5pm = new Date(today.getTime() + (1020 * ONE_MINUTE_IN_MILLIS));

      Date tomorrow8am = new Date(tomorrow.getTime() + (480 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow9am = new Date(tomorrow.getTime() + (540 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow10am = new Date(tomorrow.getTime() + (600 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow12pm = new Date(tomorrow.getTime() + (720 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow2pm = new Date(tomorrow.getTime() + (840 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow5pm = new Date(tomorrow.getTime() + (1020 * ONE_MINUTE_IN_MILLIS));

      Date tomorrow28am = new Date(tomorrow2.getTime() + (480 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow2815am = new Date(tomorrow2.getTime() + (495 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow29am = new Date(tomorrow2.getTime() + (540 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow210am = new Date(tomorrow2.getTime() + (600 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow212pm = new Date(tomorrow2.getTime() + (720 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow22pm = new Date(tomorrow2.getTime() + (840 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow25pm = new Date(tomorrow2.getTime() + (1020 * ONE_MINUTE_IN_MILLIS));

      Date tomorrow38am = new Date(tomorrow3.getTime() + (480 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow39am = new Date(tomorrow3.getTime() + (540 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow312pm = new Date(tomorrow3.getTime() + (720 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow32pm = new Date(tomorrow3.getTime() + (840 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow35pm = new Date(tomorrow3.getTime() + (1020 * ONE_MINUTE_IN_MILLIS));

      Date tomorrow48am = new Date(tomorrow4.getTime() + (480 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow49am = new Date(tomorrow4.getTime() + (540 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow4830am = new Date(tomorrow4.getTime() + (510 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow42pm = new Date(tomorrow4.getTime() + (840 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow45pm = new Date(tomorrow4.getTime() + (1020 * ONE_MINUTE_IN_MILLIS));

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

      Schedule sched = new Schedule(taskBlockList, eventBlockList, 480, 600);

      List<AssignedTaskBlock> out = sched.getAssignedTaskBlocks();

      int timeTaskOne = 0;
      int timeTaskTwo = 0;

      for (AssignedTaskBlock block : out) {
        assert((block.getStartTime().getTime()
                - AlgorithmUtil.zeroesDate(block.getStartTime()).getTime())
                / 60000 >= 480);
        assert((block.getEndTime().getTime()
                - AlgorithmUtil.zeroesDate(block.getEndTime()).getTime())
                / 60000 <= 600);
        if (block.getTaskID() == 1) {
          timeTaskOne += (block.getEndTime().getTime() - block.getStartTime().getTime()) / 60000;
        }
        if (block.getTaskID() == 2) {
          timeTaskTwo += (block.getEndTime().getTime() - block.getStartTime().getTime()) / 60000;
        }
      }

      assertEquals(30, timeTaskOne);
      assertEquals(30, timeTaskTwo);


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

      sched = new Schedule(taskBlockList, eventBlockList, 540, 1020);

      out = sched.getAssignedTaskBlocks();

      timeTaskOne = 0;
      timeTaskTwo = 0;
      int timeTaskThree = 0;

      for (AssignedTaskBlock block : out) {
        assert((block.getStartTime().getTime()
                - AlgorithmUtil.zeroesDate(block.getStartTime()).getTime())
                / 60000 >= 540);
        assert((block.getEndTime().getTime()
                - AlgorithmUtil.zeroesDate(block.getEndTime()).getTime())
                / 60000 <= 1020);
        if (block.getTaskID() == 1) {
          timeTaskOne += (block.getEndTime().getTime() - block.getStartTime().getTime()) / 60000;
        }
        if (block.getTaskID() == 2) {
          timeTaskTwo += (block.getEndTime().getTime() - block.getStartTime().getTime()) / 60000;
        }
        if (block.getTaskID() == 3) {
          timeTaskThree += (block.getEndTime().getTime() - block.getStartTime().getTime()) / 60000;
        }
      }

      assertEquals(60, timeTaskOne);
      assertEquals(130, timeTaskTwo);
      assertEquals(80, timeTaskThree);

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

      sched = new Schedule(taskBlockList, eventBlockList, 480, 540);

      out = sched.getAssignedTaskBlocks();

      timeTaskOne = 0;
      timeTaskTwo = 0;

      for (AssignedTaskBlock block : out) {
        assert((block.getStartTime().getTime()
                - AlgorithmUtil.zeroesDate(block.getStartTime()).getTime())
                / 60000 >= 480);
        assert((block.getEndTime().getTime()
                - AlgorithmUtil.zeroesDate(block.getEndTime()).getTime())
                / 60000 <= 540);
        if (block.getTaskID() == 1) {
          timeTaskOne += (block.getEndTime().getTime() - block.getStartTime().getTime()) / 60000;
        }
        if (block.getTaskID() == 2) {
          timeTaskTwo += (block.getEndTime().getTime() - block.getStartTime().getTime()) / 60000;
        }
      }

      assertEquals(30, timeTaskOne);
      assertEquals(30, timeTaskTwo);


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

      sched = new Schedule(taskBlockList, eventBlockList, 480, 840);

      out = sched.getAssignedTaskBlocks();

      timeTaskOne = 0;
      timeTaskTwo = 0;
      timeTaskThree = 0;
      int timeTaskFour = 0;
      int timeTaskFive = 0;

      for (AssignedTaskBlock block : out) {
        assert((block.getStartTime().getTime()
                - AlgorithmUtil.zeroesDate(block.getStartTime()).getTime())
                / 60000 >= 480);
        assert((block.getEndTime().getTime()
                - AlgorithmUtil.zeroesDate(block.getEndTime()).getTime())
                / 60000 <= 840);
        if (block.getTaskID() == 1) {
          timeTaskOne += (block.getEndTime().getTime() - block.getStartTime().getTime()) / 60000;
        }
        if (block.getTaskID() == 2) {
          timeTaskTwo += (block.getEndTime().getTime() - block.getStartTime().getTime()) / 60000;
        }
        if (block.getTaskID() == 3) {
          timeTaskThree += (block.getEndTime().getTime() - block.getStartTime().getTime()) / 60000;
        }
        if (block.getTaskID() == 4) {
          timeTaskFour += (block.getEndTime().getTime() - block.getStartTime().getTime()) / 60000;
        }
        if (block.getTaskID() == 5) {
          timeTaskFive += (block.getEndTime().getTime() - block.getStartTime().getTime()) / 60000;
        }
      }

      assertEquals(30, timeTaskOne);
      assertEquals(15, timeTaskTwo);
      assertEquals(100, timeTaskThree);
      assertEquals(15, timeTaskFour);
      assertEquals(30, timeTaskFive);


      // test 6:
      taskBlockList = new LinkedList<>();
      eventBlockList = new LinkedList<>();

      BusyBlock event14 = new BusyBlock(tomorrow2815am, tomorrow29am);

      eventBlockList.add(allDayToday);
      eventBlockList.add(event14);

      taskBlockList.add(new Task(1,75, tomorrow3, "task 1"));

      sched = new Schedule(taskBlockList, eventBlockList, 480, 540);

      out = sched.getAssignedTaskBlocks();

      timeTaskOne = 0;

      for (AssignedTaskBlock block : out) {
        assert((block.getStartTime().getTime()
                - AlgorithmUtil.zeroesDate(block.getStartTime()).getTime())
                / 60000 >= 480);
        assert((block.getEndTime().getTime()
                - AlgorithmUtil.zeroesDate(block.getEndTime()).getTime())
                / 60000 <= 540);
        if (block.getTaskID() == 1) {
          timeTaskOne += (block.getEndTime().getTime() - block.getStartTime().getTime()) / 60000;
        }
      }

      assertEquals(75, timeTaskOne);


      // test event block before today (task today):
      taskBlockList = new LinkedList<>();
      eventBlockList = new LinkedList<>();

      BusyBlock event15 = new BusyBlock(yesterday8am, yesterday9am);
      eventBlockList.add(event15);

      taskBlockList.add(new Task(1,75, today, "task 1"));

      sched = new Schedule(taskBlockList, eventBlockList, 480, 540);

      out = sched.getAssignedTaskBlocks();

      timeTaskOne = 0;

      for (AssignedTaskBlock block : out) {
        assert((block.getStartTime().getTime()
                - AlgorithmUtil.zeroesDate(block.getStartTime()).getTime())
                / 60000 >= 480);
        assert((block.getEndTime().getTime()
                - AlgorithmUtil.zeroesDate(block.getEndTime()).getTime())
                / 60000 <= 540);
        if (block.getTaskID() == 1) {
          timeTaskOne += (block.getEndTime().getTime() - block.getStartTime().getTime()) / 60000;
        }
      }

      assertEquals(0, timeTaskOne);


      // test event block before today (task tomorrow):
      taskBlockList = new LinkedList<>();
      eventBlockList = new LinkedList<>();

      eventBlockList.add(allDayToday);
      eventBlockList.add(event15);

      taskBlockList.add(new Task(1,75, tomorrow, "task 1"));

      sched = new Schedule(taskBlockList, eventBlockList, 480, 540);

      out = sched.getAssignedTaskBlocks();

      timeTaskOne = 0;

      for (AssignedTaskBlock block : out) {
        assert((block.getStartTime().getTime()
                - AlgorithmUtil.zeroesDate(block.getStartTime()).getTime())
                / 60000 >= 480);
        assert((block.getEndTime().getTime()
                - AlgorithmUtil.zeroesDate(block.getEndTime()).getTime())
                / 60000 <= 540);
        if (block.getTaskID() == 1) {
          timeTaskOne += (block.getEndTime().getTime() - block.getStartTime().getTime()) / 60000;
        }
      }

      assertEquals(0, timeTaskOne);


      // test event block before today (task tomorrow2):
      taskBlockList = new LinkedList<>();
      eventBlockList = new LinkedList<>();

      eventBlockList.add(allDayToday);
      eventBlockList.add(event15);

      taskBlockList.add(new Task(1,75, tomorrow2, "task 1"));

      sched = new Schedule(taskBlockList, eventBlockList, 480, 540);

      out = sched.getAssignedTaskBlocks();

      timeTaskOne = 0;

      for (AssignedTaskBlock block : out) {
        assert((block.getStartTime().getTime()
                - AlgorithmUtil.zeroesDate(block.getStartTime()).getTime())
                / 60000 >= 480);
        assert((block.getEndTime().getTime()
                - AlgorithmUtil.zeroesDate(block.getEndTime()).getTime())
                / 60000 <= 540);
        if (block.getTaskID() == 1) {
          timeTaskOne += (block.getEndTime().getTime() - block.getStartTime().getTime()) / 60000;
        }
      }

      assertEquals(60, timeTaskOne);


      // event at start of day:
      taskBlockList = new LinkedList<>();
      eventBlockList = new LinkedList<>();

      BusyBlock event16 = new BusyBlock(tomorrow8am, tomorrow10am);
      eventBlockList.add(allDayToday);
      eventBlockList.add(event16);

      taskBlockList.add(new Task(1,75, tomorrow2, "task 1"));

      sched = new Schedule(taskBlockList, eventBlockList, 540, 720);

      out = sched.getAssignedTaskBlocks();

      timeTaskOne = 0;

      for (AssignedTaskBlock block : out) {
        assert((block.getStartTime().getTime()
                - AlgorithmUtil.zeroesDate(block.getStartTime()).getTime())
                / 60000 >= 540);
        assert((block.getEndTime().getTime()
                - AlgorithmUtil.zeroesDate(block.getEndTime()).getTime())
                / 60000 <= 720);
        if (block.getTaskID() == 1) {
          timeTaskOne += (block.getEndTime().getTime() - block.getStartTime().getTime()) / 60000;
        }
      }

      assertEquals(75, timeTaskOne);


      // event at start of day multiple days:
      taskBlockList = new LinkedList<>();
      eventBlockList = new LinkedList<>();

      eventBlockList.add(allDayToday);
      eventBlockList.add(event16);

      taskBlockList.add(new Task(1,75, tomorrow3, "task 1"));

      sched = new Schedule(taskBlockList, eventBlockList, 540, 720);

      out = sched.getAssignedTaskBlocks();

      timeTaskOne = 0;

      for (AssignedTaskBlock block : out) {
        assert((block.getStartTime().getTime()
                - AlgorithmUtil.zeroesDate(block.getStartTime()).getTime())
                / 60000 >= 540);
        assert((block.getEndTime().getTime()
                - AlgorithmUtil.zeroesDate(block.getEndTime()).getTime())
                / 60000 <= 720);
        if (block.getTaskID() == 1) {
          timeTaskOne += (block.getEndTime().getTime() - block.getStartTime().getTime()) / 60000;
        }
      }

      assertEquals(75, timeTaskOne);


      // event at end of day:
      taskBlockList = new LinkedList<>();
      eventBlockList = new LinkedList<>();

      BusyBlock event17 = new BusyBlock(tomorrow10am, tomorrow12pm);
      eventBlockList.add(allDayToday);
      eventBlockList.add(event17);

      taskBlockList.add(new Task(1,75, tomorrow2, "task 1"));

      sched = new Schedule(taskBlockList, eventBlockList, 540, 720);

      out = sched.getAssignedTaskBlocks();

      timeTaskOne = 0;

      for (AssignedTaskBlock block : out) {
        assert((block.getStartTime().getTime()
                - AlgorithmUtil.zeroesDate(block.getStartTime()).getTime())
                / 60000 >= 540);
        assert((block.getEndTime().getTime()
                - AlgorithmUtil.zeroesDate(block.getEndTime()).getTime())
                / 60000 <= 720);
        if (block.getTaskID() == 1) {
          timeTaskOne += (block.getEndTime().getTime() - block.getStartTime().getTime()) / 60000;
        }
      }

      assertEquals(60, timeTaskOne);


      // event at start of day multiple days:
      taskBlockList = new LinkedList<>();
      eventBlockList = new LinkedList<>();

      eventBlockList.add(allDayToday);
      eventBlockList.add(event17);

      taskBlockList.add(new Task(1,75, tomorrow3, "task 1"));

      sched = new Schedule(taskBlockList, eventBlockList, 540, 720);

      out = sched.getAssignedTaskBlocks();

      timeTaskOne = 0;

      for (AssignedTaskBlock block : out) {
        assert((block.getStartTime().getTime()
                - AlgorithmUtil.zeroesDate(block.getStartTime()).getTime())
                / 60000 >= 540);
        assert((block.getEndTime().getTime()
                - AlgorithmUtil.zeroesDate(block.getEndTime()).getTime())
                / 60000 <= 720);
        if (block.getTaskID() == 1) {
          timeTaskOne += (block.getEndTime().getTime() - block.getStartTime().getTime()) / 60000;
        }
      }

      assertEquals(75, timeTaskOne);


      // events not in the right order:
      taskBlockList = new LinkedList<>();
      eventBlockList = new LinkedList<>();

      eventBlockList.add(event9);
      eventBlockList.add(event11);
      eventBlockList.add(allDayToday);
      eventBlockList.add(event10);
      eventBlockList.add(event13);
      eventBlockList.add(event12);

      taskBlockList.add(new Task(1, 30, tomorrow2, "task 1"));
      taskBlockList.add(new Task(2,15, tomorrow2, "task 2"));
      taskBlockList.add(new Task(3, 100, tomorrow4, "task 3"));
      taskBlockList.add(new Task(4,15, tomorrow4, "task 4"));
      taskBlockList.add(new Task(5,30, tomorrow5, "task 5"));

      sched = new Schedule(taskBlockList, eventBlockList, 480, 840);

      out = sched.getAssignedTaskBlocks();

      timeTaskOne = 0;
      timeTaskTwo = 0;
      timeTaskThree = 0;
      timeTaskFour = 0;
      timeTaskFive = 0;

      for (AssignedTaskBlock block : out) {
        assert((block.getStartTime().getTime()
                - AlgorithmUtil.zeroesDate(block.getStartTime()).getTime())
                / 60000 >= 480);
        assert((block.getEndTime().getTime()
                - AlgorithmUtil.zeroesDate(block.getEndTime()).getTime())
                / 60000 <= 840);
        if (block.getTaskID() == 1) {
          timeTaskOne += (block.getEndTime().getTime() - block.getStartTime().getTime()) / 60000;
        }
        if (block.getTaskID() == 2) {
          timeTaskTwo += (block.getEndTime().getTime() - block.getStartTime().getTime()) / 60000;
        }
        if (block.getTaskID() == 3) {
          timeTaskThree += (block.getEndTime().getTime() - block.getStartTime().getTime()) / 60000;
        }
        if (block.getTaskID() == 4) {
          timeTaskFour += (block.getEndTime().getTime() - block.getStartTime().getTime()) / 60000;
        }
        if (block.getTaskID() == 5) {
          timeTaskFive += (block.getEndTime().getTime() - block.getStartTime().getTime()) / 60000;
        }
      }

      assertEquals(30, timeTaskOne);
      assertEquals(15, timeTaskTwo);
      assertEquals(100, timeTaskThree);
      assertEquals(15, timeTaskFour);
      assertEquals(30, timeTaskFive);

    } catch (ParseException e) {
      fail();
    }

  }
}
