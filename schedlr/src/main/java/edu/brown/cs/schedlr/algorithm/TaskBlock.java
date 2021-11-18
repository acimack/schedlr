package edu.brown.cs.schedlr.algorithm;

import java.util.Date;

/**
 * A class to represent a block within a Task -- tasks are divided into TaskBlocks.
 */
public class TaskBlock implements ITimeBlock {

  private int duration;
  private Date taskDeadline;
  private boolean isBreak;
  private Date startTime = null;
  private Date endTime = null;
  private final String title;
  private final int taskID;

  /**
   * Constructor for the TaskBlock.
   */
  public TaskBlock(int taskID, int duration, Date taskDeadline, boolean isBreak, String title) {
    this.taskID = taskID;
    this.duration = duration;
    this.taskDeadline = taskDeadline;
    this.isBreak = isBreak;
    this.title = title;
  }

  @Override
  public int getTotalTime() {
    return this.duration;
  }

  public void setTotalTime(int time) {
    this.duration = time;
  }

  public Date getTaskDeadline() {
    return this.taskDeadline;
  }

  public void setStartTime(Date start) {
    this.startTime = start;
  }

  public boolean getIsBreak() {
    return this.isBreak;
  }

  public void setEndTime(Date end) {
    this.endTime = end;
  }

  public Date getStartTime() {
    return this.startTime;
  }

  public Date getEndTime() {
    return this.endTime;
  }

  public String getTitle() {
    return this.title;
  }

  public int getTaskID() {
    return taskID;
  }

  @Override
  public String toString() {
    return "TaskBlock{"
            + "duration=" + duration + "\n"
            + ", taskDeadline=" + taskDeadline + "\n"
            + ", isBreak=" + isBreak + "\n"
            + ", startTime=" + startTime + "\n"
            + ", endTime=" + endTime + "\n"
            + ", title=" + title
            + '}' + "\n";
  }
}
