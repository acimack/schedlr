package edu.brown.cs.schedlr.algorithm;

import java.text.ParseException;
import java.util.List;

/**
 * Class representing the schedule object.
 */
public class Schedule {

  private final ScheduleInfo scheduleInfo;
  private final List<AssignedTaskBlock> assignedTaskBlocks;

  /**
   * Index constructor.
   *
   * @param taskList - the list of tasks to be scheduled.
   * @param eventList - the list of existing events.
   * @param dailyStart - what time the user starts working (minutes)
   * @param dailyEnd - what time the user stops working (minutes)
   */
  public Schedule(List<Task> taskList, List<BusyBlock> eventList, int dailyStart, int dailyEnd) throws ParseException {
    this.scheduleInfo = new ScheduleInfo(taskList, eventList, dailyStart, dailyEnd);

    this.assignedTaskBlocks = this.generateSchedule();
  }


  /**
   * Generates the schedule.
   *
   * @return the list of TaskBlock that have times associated with them
   */
  private List<AssignedTaskBlock> generateSchedule() throws ParseException {

    // gets the best schedule
    WeekScore score = this.createScore();
    System.out.println(score.getScore());
    System.out.println(score.getTasksPerDay());

    List<AssignedTaskBlock> assignedTimes = TaskTimeAssigner.assignTaskTimes(score.getTasksPerDay(), scheduleInfo);

    return TaskTimeAssigner.coalesce(assignedTimes);

  }

  /**
   * Creates a score for the given schedule info (containing the tasks per day).
   *
   * @return the WeekScore
   */
  private WeekScore createScore() throws ParseException {
    int freeTimeRemaining = 0;
    if (!scheduleInfo.getDailyFreeTime().isEmpty()) {
      freeTimeRemaining = scheduleInfo.getDailyFreeTime().get(0);
    }
    int weekDay = 0;

    return WeekScoreGenerator.generateWeekScore(0, freeTimeRemaining,
            scheduleInfo.getTotalTaskTime(), weekDay, scheduleInfo);
  }

  /**
   * Gets the assigned task blocks.
   *
   * @return the assignedTasksBlocks
   */
  public List<AssignedTaskBlock> getAssignedTaskBlocks() {
    return this.assignedTaskBlocks;
  }
}
