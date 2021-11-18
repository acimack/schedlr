package edu.brown.cs.schedlr.algorithm;

import java.util.Date;

/**
 * A class for AssignedTaskBlock.
 */
public class AssignedTaskBlock {

  // the fields for the AssignedTaskBlock
  private final Date taskDeadline;
  private final Date startTime;
  private final Date endTime;
  private final boolean isBreak;
  private final String title;
  private final int taskID;
  private boolean isComplete = false;

  /**
   * Constructor for the AssignedTaskBlock without the isComplete boolean.
   * @param taskID - the taskID
   * @param taskDeadline - the deadline
   * @param start - the start of the block
   * @param end - the end of the block
   * @param isBreak - if the block is a break
   * @param title - the title of the task
   */
  public AssignedTaskBlock(int taskID, Date taskDeadline, Date start, Date end,
                           boolean isBreak, String title) {
    this.taskID = taskID;
    this.taskDeadline = taskDeadline;
    this.startTime = start;
    this.endTime = end;
    this.isBreak = isBreak;
    this.title = title;
  }

  /**
   * Constructor for the AssignedTaskBlock with the isComplete boolean.
   * @param taskID - the taskID
   * @param taskDeadline - the deadline
   * @param start - the start of the block
   * @param end - the end of the block
   * @param isBreak - if the block is a break
   * @param title - the title of the task
   * @param isComplete - if the block has been completed
   */
  public AssignedTaskBlock(int taskID, Date taskDeadline, Date start, Date end,
                           boolean isBreak, String title, boolean isComplete) {
    this.taskID = taskID;
    this.taskDeadline = taskDeadline;
    this.startTime = start;
    this.endTime = end;
    this.isBreak = isBreak;
    this.title = title;
    this.isComplete = isComplete;
  }


  /**
   * Gets the task deadline.
   * @return the task deadline
   */
  public Date getTaskDeadline() {
    return this.taskDeadline;
  }

  /**
   * Gets the block start time.
   * @return the startTime
   */
  public Date getStartTime() {
    return this.startTime;
  }

  /**
   * Gets the block end time.
   * @return the endTime
   */
  public Date getEndTime() {
    return this.endTime;
  }

  /**
   * Gets the taskID.
   * @return the taskID
   */
  public int getTaskID() {
    return taskID;
  }

  /**
   * Gets the title.
   * @return the title
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * Gets the isBreak field.
   * @return if isBreak
   */
  public boolean getIsBreak() {
    return this.isBreak;
  }

  /**
   * Gets the isComplete field.
   * @return if isComplete
   */
  public boolean isComplete() {
    return isComplete;
  }

  @Override
  public String toString() {
    return "AssignedTaskBlock{" + "\n"
            + "taskID=" + taskID + "\n"
            + "taskDeadline=" + taskDeadline + "\n"
            + ", startTime=" + startTime + "\n"
            + ", endTime=" + endTime + "\n"
            + ", isBreak=" + isBreak + "\n"
            + ", title='" + title + '\''
            + '}' + "\n";
  }
}
