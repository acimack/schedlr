package edu.brown.cs.schedlr.algorithm;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static edu.brown.cs.schedlr.algorithm.ScheduleConstants.ONE_MINUTE_IN_MILLIS;
import static edu.brown.cs.schedlr.algorithm.ScheduleConstants.TASK_BLOCK_CHUNK_SIZE;

/**
 * A class that holds all of  the information for a given schedule.
 */
public class ScheduleInfo {

  // fields for the classs
  private final int daysInSchedule;

  private final List<FreeBlock> freeBlockList;
  private final List<TaskBlock> taskBlockList;
  private final List<Integer> dailyFreeTime;

  private final double bestWeekScore;
  private final int totalTaskTime;

  private final int dailyStart;
  private final int dailyEnd;

  // fields for testing
  private final List<FreeBlock> freeBlockListInitial;
  private final List<TaskBlock> taskBlockListInitial;
  private final List<Integer> dailyFreeTimeInitial;
  private final int totalTaskTimeInitial;


  /**
   * Constructor for ScheduleInfo.
   *
   * @param taskList - the list of tasks.
   * @param eventList - the list of events.
   * @param dailyStart - the time each day starts (in minutes).
   * @param dailyEnd - the time each day ends (in minutes).
   */
  public ScheduleInfo(List<Task> taskList, List<BusyBlock> eventList,
                      int dailyStart, int dailyEnd) throws ParseException {
    this.dailyStart = dailyStart;
    this.dailyEnd = dailyEnd;

    // takes in the tasks to be completed and splits
    // them into hour long chunks and sorts by due date
    taskBlockList = splitTasks(taskList);
    taskBlockList.sort(Comparator.comparing(TaskBlock::getTaskDeadline));
    taskBlockListInitial = new LinkedList<>(taskBlockList);

    // finds the number of days in the schedule
    if (taskBlockList.isEmpty()) {
      this.daysInSchedule = 0;
    } else {
      Date lastTaskDate = taskBlockList.get(taskBlockList.size() - 1).getTaskDeadline();
      long lastTaskLong = AlgorithmUtil.dateToRelativeWeekDay(lastTaskDate);
      this.daysInSchedule = Math.toIntExact(lastTaskLong);
    }

    this.freeBlockList = createFreeBlocksFromEvents(eventList);
    freeBlockListInitial = new LinkedList<>(freeBlockList);

    // creates an array of how many minutes are free each day of the week
    this.dailyFreeTime = calculateDailyFreeTime();
    dailyFreeTimeInitial = new LinkedList<>(dailyFreeTime);

    int totalFreeTime = AlgorithmUtil.totalFreeBlockTime(freeBlockList);
    this.totalTaskTime = AlgorithmUtil.totalTaskBlockTime(taskBlockList);

    totalTaskTimeInitial = totalTaskTime;

    // goal for ratio of work to free time on each day
    this.bestWeekScore = Math.abs((double) totalTaskTime / (double) totalFreeTime);
  }

  /**
   * Creates free blocks from a list of events.
   *
   * @param events - the events that represent busy time.
   * @return the freeblock list
   */
   private List<FreeBlock> createFreeBlocksFromEvents(List<BusyBlock> events) throws ParseException {

    // creates new list for free blocks
    List<FreeBlock> freeBlocks = new ArrayList<>();

    // sorts events by deadline
    events.sort(Comparator.comparing(BusyBlock::getStart));

    // finds the current date
    Calendar cal = Calendar.getInstance();
    Date currDate = AlgorithmUtil.zeroesDate(cal.getTime());

    // finds the first event
    BusyBlock currEvent = null;
    if (!events.isEmpty()) {
      currEvent = events.remove(0);
      // removes the tasks for any day prior to the current day
      while (AlgorithmUtil.dateToRelativeWeekDay(currEvent.getStart())
              < AlgorithmUtil.dateToRelativeWeekDay(currDate)) {
        if (events.isEmpty()) {
          break;
        } else {
          currEvent = events.remove(0);
        }
      }
    }

     // iterates through the days in the schedule
    for (int i = 0; i < daysInSchedule; i++) {

      // initializes a potential start and end date
      Date start = new Date(currDate.getTime() + (dailyStart * ONE_MINUTE_IN_MILLIS));
      Date end = new Date(currDate.getTime() + (dailyEnd * ONE_MINUTE_IN_MILLIS));

      // sets start to null if the current time is after that of the current day
      if (AlgorithmUtil.dateToRelativeWeekDay(currDate) == 0) {
        Date exactTimeSecs = cal.getTime();
        cal.setTime(exactTimeSecs);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date exactTime = cal.getTime();
        if ((exactTime.getTime() - currDate.getTime()) / ONE_MINUTE_IN_MILLIS > dailyEnd) {
          start = null;
        } else if ((exactTime.getTime() - currDate.getTime()) / ONE_MINUTE_IN_MILLIS > dailyStart) {
          start = exactTime;
        }
      }

      // if the current event is null (no events) creates a free block for the whole day
      if (currEvent == null) {
        if (start != null) {
          FreeBlock fBlock = new FreeBlock(currDate, start, end);
          freeBlocks.add(fBlock);
        }

        // the current event is not the current day (no events) creates
        // a free block for the whole day
      } else if (AlgorithmUtil.dateToRelativeWeekDay(currEvent.getStart())
              != AlgorithmUtil.dateToRelativeWeekDay(currDate)) {
        if (start != null) {
          FreeBlock fBlock = new FreeBlock(currDate, start, end);
          freeBlocks.add(fBlock);
        }
      } else {
        if (start != null) {

          // iterates through the events that are on that day
          while (AlgorithmUtil.dateToRelativeWeekDay(currEvent.getStart())
                  == AlgorithmUtil.dateToRelativeWeekDay(currDate)) {

            // gets the end time from the start time of the current event
            end = currEvent.getStart();

            // checks that the start is before end
            if (((start.getTime() - currDate.getTime()) < (end.getTime() - currDate.getTime()))) {

              if (((start.getTime() - currDate.getTime()) / ONE_MINUTE_IN_MILLIS < dailyStart
                      && (end.getTime() - currDate.getTime()) / ONE_MINUTE_IN_MILLIS <= dailyStart)
              || ((start.getTime() - currDate.getTime()) / ONE_MINUTE_IN_MILLIS >= dailyEnd
                      && (end.getTime() - currDate.getTime()) / ONE_MINUTE_IN_MILLIS > dailyEnd)) {
                start = null;
              } else if ((start.getTime() - currDate.getTime()) / ONE_MINUTE_IN_MILLIS < dailyStart) {
                start = new Date(currDate.getTime() + (dailyStart * ONE_MINUTE_IN_MILLIS));
              } else if ((end.getTime() - currDate.getTime()) / ONE_MINUTE_IN_MILLIS > dailyEnd) {
                end = new Date(currDate.getTime() + (dailyEnd * ONE_MINUTE_IN_MILLIS));
              }
            } else {
              start = null;
            }
            if (start != null) {
              FreeBlock fBlock = new FreeBlock(currDate, start, end);
              freeBlocks.add(fBlock);
              if (currEvent.getEnd().getTime() > start.getTime()) {
                start = currEvent.getEnd();
              }
            } else {
              start = currEvent.getEnd();
            }
            if (events.isEmpty()) {
              break;
            } else {
              currEvent = events.remove(0);
            }
          }

          // makes free block for the rest of the day (after events)
          if ((start.getTime() - currDate.getTime()) / ONE_MINUTE_IN_MILLIS < dailyEnd) {
            if ((start.getTime() - currDate.getTime()) / ONE_MINUTE_IN_MILLIS < dailyStart) {
              start = new Date(currDate.getTime() + (dailyStart * ONE_MINUTE_IN_MILLIS));
            }
            Date endTime = new Date(currDate.getTime() + (dailyEnd * ONE_MINUTE_IN_MILLIS));
            FreeBlock fBlock = new FreeBlock(currDate, start, endTime);
            freeBlocks.add(fBlock);
          }
        } else {

          // removes the tasks for that day if the times of the task are not in the daily r ange
          while (AlgorithmUtil.dateToRelativeWeekDay(currEvent.getStart())
                  == AlgorithmUtil.dateToRelativeWeekDay(currDate)) {
            if (events.isEmpty()) {
              break;
            } else {
              currEvent = events.remove(0);
            }
          }
        }
      }

      // moves to the next day
      cal.add(Calendar.DAY_OF_YEAR, 1);
      currDate = AlgorithmUtil.zeroesDate(cal.getTime());
    }

    // returns the created free blocks
    return freeBlocks;
  }


  /**
   * Splits tasks into TaskBlocks so that tasks become spread out.
   *
   * @param tasks - the list of Tasks to be split
   * @return the list of TaskBlocks that have been separated
   */
  private List<TaskBlock> splitTasks(List<Task> tasks) {

    if (tasks.isEmpty()) {
      return new LinkedList<>();
    }

    // initialize a new list ofr the split blocks
    List<TaskBlock> allTaskBlocks = new ArrayList<>();

    // iterate through each task to create it's task blocks
    for (Task task : tasks) {

      Date deadline = task.getDeadline();

      // calculate how many blocks are needed based off of the duration of the task
      int timeToComplete = task.getTimeToComplete();
      int blocks = timeToComplete / TASK_BLOCK_CHUNK_SIZE;

      TaskBlock currBlock;

      // create a task from the remainder time
      if (blocks == 0) {
        currBlock = new TaskBlock(task.getTaskID(), timeToComplete, deadline, false, task.getTitle());
        allTaskBlocks.add(currBlock);
      } else {

        // iterate through the number of blocks needed
        for (int i = 1; i <= blocks; i++) {
          int duration = TASK_BLOCK_CHUNK_SIZE;

          // add to the duration the remainder time if it's the first block
          if (i == 1) {
            duration += (timeToComplete % TASK_BLOCK_CHUNK_SIZE);
          }

          // create a block and add it to the list of blocks
          currBlock = new TaskBlock(task.getTaskID(), duration, deadline, false, task.getTitle());
          allTaskBlocks.add(currBlock);
        }
      }
    }

    // returns the split tasks
    return allTaskBlocks;
  }

  /**
   * Calculates the free time for each day of the week.
   *
   * @return the list of free time for each day of the week
   */
  private List<Integer> calculateDailyFreeTime() throws ParseException {

    // a list to store the daily times
    List<Integer> dailyTime = new ArrayList<>();

    int date;

    // iterates through the free blocks
    for (FreeBlock block : freeBlockList) {

      // gets the date from the current block
      date = Math.toIntExact(AlgorithmUtil.dateToRelativeWeekDay(block.getDay()));

      // adds zeros for any missed days
      if (dailyTime.size() <= date) {
        for (int i = dailyTime.size(); i <= date; i++) {
          dailyTime.add(0);
        }
      }

      // adds the current day time to the time already there
      int currTime = dailyTime.get(date);
      currTime += block.getTotalTime();
      dailyTime.set(date, currTime);
    }

    // adds zeros for the rest of the days
    if (dailyTime.size() < daysInSchedule) {
      for (int i = dailyTime.size(); i < daysInSchedule; i++) {
        dailyTime.add(0);
      }
    }
    return dailyTime;
  }

  public List<FreeBlock> getFreeBlockList() {
    return freeBlockList;
  }

  public List<TaskBlock> getTaskBlockList() {
    return taskBlockList;
  }

  public int getDaysInSchedule() {
    return daysInSchedule;
  }

  public int getTotalTaskTime() {
    return totalTaskTime;
  }

  public List<Integer> getDailyFreeTime() {
    return dailyFreeTime;
  }

  public double getBestWeekScore() {
    return bestWeekScore;
  }


  // for testing:
  public List<FreeBlock> getFreeBlockListInitial() {
    return freeBlockListInitial;
  }

  public List<TaskBlock> getTaskBlockListInitial() {
    return taskBlockListInitial;
  }

  public List<Integer> getDailyFreeTimeInitial() {
    return dailyFreeTimeInitial;
  }

  public int getTotalTaskTimeInitial() {
    return totalTaskTimeInitial;
  }
}
