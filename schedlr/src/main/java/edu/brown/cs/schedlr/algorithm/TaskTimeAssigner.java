package edu.brown.cs.schedlr.algorithm;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static edu.brown.cs.schedlr.algorithm.ScheduleConstants.BREAK_CHUNK_SIZE;
import static edu.brown.cs.schedlr.algorithm.ScheduleConstants.ONE_MINUTE_IN_MILLIS;

/**
 * A class to assign times to tasks.
 */
public final class TaskTimeAssigner {

  private TaskTimeAssigner() {
  }

  /**
   * Creates a list of assigned task blocks given a list of tasks.
   *
   * @param tasksPerDay - the tasks
   * @param scheduleInfo - the other information about the schedule
   * @return the AssignedTaskBlock list
   * @throws ParseException if unable to parse dates
   */
  public static List<AssignedTaskBlock> assignTaskTimes(List<Integer> tasksPerDay,
                                                        ScheduleInfo scheduleInfo)
          throws ParseException {

    // sets up a new list for assigned tasks.
    List<AssignedTaskBlock> assignedTasks = new LinkedList<>();

    // creates vars for the start and end index for the task list.
    int start = 0;
    int end;

    // iterates through the days of the week
    for (int i = 0; i < scheduleInfo.getDaysInSchedule(); i++) {

      // gets the end index from the tasksPerDay list
      end = tasksPerDay.get(i);

      // finds the number of taskBlocks and FreeBlocks on that day
      List<TaskBlock> tasks = getTaskBlocksByDay(start, end, i, scheduleInfo.getTaskBlockList());
      List<FreeBlock> free = getFreeBlocksByDay(i, scheduleInfo.getFreeBlockList());

      // Assigns the tasks for that day.
      List<AssignedTaskBlock> dayTasks = assignSingleDay(tasks, free);

      // adds the new tasks to the assigned tasks list
      assignedTasks.addAll(dayTasks);

      // resets the start and end indices
      if (end != 0) {
        start = end;
      }
    }
    return assignedTasks;
  }

  /**
   * Assigns the tasks to free blocks for a given day.
   *
   * @param tasks - the list of tasks for the day.
   * @param free - the list of free blocks for the day
   * @return the assigned tasks
   */
  private static List<AssignedTaskBlock> assignSingleDay(List<TaskBlock> tasks,
                                                         List<FreeBlock> free) {

    // if no tasks, returns the empty list back
    if (tasks.isEmpty()) {
      return new LinkedList<>();
    }

    // creates a list to store the assigned tasks
    List<AssignedTaskBlock> allDayTasks = new LinkedList<>();

    // calculates how much free time
    int taskTime = AlgorithmUtil.totalTaskBlockTime(tasks);
    int freeBlocksTime = AlgorithmUtil.totalFreeBlockTime(free);
    int freeTime = freeBlocksTime - taskTime;

    // adds TaskBlocks to list of tasks for breaks and shuffles them in
    tasks.addAll(createFakeTasks(freeTime, tasks.get(0).getTaskDeadline()));
    Collections.shuffle(tasks);

    // iterates through each task to assign times
    for (TaskBlock task : tasks) {

      // gets the duration of the task
      int duration = task.getTotalTime();

      // loops until all of the task has been scheduled
      while (duration != 0) {

        // ensures that there is free time remaining
        if (!free.isEmpty()) {

          // gets the first freeblock
          FreeBlock first = free.get(0);

          // checks if the freeblock is longer than the duration and
          // assigns time/splits the freeblock
          if (first.getTotalTime() > duration) {
            Date start = first.getStartTime();
            long tInMillis = start.getTime();
            Date end = new Date(tInMillis + (duration * ONE_MINUTE_IN_MILLIS));
            task.setStartTime(start);
            task.setEndTime(end);
            first.setStartTime(end);
            AssignedTaskBlock assignedTask = new AssignedTaskBlock(task.getTaskID(), task.getTaskDeadline(),
                    start, end, task.getIsBreak(), task.getTitle());
            allDayTasks.add(assignedTask);
            duration = 0;

            // if the freeblock is not long enough, assigns the time, removes the freeblock
            // and sets part of the task
          } else {
            Date start = first.getStartTime();
            Date end = first.getEndTime();
            AssignedTaskBlock firstBlock = new AssignedTaskBlock(task.getTaskID(), task.getTaskDeadline(),
                    start, end, task.getIsBreak(), task.getTitle());
            free.remove(0);
            allDayTasks.add(firstBlock);
            duration -= first.getTotalTime();
          }

          // if there is not enough free time, ignores the task... (sets it as assigned)
        } else {
          duration = 0;
        }
      }
    }

    // returns the newly assigned tasks
    return allDayTasks;
  }

  /**
   * Creates tasks to represent breaks on a given day.
   *
   * @param freeTime - the amount of time to create breaks for
   * @param day - the day of the breaks
   * @return the list of TaskBlocks that represent breaks
   */
  private static List<TaskBlock> createFakeTasks(int freeTime, Date day) {
    List<TaskBlock> fakeTasks = new LinkedList<>();

    // calculates the number of breaks to create (30 min increments)
    int numTasks = freeTime / BREAK_CHUNK_SIZE;

    // iterates through the number of tasks and adds a break TaskBlock
    for (int i = 0; i < numTasks; i++) {
      int duration = BREAK_CHUNK_SIZE;

      // if it is the first break, adds any potential free time that remains
      if (i == 0) {
        duration += freeTime % BREAK_CHUNK_SIZE;
      }
      TaskBlock fake = new TaskBlock(-1, duration, day, true, "");
      fakeTasks.add(fake);
    }

    // returns the break TaskBlocks
    return fakeTasks;
  }


  /**
   * Coalesces the assigned blocks together.
   *
   * @param toCoalesce - the blocks to coalesce.
   * @return the new list of blocks
   */
  public static List<AssignedTaskBlock> coalesce(List<AssignedTaskBlock> toCoalesce) {

    // creates a new list for the new coalesced blocks
    List<AssignedTaskBlock> toReturn = new LinkedList<>();

    // a temporary list to store tasks that can be combined
    List<AssignedTaskBlock> canCombine = new LinkedList<>();

    // returns an empty list if the input list was empty
    if (toCoalesce.isEmpty()) {
      return new ArrayList<>();
    }

    // pulls the first task block and adds it to the can combine list
    AssignedTaskBlock first = toCoalesce.remove(0);
    canCombine.add(first);
    if (toCoalesce.isEmpty()) {
      toReturn.addAll(canCombine);
      return toReturn;
    }

    // iterates through all of the tasks in the toCoalesce list
    while (!toCoalesce.isEmpty()) {

      // pulls the next block
      AssignedTaskBlock next = toCoalesce.remove(0);

      // checks if it cannot be combined with the previous task (and if not, creates a
      // task block out of the current cancombine list
      if (!first.getEndTime().equals(next.getStartTime())
              || first.getTaskID() != next.getTaskID()
              || first.getIsBreak() != next.getIsBreak()) {
        AssignedTaskBlock combinedBlock = new AssignedTaskBlock(canCombine.get(0).getTaskID(),
                canCombine.get(0).getTaskDeadline(), canCombine.get(0).getStartTime(),
                canCombine.get(canCombine.size() - 1).getEndTime(),
                canCombine.get(0).getIsBreak(), canCombine.get(0).getTitle());
        toReturn.add(combinedBlock);
        canCombine = new LinkedList<>();
      }

      // if can be combined, adds to the list
      canCombine.add(next);
      first = next;
    }

    // creates the last block
    AssignedTaskBlock combinedBlock = new AssignedTaskBlock(canCombine.get(0).getTaskID(),
      canCombine.get(0).getTaskDeadline(), canCombine.get(0).getStartTime(),
      canCombine.get(canCombine.size() - 1).getEndTime(),
      canCombine.get(0).getIsBreak(), canCombine.get(0).getTitle());
    toReturn.add(combinedBlock);

    return toReturn;
  }

  /**
   * Finds the FreeBlocks for a given day.
   *
   * @param day - the day to find blocks of
   * @return the List of FreeBlocks for that day.
   */
  private static List<FreeBlock> getFreeBlocksByDay(int day, List<FreeBlock> freeBlockList) throws ParseException {
    List<FreeBlock> fBlocks = new LinkedList<>();

    // iterates through the FreeBlocks and checks if they are on the given day
    for (FreeBlock block :  freeBlockList) {
      if (AlgorithmUtil.dateToRelativeWeekDay(block.getDay()) == day) {
        fBlocks.add(block);
      }
    }
    return fBlocks;
  }

  /**
   * Finds the TaskBlocks for a given day.
   *
   * @param start - the start index relative to taskBlockList (inclusive)
   * @param end - the end index relative to taskBlockList (not inclusive)
   * @return the TaskBlocks for that day
   */
  private static List<TaskBlock> getTaskBlocksByDay(int start, int end, int day,
                                                    List<TaskBlock> taskBlockList)
          throws ParseException {
    List<TaskBlock> tasks = new ArrayList<>();
    for (int i = start; i < end; i++) {
      if (AlgorithmUtil.dateToRelativeWeekDay(taskBlockList.get(i).getTaskDeadline()) > day) {
        tasks.add(taskBlockList.get(i));
      }
    }
    return tasks;
  }
}
