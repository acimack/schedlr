package edu.brown.cs.schedlr;

import edu.brown.cs.schedlr.algorithm.*;
import junit.framework.Assert;
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

public class ScheduleInfoTest {

  @Test
  public void testCreateFreeBlocksFromEvents() {
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

      ScheduleInfo sched = new ScheduleInfo(taskBlockList, eventBlockList, 480, 600);

      List<FreeBlock> out = sched.getFreeBlockListInitial();

      List<FreeBlock> expected = new LinkedList<>();
      expected.add(new FreeBlock(tomorrow, tomorrow8am, tomorrow9am));
      expected.add(new FreeBlock(tomorrow2, tomorrow28am, tomorrow29am));

      assertEquals(expected.size(), out.size());
      assertEquals(expected.get(0).getDay(), out.get(0).getDay());
      assertEquals(expected.get(0).getStartTime(), out.get(0).getStartTime());
      assertEquals(expected.get(0).getEndTime(), out.get(0).getEndTime());
      assertEquals(expected.get(1).getDay(), out.get(1).getDay());
      assertEquals(expected.get(1).getStartTime(), out.get(1).getStartTime());
      assertEquals(expected.get(1).getEndTime(), out.get(1).getEndTime());

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

      sched = new ScheduleInfo(taskBlockList, eventBlockList, 540, 1020);

      out = sched.getFreeBlockListInitial();

      expected = new LinkedList<>();
      expected.add(new FreeBlock(tomorrow2, tomorrow29am, tomorrow25pm));
      expected.add(new FreeBlock(tomorrow3, tomorrow39am, tomorrow35pm));
      expected.add(new FreeBlock(tomorrow4, tomorrow49am, tomorrow45pm));

      assertEquals(expected.size(), out.size());
      assertEquals(expected.get(0).getDay(), out.get(0).getDay());
      assertEquals(expected.get(0).getStartTime(), out.get(0).getStartTime());
      assertEquals(expected.get(0).getEndTime(), out.get(0).getEndTime());
      assertEquals(expected.get(1).getDay(), out.get(1).getDay());
      assertEquals(expected.get(1).getStartTime(), out.get(1).getStartTime());
      assertEquals(expected.get(1).getEndTime(), out.get(1).getEndTime());


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

      sched = new ScheduleInfo(taskBlockList, eventBlockList, 480, 540);

      out = sched.getFreeBlockListInitial();

      expected = new LinkedList<>();
      expected.add(new FreeBlock(tomorrow2, tomorrow28am, tomorrow29am));
      expected.add(new FreeBlock(tomorrow3, tomorrow38am, tomorrow39am));

      assertEquals(expected.size(), out.size());
      assertEquals(expected.get(0).getDay(), out.get(0).getDay());
      assertEquals(expected.get(0).getStartTime(), out.get(0).getStartTime());
      assertEquals(expected.get(0).getEndTime(), out.get(0).getEndTime());
      assertEquals(expected.get(1).getDay(), out.get(1).getDay());
      assertEquals(expected.get(1).getStartTime(), out.get(1).getStartTime());
      assertEquals(expected.get(1).getEndTime(), out.get(1).getEndTime());

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

      sched = new ScheduleInfo(taskBlockList, eventBlockList, 480, 840);

      out = sched.getFreeBlockListInitial();

      expected = new LinkedList<>();
      expected.add(new FreeBlock(tomorrow, tomorrow8am, tomorrow9am));
      expected.add(new FreeBlock(tomorrow3, tomorrow312pm, tomorrow32pm));
      expected.add(new FreeBlock(tomorrow4, tomorrow48am, tomorrow4830am));

      assertEquals(expected.size(), out.size());
      assertEquals(expected.get(0).getDay(), out.get(0).getDay());
      assertEquals(expected.get(0).getStartTime(), out.get(0).getStartTime());
      assertEquals(expected.get(0).getEndTime(), out.get(0).getEndTime());
      assertEquals(expected.get(1).getDay(), out.get(1).getDay());
      assertEquals(expected.get(1).getStartTime(), out.get(1).getStartTime());
      assertEquals(expected.get(1).getEndTime(), out.get(1).getEndTime());
      assertEquals(expected.get(2).getDay(), out.get(2).getDay());
      assertEquals(expected.get(2).getStartTime(), out.get(2).getStartTime());
      assertEquals(expected.get(2).getEndTime(), out.get(2).getEndTime());

      // test 6:
      taskBlockList = new LinkedList<>();
      eventBlockList = new LinkedList<>();

      BusyBlock event14 = new BusyBlock(tomorrow2815am, tomorrow29am);

      eventBlockList.add(allDayToday);
      eventBlockList.add(event14);

      taskBlockList.add(new Task(1,75, tomorrow3, "task 1"));

      sched = new ScheduleInfo(taskBlockList, eventBlockList, 480, 540);

      out = sched.getFreeBlockListInitial();

      expected = new LinkedList<>();
      expected.add(new FreeBlock(tomorrow, tomorrow8am, tomorrow9am));
      expected.add(new FreeBlock(tomorrow2, tomorrow28am, tomorrow2815am));

      assertEquals(expected.size(), out.size());
      assertEquals(expected.get(0).getDay(), out.get(0).getDay());
      assertEquals(expected.get(0).getStartTime(), out.get(0).getStartTime());
      assertEquals(expected.get(0).getEndTime(), out.get(0).getEndTime());
      assertEquals(expected.get(1).getDay(), out.get(1).getDay());
      assertEquals(expected.get(1).getStartTime(), out.get(1).getStartTime());
      assertEquals(expected.get(1).getEndTime(), out.get(1).getEndTime());

      // test event block before today (task today):
      taskBlockList = new LinkedList<>();
      eventBlockList = new LinkedList<>();

      BusyBlock event15 = new BusyBlock(yesterday8am, yesterday9am);
      eventBlockList.add(event15);

      taskBlockList.add(new Task(1,75, today, "task 1"));

      sched = new ScheduleInfo(taskBlockList, eventBlockList, 480, 540);

      out = sched.getFreeBlockListInitial();

      assertEquals(0, out.size());


      // test event block before today (task tomorrow):
      taskBlockList = new LinkedList<>();
      eventBlockList = new LinkedList<>();

      eventBlockList.add(allDayToday);
      eventBlockList.add(event15);

      taskBlockList.add(new Task(1,75, today, "task 1"));

      sched = new ScheduleInfo(taskBlockList, eventBlockList, 480, 540);

      out = sched.getFreeBlockListInitial();

      assertEquals(0, out.size());


      // test event block before today (task tomorrow2):
      taskBlockList = new LinkedList<>();
      eventBlockList = new LinkedList<>();

      eventBlockList.add(allDayToday);
      eventBlockList.add(event15);

      taskBlockList.add(new Task(1,75, tomorrow2, "task 1"));

      sched = new ScheduleInfo(taskBlockList, eventBlockList, 480, 540);

      out = sched.getFreeBlockListInitial();

      expected = new LinkedList<>();
      expected.add(new FreeBlock(tomorrow, tomorrow8am, tomorrow9am));

      assertEquals(1, out.size());
      assertEquals(expected.get(0).getDay(), out.get(0).getDay());
      assertEquals(expected.get(0).getStartTime(), out.get(0).getStartTime());
      assertEquals(expected.get(0).getEndTime(), out.get(0).getEndTime());


      // event at start of day:
      taskBlockList = new LinkedList<>();
      eventBlockList = new LinkedList<>();

      BusyBlock event16 = new BusyBlock(tomorrow8am, tomorrow10am);
      eventBlockList.add(allDayToday);
      eventBlockList.add(event16);

      taskBlockList.add(new Task(1,75, tomorrow2, "task 1"));

      sched = new ScheduleInfo(taskBlockList, eventBlockList, 540, 720);

      out = sched.getFreeBlockListInitial();

      expected = new LinkedList<>();
      expected.add(new FreeBlock(tomorrow, tomorrow10am, tomorrow12pm));

      assertEquals(1, out.size());
      assertEquals(expected.get(0).getDay(), out.get(0).getDay());
      assertEquals(expected.get(0).getStartTime(), out.get(0).getStartTime());
      assertEquals(expected.get(0).getEndTime(), out.get(0).getEndTime());


      // event at start of day multiple days:
      taskBlockList = new LinkedList<>();
      eventBlockList = new LinkedList<>();

      eventBlockList.add(allDayToday);
      eventBlockList.add(event16);

      taskBlockList.add(new Task(1,75, tomorrow3, "task 1"));

      sched = new ScheduleInfo(taskBlockList, eventBlockList, 540, 720);

      out = sched.getFreeBlockListInitial();

      expected = new LinkedList<>();
      expected.add(new FreeBlock(tomorrow, tomorrow10am, tomorrow12pm));
      expected.add(new FreeBlock(tomorrow2, tomorrow29am, tomorrow212pm));

      assertEquals(2, out.size());
      assertEquals(expected.get(0).getDay(), out.get(0).getDay());
      assertEquals(expected.get(0).getStartTime(), out.get(0).getStartTime());
      assertEquals(expected.get(0).getEndTime(), out.get(0).getEndTime());
      assertEquals(expected.get(1).getDay(), out.get(1).getDay());
      assertEquals(expected.get(1).getStartTime(), out.get(1).getStartTime());
      assertEquals(expected.get(1).getEndTime(), out.get(1).getEndTime());


      // event at end of day:
      taskBlockList = new LinkedList<>();
      eventBlockList = new LinkedList<>();

      BusyBlock event17 = new BusyBlock(tomorrow10am, tomorrow12pm);
      eventBlockList.add(allDayToday);
      eventBlockList.add(event17);

      taskBlockList.add(new Task(1,75, tomorrow2, "task 1"));

      sched = new ScheduleInfo(taskBlockList, eventBlockList, 540, 720);

      out = sched.getFreeBlockListInitial();

      expected = new LinkedList<>();
      expected.add(new FreeBlock(tomorrow, tomorrow9am, tomorrow10am));

      assertEquals(1, out.size());
      assertEquals(expected.get(0).getDay(), out.get(0).getDay());
      assertEquals(expected.get(0).getStartTime(), out.get(0).getStartTime());
      assertEquals(expected.get(0).getEndTime(), out.get(0).getEndTime());


      // event at start of day multiple days:
      taskBlockList = new LinkedList<>();
      eventBlockList = new LinkedList<>();

      eventBlockList.add(allDayToday);
      eventBlockList.add(event17);

      taskBlockList.add(new Task(1,75, tomorrow3, "task 1"));

      sched = new ScheduleInfo(taskBlockList, eventBlockList, 540, 720);

      out = sched.getFreeBlockListInitial();

      expected = new LinkedList<>();
      expected.add(new FreeBlock(tomorrow, tomorrow9am, tomorrow10am));
      expected.add(new FreeBlock(tomorrow2, tomorrow29am, tomorrow212pm));

      assertEquals(2, out.size());
      assertEquals(expected.get(0).getDay(), out.get(0).getDay());
      assertEquals(expected.get(0).getStartTime(), out.get(0).getStartTime());
      assertEquals(expected.get(0).getEndTime(), out.get(0).getEndTime());
      assertEquals(expected.get(1).getDay(), out.get(1).getDay());
      assertEquals(expected.get(1).getStartTime(), out.get(1).getStartTime());
      assertEquals(expected.get(1).getEndTime(), out.get(1).getEndTime());


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

      sched = new ScheduleInfo(taskBlockList, eventBlockList, 480, 840);

      out = sched.getFreeBlockListInitial();

      expected = new LinkedList<>();
      expected.add(new FreeBlock(tomorrow, tomorrow8am, tomorrow9am));
      expected.add(new FreeBlock(tomorrow3, tomorrow312pm, tomorrow32pm));
      expected.add(new FreeBlock(tomorrow4, tomorrow48am, tomorrow4830am));

      assertEquals(expected.size(), out.size());
      assertEquals(expected.get(0).getDay(), out.get(0).getDay());
      assertEquals(expected.get(0).getStartTime(), out.get(0).getStartTime());
      assertEquals(expected.get(0).getEndTime(), out.get(0).getEndTime());
      assertEquals(expected.get(1).getDay(), out.get(1).getDay());
      assertEquals(expected.get(1).getStartTime(), out.get(1).getStartTime());
      assertEquals(expected.get(1).getEndTime(), out.get(1).getEndTime());
      assertEquals(expected.get(2).getDay(), out.get(2).getDay());
      assertEquals(expected.get(2).getStartTime(), out.get(2).getStartTime());
      assertEquals(expected.get(2).getEndTime(), out.get(2).getEndTime());

    } catch (ParseException e) {
      fail();
    }
  }

  @Test
  public void testCalculateDailyFreeTime() {
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
      Date tomorrow6 = formatter.parse(formatter.format(calendar.getTime()));

      Date today8am = new Date(today.getTime() + (480 * ONE_MINUTE_IN_MILLIS));
      Date today10am = new Date(today.getTime() + (600 * ONE_MINUTE_IN_MILLIS));

      Date tomorrow8am = new Date(tomorrow.getTime() + (480 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow9am = new Date(tomorrow.getTime() + (540 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow10am = new Date(tomorrow.getTime() + (600 * ONE_MINUTE_IN_MILLIS));

      Date tomorrow3830am = new Date(tomorrow3.getTime() + (510 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow39am = new Date(tomorrow3.getTime() + (540 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow310am = new Date(tomorrow3.getTime() + (600 * ONE_MINUTE_IN_MILLIS));

      Date tomorrow48am = new Date(tomorrow4.getTime() + (480 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow410am = new Date(tomorrow4.getTime() + (600 * ONE_MINUTE_IN_MILLIS));

      List<Task> taskList = new LinkedList<>();
      List<BusyBlock> eventList = new LinkedList<>();

      ScheduleInfo sched = new ScheduleInfo(new LinkedList<>(taskList), new LinkedList<>(eventList), 0, 0);
      List<Integer> freeTime = sched.getDailyFreeTime();
      assert(freeTime.isEmpty());

      eventList.add(new BusyBlock(today8am, today10am));
      eventList.add(new BusyBlock(tomorrow8am, tomorrow9am));
      sched = new ScheduleInfo(new LinkedList<>(taskList), new LinkedList<>(eventList), 0, 0);
      freeTime = sched.getDailyFreeTime();
      assert(freeTime.isEmpty());

      sched = new ScheduleInfo(new LinkedList<>(taskList), new LinkedList<>(eventList), 480, 540);
      freeTime = sched.getDailyFreeTime();
      assert(freeTime.isEmpty());

      sched = new ScheduleInfo(new LinkedList<>(taskList), new LinkedList<>(eventList), 480, 600);
      freeTime = sched.getDailyFreeTime();
      assert(freeTime.isEmpty());

      taskList.add(new Task(1, 60, today, ""));
      sched = new ScheduleInfo(new LinkedList<>(taskList), new LinkedList<>(eventList), 480, 600);
      freeTime = sched.getDailyFreeTime();
      assert(freeTime.isEmpty());

      taskList.add(new Task(2, 60, tomorrow, ""));
      sched = new ScheduleInfo(new LinkedList<>(taskList), new LinkedList<>(eventList), 480, 600);
      freeTime = sched.getDailyFreeTime();
      assertEquals(1, freeTime.size());
      assertEquals(0, freeTime.get(0), .001);

      taskList.add(new Task(3,60, tomorrow2, ""));
      sched = new ScheduleInfo(new LinkedList<>(taskList), new LinkedList<>(eventList), 480, 600);
      freeTime = sched.getDailyFreeTime();
      assertEquals(2, freeTime.size());
      assertEquals(0, freeTime.get(0), .001);
      assertEquals(60, freeTime.get(1), .001);

      eventList.add(new BusyBlock(tomorrow9am, tomorrow10am));
      sched = new ScheduleInfo(new LinkedList<>(taskList), new LinkedList<>(eventList), 480, 600);
      freeTime = sched.getDailyFreeTime();
      assertEquals(2, freeTime.size());
      assertEquals(0, freeTime.get(0), .001);
      assertEquals(0, freeTime.get(1), .001);

      eventList.add(new BusyBlock(tomorrow48am, tomorrow410am));
      sched = new ScheduleInfo(new LinkedList<>(taskList), new LinkedList<>(eventList), 480, 600);
      freeTime = sched.getDailyFreeTime();
      assertEquals(2, freeTime.size());
      assertEquals(0, freeTime.get(0), .001);
      assertEquals(0, freeTime.get(1), .001);

      taskList.add(new Task(4, 60, tomorrow3, ""));
      sched = new ScheduleInfo(new LinkedList<>(taskList), new LinkedList<>(eventList), 480, 600);
      freeTime = sched.getDailyFreeTime();
      assertEquals(3, freeTime.size());
      assertEquals(0, freeTime.get(0), .001);
      assertEquals(0, freeTime.get(1), .001);
      assertEquals(120, freeTime.get(2), .001);

      taskList.add(new Task(5, 60, tomorrow4, ""));
      sched = new ScheduleInfo(new LinkedList<>(taskList), new LinkedList<>(eventList), 480, 600);
      freeTime = sched.getDailyFreeTime();
      assertEquals(4, freeTime.size());
      assertEquals(0, freeTime.get(0), .001);
      assertEquals(0, freeTime.get(1), .001);
      assertEquals(120, freeTime.get(2), .001);
      assertEquals(120, freeTime.get(3), .001);

      taskList.add(new Task(6, 60, tomorrow5, ""));
      sched = new ScheduleInfo(new LinkedList<>(taskList), new LinkedList<>(eventList), 480, 600);
      freeTime = sched.getDailyFreeTime();
      assertEquals(5, freeTime.size());
      assertEquals(0, freeTime.get(0), .001);
      assertEquals(0, freeTime.get(1), .001);
      assertEquals(120, freeTime.get(2), .001);
      assertEquals(120, freeTime.get(3), .001);
      assertEquals(0, freeTime.get(4), .001);

      taskList.add(new Task(7, 60, tomorrow6, ""));
      sched = new ScheduleInfo(new LinkedList<>(taskList), new LinkedList<>(eventList), 480, 600);
      freeTime = sched.getDailyFreeTime();
      assertEquals(6, freeTime.size());
      assertEquals(0, freeTime.get(0), .001);
      assertEquals(0, freeTime.get(1), .001);
      assertEquals(120, freeTime.get(2), .001);
      assertEquals(120, freeTime.get(3), .001);
      assertEquals(0, freeTime.get(4), .001);
      assertEquals(120, freeTime.get(5), .001);

      eventList.add(new BusyBlock(tomorrow3830am, tomorrow39am));
      sched = new ScheduleInfo(new LinkedList<>(taskList), new LinkedList<>(eventList), 480, 600);
      freeTime = sched.getDailyFreeTime();
      assertEquals(6, freeTime.size());
      assertEquals(0, freeTime.get(0), .001);
      assertEquals(0, freeTime.get(1), .001);
      assertEquals(120, freeTime.get(2), .001);
      assertEquals(90, freeTime.get(3), .001);
      assertEquals(0, freeTime.get(4), .001);
      assertEquals(120, freeTime.get(5), .001);

      eventList.add(new BusyBlock(tomorrow39am, tomorrow310am));
      sched = new ScheduleInfo(new LinkedList<>(taskList), new LinkedList<>(eventList), 480, 600);
      freeTime = sched.getDailyFreeTime();
      assertEquals(6, freeTime.size());
      assertEquals(0, freeTime.get(0), .001);
      assertEquals(0, freeTime.get(1), .001);
      assertEquals(120, freeTime.get(2), .001);
      assertEquals(30, freeTime.get(3), .001);
      assertEquals(0, freeTime.get(4), .001);
      assertEquals(120, freeTime.get(5), .001);

    } catch (ParseException e) {
      fail();
    }
  }

  @Test
  public void testSplitTasks() {
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


      // test 1:
      List<Task> taskBlockList = new LinkedList<>();
      List<BusyBlock> eventBlockList = new LinkedList<>();

      taskBlockList.add(new Task(1,30, tomorrow3, "task 1"));
      taskBlockList.add(new Task(2,30, tomorrow3, "task 2"));

      ScheduleInfo sched = new ScheduleInfo(taskBlockList, eventBlockList, 480, 600);

      List<TaskBlock> expected = new LinkedList<>();
      expected.add(new TaskBlock(1,30, tomorrow3, false,"task 1"));
      expected.add(new TaskBlock(2,30, tomorrow3, false,"task 2"));

      List<TaskBlock> out = sched.getTaskBlockListInitial();

      assertEquals(expected.size(), out.size());
      assertEquals(expected.get(0).getTaskDeadline(), out.get(0).getTaskDeadline());
      assertEquals(expected.get(0).getTotalTime(), out.get(0).getTotalTime());
      assertEquals(expected.get(0).getTaskID(), out.get(0).getTaskID());
      assertEquals(expected.get(0).getTitle(), out.get(0).getTitle());
      assertEquals(expected.get(1).getTaskDeadline(), out.get(1).getTaskDeadline());
      assertEquals(expected.get(1).getTotalTime(), out.get(1).getTotalTime());
      assertEquals(expected.get(1).getTaskID(), out.get(1).getTaskID());
      assertEquals(expected.get(1).getTitle(), out.get(1).getTitle());


      // test 2:
      taskBlockList = new LinkedList<>();

      taskBlockList.add(new Task(1, 60, tomorrow5, "task 1"));
      taskBlockList.add(new Task(2,130, tomorrow5, "task 2"));
      taskBlockList.add(new Task(3, 80, tomorrow5, "task 3"));

      sched = new ScheduleInfo(taskBlockList, eventBlockList, 480, 600);

      expected = new LinkedList<>();
      expected.add(new TaskBlock(1,60, tomorrow5, false,"task 1"));
      expected.add(new TaskBlock(2,70, tomorrow5, false,"task 2"));
      expected.add(new TaskBlock(2,60, tomorrow5, false,"task 2"));
      expected.add(new TaskBlock(3,80, tomorrow5, false,"task 3"));

      out = sched.getTaskBlockListInitial();

      assertEquals(expected.size(), out.size());
      assertEquals(expected.get(0).getTaskDeadline(), out.get(0).getTaskDeadline());
      assertEquals(expected.get(0).getTotalTime(), out.get(0).getTotalTime());
      assertEquals(expected.get(0).getTaskID(), out.get(0).getTaskID());
      assertEquals(expected.get(0).getTitle(), out.get(0).getTitle());
      assertEquals(expected.get(1).getTaskDeadline(), out.get(1).getTaskDeadline());
      assertEquals(expected.get(1).getTotalTime(), out.get(1).getTotalTime());
      assertEquals(expected.get(1).getTaskID(), out.get(1).getTaskID());
      assertEquals(expected.get(1).getTitle(), out.get(1).getTitle());
      assertEquals(expected.get(2).getTaskDeadline(), out.get(2).getTaskDeadline());
      assertEquals(expected.get(2).getTotalTime(), out.get(2).getTotalTime());
      assertEquals(expected.get(2).getTaskID(), out.get(2).getTaskID());
      assertEquals(expected.get(2).getTitle(), out.get(2).getTitle());
      assertEquals(expected.get(3).getTaskDeadline(), out.get(3).getTaskDeadline());
      assertEquals(expected.get(3).getTotalTime(), out.get(3).getTotalTime());
      assertEquals(expected.get(3).getTaskID(), out.get(3).getTaskID());
      assertEquals(expected.get(3).getTitle(), out.get(3).getTitle());


      // test 3:
      taskBlockList = new LinkedList<>();

      taskBlockList.add(new Task(1, 30, tomorrow4, "task 1"));
      taskBlockList.add(new Task(2, 30, tomorrow4, "task 2"));

      sched = new ScheduleInfo(taskBlockList, eventBlockList, 480, 600);

      expected = new LinkedList<>();
      expected.add(new TaskBlock(1,30, tomorrow4, false,"task 1"));
      expected.add(new TaskBlock(2,30, tomorrow4, false,"task 2"));

      out = sched.getTaskBlockListInitial();

      assertEquals(expected.size(), out.size());
      assertEquals(expected.get(0).getTaskDeadline(), out.get(0).getTaskDeadline());
      assertEquals(expected.get(0).getTotalTime(), out.get(0).getTotalTime());
      assertEquals(expected.get(0).getTaskID(), out.get(0).getTaskID());
      assertEquals(expected.get(0).getTitle(), out.get(0).getTitle());
      assertEquals(expected.get(1).getTaskDeadline(), out.get(1).getTaskDeadline());
      assertEquals(expected.get(1).getTotalTime(), out.get(1).getTotalTime());
      assertEquals(expected.get(1).getTaskID(), out.get(1).getTaskID());
      assertEquals(expected.get(1).getTitle(), out.get(1).getTitle());


      // test 4:
      taskBlockList = new LinkedList<>();

      taskBlockList.add(new Task(1, 30, tomorrow2, "task 1"));
      taskBlockList.add(new Task(2,15, tomorrow2, "task 2"));
      taskBlockList.add(new Task(3, 100, tomorrow4, "task 3"));
      taskBlockList.add(new Task(4,15, tomorrow4, "task 4"));
      taskBlockList.add(new Task(5,30, tomorrow5, "task 5"));

      sched = new ScheduleInfo(taskBlockList, eventBlockList, 480, 600);

      expected = new LinkedList<>();
      expected.add(new TaskBlock(1,30, tomorrow2, false,"task 1"));
      expected.add(new TaskBlock(2,15, tomorrow2, false,"task 2"));
      expected.add(new TaskBlock(3,100, tomorrow4, false,"task 3"));
      expected.add(new TaskBlock(4,15, tomorrow4, false,"task 4"));
      expected.add(new TaskBlock(5,30, tomorrow5, false,"task 5"));

      out = sched.getTaskBlockListInitial();

      assertEquals(expected.size(), out.size());
      assertEquals(expected.get(0).getTaskDeadline(), out.get(0).getTaskDeadline());
      assertEquals(expected.get(0).getTotalTime(), out.get(0).getTotalTime());
      assertEquals(expected.get(0).getTaskID(), out.get(0).getTaskID());
      assertEquals(expected.get(0).getTitle(), out.get(0).getTitle());
      assertEquals(expected.get(1).getTaskDeadline(), out.get(1).getTaskDeadline());
      assertEquals(expected.get(1).getTotalTime(), out.get(1).getTotalTime());
      assertEquals(expected.get(1).getTaskID(), out.get(1).getTaskID());
      assertEquals(expected.get(1).getTitle(), out.get(1).getTitle());
      assertEquals(expected.get(2).getTaskDeadline(), out.get(2).getTaskDeadline());
      assertEquals(expected.get(2).getTotalTime(), out.get(2).getTotalTime());
      assertEquals(expected.get(2).getTaskID(), out.get(2).getTaskID());
      assertEquals(expected.get(2).getTitle(), out.get(2).getTitle());
      assertEquals(expected.get(3).getTaskDeadline(), out.get(3).getTaskDeadline());
      assertEquals(expected.get(3).getTotalTime(), out.get(3).getTotalTime());
      assertEquals(expected.get(3).getTaskID(), out.get(3).getTaskID());
      assertEquals(expected.get(3).getTitle(), out.get(3).getTitle());
      assertEquals(expected.get(4).getTaskDeadline(), out.get(4).getTaskDeadline());
      assertEquals(expected.get(4).getTotalTime(), out.get(4).getTotalTime());
      assertEquals(expected.get(4).getTaskID(), out.get(4).getTaskID());
      assertEquals(expected.get(4).getTitle(), out.get(4).getTitle());


      // task 5:
      taskBlockList = new LinkedList<>();

      taskBlockList.add(new Task(1,30, tomorrow2, "task 1"));
      taskBlockList.add(new Task(2,15, tomorrow2, "task 2"));
      taskBlockList.add(new Task(3,15, tomorrow3, "task 3"));
      taskBlockList.add(new Task(4,100, tomorrow4, "task 4"));
      taskBlockList.add(new Task(5, 15, tomorrow4, "task 5"));
      taskBlockList.add(new Task(6, 30, tomorrow5, "task 6"));

      sched = new ScheduleInfo(taskBlockList, eventBlockList, 480, 600);

      expected = new LinkedList<>();
      expected.add(new TaskBlock(1,30, tomorrow2, false,"task 1"));
      expected.add(new TaskBlock(2,15, tomorrow2, false,"task 2"));
      expected.add(new TaskBlock(3,15, tomorrow3, false,"task 3"));
      expected.add(new TaskBlock(4,100, tomorrow4, false,"task 4"));
      expected.add(new TaskBlock(5,15, tomorrow4, false,"task 5"));
      expected.add(new TaskBlock(6,30, tomorrow5, false,"task 6"));

      out = sched.getTaskBlockListInitial();

      assertEquals(expected.size(), out.size());
      assertEquals(expected.get(0).getTaskDeadline(), out.get(0).getTaskDeadline());
      assertEquals(expected.get(0).getTotalTime(), out.get(0).getTotalTime());
      assertEquals(expected.get(0).getTaskID(), out.get(0).getTaskID());
      assertEquals(expected.get(0).getTitle(), out.get(0).getTitle());
      assertEquals(expected.get(1).getTaskDeadline(), out.get(1).getTaskDeadline());
      assertEquals(expected.get(1).getTotalTime(), out.get(1).getTotalTime());
      assertEquals(expected.get(1).getTaskID(), out.get(1).getTaskID());
      assertEquals(expected.get(1).getTitle(), out.get(1).getTitle());
      assertEquals(expected.get(2).getTaskDeadline(), out.get(2).getTaskDeadline());
      assertEquals(expected.get(2).getTotalTime(), out.get(2).getTotalTime());
      assertEquals(expected.get(2).getTaskID(), out.get(2).getTaskID());
      assertEquals(expected.get(2).getTitle(), out.get(2).getTitle());
      assertEquals(expected.get(3).getTaskDeadline(), out.get(3).getTaskDeadline());
      assertEquals(expected.get(3).getTotalTime(), out.get(3).getTotalTime());
      assertEquals(expected.get(3).getTaskID(), out.get(3).getTaskID());
      assertEquals(expected.get(3).getTitle(), out.get(3).getTitle());
      assertEquals(expected.get(4).getTaskDeadline(), out.get(4).getTaskDeadline());
      assertEquals(expected.get(4).getTotalTime(), out.get(4).getTotalTime());
      assertEquals(expected.get(4).getTaskID(), out.get(4).getTaskID());
      assertEquals(expected.get(4).getTitle(), out.get(4).getTitle());
      assertEquals(expected.get(5).getTaskDeadline(), out.get(5).getTaskDeadline());
      assertEquals(expected.get(5).getTotalTime(), out.get(5).getTotalTime());
      assertEquals(expected.get(5).getTaskID(), out.get(5).getTaskID());
      assertEquals(expected.get(5).getTitle(), out.get(5).getTitle());


      // test 6:
      taskBlockList = new LinkedList<>();

      taskBlockList.add(new Task(1, 45, tomorrow2, "task 1"));
      taskBlockList.add(new Task(2, 15, tomorrow2, "task 2"));
      taskBlockList.add(new Task(3, 100, tomorrow4, "task 3"));
      taskBlockList.add(new Task(4, 50, tomorrow5, "task 4"));

      sched = new ScheduleInfo(taskBlockList, eventBlockList, 480, 600);

      expected = new LinkedList<>();
      expected.add(new TaskBlock(1,45, tomorrow2, false,"task 1"));
      expected.add(new TaskBlock(2,15, tomorrow2, false,"task 2"));
      expected.add(new TaskBlock(3,100, tomorrow4, false,"task 3"));
      expected.add(new TaskBlock(4,50, tomorrow5, false,"task 4"));

      out = sched.getTaskBlockListInitial();

      assertEquals(expected.size(), out.size());
      assertEquals(expected.get(0).getTaskDeadline(), out.get(0).getTaskDeadline());
      assertEquals(expected.get(0).getTotalTime(), out.get(0).getTotalTime());
      assertEquals(expected.get(0).getTaskID(), out.get(0).getTaskID());
      assertEquals(expected.get(0).getTitle(), out.get(0).getTitle());
      assertEquals(expected.get(1).getTaskDeadline(), out.get(1).getTaskDeadline());
      assertEquals(expected.get(1).getTotalTime(), out.get(1).getTotalTime());
      assertEquals(expected.get(1).getTaskID(), out.get(1).getTaskID());
      assertEquals(expected.get(1).getTitle(), out.get(1).getTitle());
      assertEquals(expected.get(2).getTaskDeadline(), out.get(2).getTaskDeadline());
      assertEquals(expected.get(2).getTotalTime(), out.get(2).getTotalTime());
      assertEquals(expected.get(2).getTaskID(), out.get(2).getTaskID());
      assertEquals(expected.get(2).getTitle(), out.get(2).getTitle());
      assertEquals(expected.get(3).getTaskDeadline(), out.get(3).getTaskDeadline());
      assertEquals(expected.get(3).getTotalTime(), out.get(3).getTotalTime());
      assertEquals(expected.get(3).getTaskID(), out.get(3).getTaskID());
      assertEquals(expected.get(3).getTitle(), out.get(3).getTitle());


      // test 7:
      taskBlockList = new LinkedList<>();

      taskBlockList.add(new Task(1,45, tomorrow2, "task 1"));
      taskBlockList.add(new Task(2,15, tomorrow2, "task 2"));
      taskBlockList.add(new Task(3,100, tomorrow4, "task 3"));
      taskBlockList.add(new Task(4,60, tomorrow5, "task 4"));

      sched = new ScheduleInfo(taskBlockList, eventBlockList, 480, 600);

      expected = new LinkedList<>();
      expected.add(new TaskBlock(1,45, tomorrow2, false,"task 1"));
      expected.add(new TaskBlock(2,15, tomorrow2, false,"task 2"));
      expected.add(new TaskBlock(3,100, tomorrow4, false,"task 3"));
      expected.add(new TaskBlock(4,60, tomorrow5, false,"task 4"));

      out = sched.getTaskBlockListInitial();

      assertEquals(expected.size(), out.size());
      assertEquals(expected.get(0).getTaskDeadline(), out.get(0).getTaskDeadline());
      assertEquals(expected.get(0).getTotalTime(), out.get(0).getTotalTime());
      assertEquals(expected.get(0).getTaskID(), out.get(0).getTaskID());
      assertEquals(expected.get(0).getTitle(), out.get(0).getTitle());
      assertEquals(expected.get(1).getTaskDeadline(), out.get(1).getTaskDeadline());
      assertEquals(expected.get(1).getTotalTime(), out.get(1).getTotalTime());
      assertEquals(expected.get(1).getTaskID(), out.get(1).getTaskID());
      assertEquals(expected.get(1).getTitle(), out.get(1).getTitle());
      assertEquals(expected.get(2).getTaskDeadline(), out.get(2).getTaskDeadline());
      assertEquals(expected.get(2).getTotalTime(), out.get(2).getTotalTime());
      assertEquals(expected.get(2).getTaskID(), out.get(2).getTaskID());
      assertEquals(expected.get(2).getTitle(), out.get(2).getTitle());
      assertEquals(expected.get(3).getTaskDeadline(), out.get(3).getTaskDeadline());
      assertEquals(expected.get(3).getTotalTime(), out.get(3).getTotalTime());
      assertEquals(expected.get(3).getTaskID(), out.get(3).getTaskID());
      assertEquals(expected.get(3).getTitle(), out.get(3).getTitle());

      // test 8:
      taskBlockList = new LinkedList<>();

      taskBlockList.add(new Task(1,75, tomorrow3, "task 1"));

      sched = new ScheduleInfo(taskBlockList, eventBlockList, 480, 600);

      expected = new LinkedList<>();
      expected.add(new TaskBlock(1,75, tomorrow3, false,"task 1"));

      out = sched.getTaskBlockListInitial();

      assertEquals(expected.size(), out.size());
      assertEquals(expected.get(0).getTaskDeadline(), out.get(0).getTaskDeadline());
      assertEquals(expected.get(0).getTotalTime(), out.get(0).getTotalTime());
      assertEquals(expected.get(0).getTaskID(), out.get(0).getTaskID());
      assertEquals(expected.get(0).getTitle(), out.get(0).getTitle());

    } catch (ParseException e) {
      Assert.fail();
    }

  }

}
