package edu.brown.cs.schedlr.algorithm;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static edu.brown.cs.schedlr.algorithm.ScheduleConstants.SCORE_PENALTY;

public final class WeekScoreGenerator {

  private WeekScoreGenerator() {
  }

  public static WeekScore generateWeekScore(int currInd, int freeTimeRemaining,
                                            int taskTimeRemaining, int weekDay,
                                            ScheduleInfo scheduleInfo) throws ParseException {
    // checks if it is the last task in the list
    if (currInd == scheduleInfo.getTaskBlockList().size()
            || ((weekDay + 1 == scheduleInfo.getDaysInSchedule() || weekDay == scheduleInfo.getDaysInSchedule())
            && freeTimeRemaining == 0)) {

      // sets up the base case weekscore
      List<Integer> dailyTasks = new LinkedList<>();

      for (int i = 0; i < scheduleInfo.getDaysInSchedule(); i++) {
        dailyTasks.add(0);
      }

      // calculates the score for that day
      Integer totalDayFreeTime = 0;
      if (!scheduleInfo.getDailyFreeTime().isEmpty()) {
        totalDayFreeTime = scheduleInfo.getDailyFreeTime().get(weekDay);
      }
      double workTime = totalDayFreeTime - freeTimeRemaining;

      // sets the number of tasks for the current index
      if (!dailyTasks.isEmpty()) {
        dailyTasks.set(weekDay, currInd);
        if (workTime == 0) {
          dailyTasks.set(weekDay, 0);
        }
      }

      // calculates the score if the freetime of the day is not 0
      double score = Double.POSITIVE_INFINITY;
      if (totalDayFreeTime != 0 && workTime >= 0) {
        score = Math.pow(Math.abs((workTime / totalDayFreeTime) - scheduleInfo.getBestWeekScore()), SCORE_PENALTY);
      }

      // calculates the score for all of the following days
      for (int i = weekDay + 1; i < scheduleInfo.getDailyFreeTime().size(); i++) {
        if (scheduleInfo.getDailyFreeTime().get(i) != 0) {
          score += Math.pow(Math.abs((0) - scheduleInfo.getBestWeekScore()), SCORE_PENALTY);
        }
      }
      return new WeekScore(dailyTasks, score);
    }

    // finds the current task
    TaskBlock currTask = scheduleInfo.getTaskBlockList().get(currInd);

    // todo: fix so that currday is always init
    WeekScore currDay = null;

    while (AlgorithmUtil.dateToRelativeWeekDay(currTask.getTaskDeadline()) < weekDay + 1) {
      currInd += 1;
      currTask = scheduleInfo.getTaskBlockList().get(currInd);
    }

    // checks if there is enough time on the day to add the task
    if (freeTimeRemaining >= currTask.getTotalTime()
            && AlgorithmUtil.dateToRelativeWeekDay(currTask.getTaskDeadline()) >= weekDay + 1) {

      // assigns the current task to current day and calculates the score for the next task
      currDay = generateWeekScore(currInd + 1,
              freeTimeRemaining - currTask.getTotalTime(),
              taskTimeRemaining - currTask.getTotalTime(), weekDay,
              scheduleInfo);

    }

    // automatically returns the current day if the task is due the next day
    if (AlgorithmUtil.dateToRelativeWeekDay(currTask.getTaskDeadline()) == weekDay + 1) {

      // if the task didn't fit on the current day,
      // still assigns to current day, but overrides score
      if (currDay == null) {
        currDay = generateWeekScore(currInd + 1,
                freeTimeRemaining - currTask.getTotalTime(),
                taskTimeRemaining - currTask.getTotalTime(), weekDay,
                scheduleInfo);
        List<Integer> tasks = new ArrayList<>(currDay.getTasksPerDay());
        return new WeekScore(tasks, Double.POSITIVE_INFINITY);
      }
      return currDay;
    }

    double nextDayScore = 0;
    // checks if the remaining tasks can be completed in the next days, and if not
    // returns the current task on the current day
    if (!canComplete(currInd, weekDay + 1, taskTimeRemaining,
            scheduleInfo) && freeTimeRemaining != 0
            && AlgorithmUtil.dateToRelativeWeekDay(currTask.getTaskDeadline()) >= weekDay + 1) {
      if (currDay != null) {
        return currDay;
      }

      // splits the current task to assign it to the current day
      TaskBlock first = new TaskBlock(currTask.getTaskID(), freeTimeRemaining, currTask.getTaskDeadline(),
              false, currTask.getTitle());
      TaskBlock second = new TaskBlock(currTask.getTaskID(),currTask.getTotalTime() - freeTimeRemaining,
              currTask.getTaskDeadline(), false, currTask.getTitle());
      scheduleInfo.getTaskBlockList().remove(currInd);
      scheduleInfo.getTaskBlockList().add(currInd, first);
      scheduleInfo.getTaskBlockList().add(currInd + 1, second);
      return generateWeekScore(currInd, freeTimeRemaining, taskTimeRemaining, weekDay,
              scheduleInfo);
    } else if (!canComplete(currInd, weekDay + 1, taskTimeRemaining,
            scheduleInfo) && freeTimeRemaining == 0) {
      nextDayScore = Double.POSITIVE_INFINITY;
    }

    // finds the score for the current task if it is assigned to the next day
    WeekScore nextDay = generateWeekScore(currInd, scheduleInfo.getDailyFreeTime().get(weekDay + 1),
            taskTimeRemaining, weekDay + 1,
            scheduleInfo);

    // calculates the score and the tasks for the next day plus the current day
    Integer totalDayFreeTime = scheduleInfo.getDailyFreeTime().get(weekDay);
    double workTime = totalDayFreeTime - freeTimeRemaining;
//    double nextDayScore = nextDay.getScore();
    nextDayScore += nextDay.getScore();
    if (totalDayFreeTime != 0) {
      nextDayScore += Math.pow(Math.abs((workTime / totalDayFreeTime) - scheduleInfo.getBestWeekScore()),
              SCORE_PENALTY);
    }
    nextDay.getTasksPerDay().set(weekDay, currInd);

    // sets the current day from next day if currDay is null
    if (currDay == null) {
      List<Integer> dailyTasks = new ArrayList<>(nextDay.getTasksPerDay());
      if (workTime == 0) {
        dailyTasks.set(weekDay, 0);
      } else {
        dailyTasks.set(weekDay, currInd);
      }
      currDay = new WeekScore(dailyTasks, nextDayScore);
    }

    // returns the weekScore that has a better score
    if (currDay.getScore() <= nextDayScore) {
      return currDay;
    } else {
      List<Integer> dailyTasks = new ArrayList<>(nextDay.getTasksPerDay());
      if (workTime == 0) {
        dailyTasks.set(weekDay, 0);
      } else {
        dailyTasks.set(weekDay, currInd);
      }
      return new WeekScore(dailyTasks, nextDayScore);
    }
  }

  /**
   * Checks if the rest of the tasks can be completed if the
   * current task is started on the next day.
   *
   * @param ind - the index of the task
   * @param weekDay - the weekday to start at
   * @param remaining - the amount of task time remaining
   * @return true if can be completed, false if cannot be completed
   */
  private static boolean canComplete(int ind, int weekDay, int remaining,
                              ScheduleInfo scheduleInfo) throws ParseException {

    // creates an array for the amount of time remaining
    ArrayList<Integer> tempFreeTime = new ArrayList<>();

    // initializes the tempfreetime list
    for (int i = 0; i < scheduleInfo.getDaysInSchedule(); i++) {
      tempFreeTime.add(0);
    }

    // calculates the total amount of free time remaining
    int freeRestDays = 0;

    // sets the array and the total time free
    for (int i = weekDay; i < scheduleInfo.getDailyFreeTime().size(); i++) {
      tempFreeTime.set(i, scheduleInfo.getDailyFreeTime().get(i));
      freeRestDays += scheduleInfo.getDailyFreeTime().get(i);
    }

    // returns false if less time free than the remaining task time in the week
    if (freeRestDays < remaining) {
      return false;
    }

    // get the info about the current task
    int taskTimeRemaining = scheduleInfo.getTaskBlockList().get(ind).getTotalTime();
    long deadline = AlgorithmUtil.dateToRelativeWeekDay(scheduleInfo.getTaskBlockList().get(ind).getTaskDeadline());

    // iterates until taskTimeRemaining is zero or false was returned
    while (taskTimeRemaining > 0) {

      // checks if the current day has enough time to complete the task
      // and if not, returns false if due next day or moves onto the next task
      int currDayFree = tempFreeTime.get(weekDay);
      if (currDayFree >= taskTimeRemaining) {
        taskTimeRemaining = 0;
      } else if (deadline == weekDay + 1) {
        return false;
      } else {
        taskTimeRemaining -= currDayFree;
        tempFreeTime.set(weekDay, 0);
        weekDay += 1;
      }
    }
    return true;
  }
}
