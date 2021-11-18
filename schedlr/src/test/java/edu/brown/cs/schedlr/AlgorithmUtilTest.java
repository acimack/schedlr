package edu.brown.cs.schedlr;

import edu.brown.cs.schedlr.algorithm.AlgorithmUtil;
import edu.brown.cs.schedlr.algorithm.FreeBlock;
import edu.brown.cs.schedlr.algorithm.TaskBlock;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static edu.brown.cs.schedlr.algorithm.ScheduleConstants.ONE_MINUTE_IN_MILLIS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AlgorithmUtilTest {

  @Test
  public void testDateToRelativeWeekDay() {
    Calendar calendar = Calendar.getInstance();
    Date date = calendar.getTime();
    try {
      assertEquals(0, AlgorithmUtil.dateToRelativeWeekDay(date));
      calendar.add(Calendar.DAY_OF_YEAR, -1);
      date = calendar.getTime();
      assertEquals(-1, AlgorithmUtil.dateToRelativeWeekDay(date));
      calendar.add(Calendar.DAY_OF_YEAR, 2);
      date = calendar.getTime();
      assertEquals(1, AlgorithmUtil.dateToRelativeWeekDay(date));
      calendar.add(Calendar.DAY_OF_YEAR, 1);
      date = calendar.getTime();
      assertEquals(2, AlgorithmUtil.dateToRelativeWeekDay(date));
      calendar.add(Calendar.DAY_OF_YEAR, 4);
      date = calendar.getTime();
      assertEquals(6, AlgorithmUtil.dateToRelativeWeekDay(date));
    } catch (ParseException e) {
      fail();
    }
  }

  @Test
  public void testZeroesDate() {
    Calendar zeroedCal = new GregorianCalendar();
    zeroedCal.set(Calendar.HOUR_OF_DAY, 0);
    zeroedCal.set(Calendar.MINUTE, 0);
    zeroedCal.set(Calendar.SECOND, 0);
    zeroedCal.set(Calendar.MILLISECOND, 0);

    try {
      Date date = zeroedCal.getTime();

      Calendar calendar = Calendar.getInstance();
      Date expectDate = calendar.getTime();
      Date zeroed = AlgorithmUtil.zeroesDate(expectDate);

      assertEquals(date, zeroed);

      zeroedCal.add(Calendar.DAY_OF_YEAR, 1);
      calendar.add(Calendar.DAY_OF_YEAR, 1);

      date = zeroedCal.getTime();
      expectDate = calendar.getTime();
      zeroed = AlgorithmUtil.zeroesDate(expectDate);

      assertEquals(date, zeroed);

      zeroedCal.add(Calendar.DAY_OF_YEAR, 1);
      calendar.add(Calendar.DAY_OF_YEAR, 1);

      date = zeroedCal.getTime();
      expectDate = calendar.getTime();
      zeroed = AlgorithmUtil.zeroesDate(expectDate);

      assertEquals(date, zeroed);
    } catch (ParseException e) {
      fail();
    }
  }

  @Test
  public void testTotalFreeBlockTime() {
    Calendar calendar = Calendar.getInstance();
    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    try {
      Date today = formatter.parse(formatter.format(calendar.getTime()));
      calendar.add(Calendar.DAY_OF_YEAR, 1);
      Date tomorrow = formatter.parse(formatter.format(calendar.getTime()));
      calendar.add(Calendar.DAY_OF_YEAR, 1);

      Date today8am = new Date(today.getTime() + (480 * ONE_MINUTE_IN_MILLIS));
      Date today10am = new Date(today.getTime() + (600 * ONE_MINUTE_IN_MILLIS));
      Date today1015am = new Date(today.getTime() + (615 * ONE_MINUTE_IN_MILLIS));

      Date tomorrow9am = new Date(tomorrow.getTime() + (540 * ONE_MINUTE_IN_MILLIS));
      Date tomorrow10am = new Date(tomorrow.getTime() + (600 * ONE_MINUTE_IN_MILLIS));

      List<FreeBlock> fBlockList = new ArrayList<>();

      int time = AlgorithmUtil.totalFreeBlockTime(fBlockList);
      assertEquals(0, time);

      fBlockList.add(new FreeBlock(today, today8am, today10am));
      time = AlgorithmUtil.totalFreeBlockTime(fBlockList);
      assertEquals(120, time);

      fBlockList.add(new FreeBlock(today, today10am, today1015am));
      time = AlgorithmUtil.totalFreeBlockTime(fBlockList);
      assertEquals(135, time);

      fBlockList.add(new FreeBlock(tomorrow, tomorrow9am, tomorrow10am));
      time = AlgorithmUtil.totalFreeBlockTime(fBlockList);
      assertEquals(195, time);

    } catch (ParseException e) {
      fail();
    }
  }

  @Test
  public void testTotalTaskBlockTime() {
    Calendar calendar = Calendar.getInstance();
    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    try {
      Date today = formatter.parse(formatter.format(calendar.getTime()));
      calendar.add(Calendar.DAY_OF_YEAR, 1);
      Date tomorrow = formatter.parse(formatter.format(calendar.getTime()));
      calendar.add(Calendar.DAY_OF_YEAR, 1);

      List<TaskBlock> tBlockList = new ArrayList<>();

      int time = AlgorithmUtil.totalTaskBlockTime(tBlockList);
      assertEquals(0, time);

      tBlockList.add(new TaskBlock(1,120, today, false, ""));
      time = AlgorithmUtil.totalTaskBlockTime(tBlockList);
      assertEquals(120, time);

      tBlockList.add(new TaskBlock(2,15, today, false, ""));
      time = AlgorithmUtil.totalTaskBlockTime(tBlockList);
      assertEquals(135, time);

      tBlockList.add(new TaskBlock(3,60, tomorrow, false, ""));
      time = AlgorithmUtil.totalTaskBlockTime(tBlockList);
      assertEquals(195, time);

    } catch (ParseException e) {
      fail();
    }
  }
}
