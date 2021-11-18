package edu.brown.cs.schedlr.algorithm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class representing database of all schedlr tasks and object.
 * - algorithm lives here
 */
public class Task {
  private String userID;
  private int calendarID;
  private int eventID;
  // task start and end time
  private Date startTime;
  private Date endTime;

  // alg parameters
  private int label;
  private Date deadline;
  private int timeToComplete;
  private Double priorityScore;
  private int difficultyScore;
  private boolean isComplete;
  private List<Task> subtaskList;
  private Double hoursLeftToAllocate;

  private List<TaskBlock> taskBlockList;
  private final String title;
  private final int taskID;

  public Task(int taskID, int duration, Date deadline, String title) {
    this.taskID = taskID;
    this.timeToComplete = duration;
    this.deadline = deadline;
    this.title = title;
    this.isComplete = false;
  }

  public Task(int taskID, int duration, Date deadline, String title, boolean isComplete) {
    this.taskID = taskID;
    this.timeToComplete = duration;
    this.deadline = deadline;
    this.title = title;
    this.isComplete = isComplete;
  }

  public void setTaskBlockList(List<TaskBlock> taskList) {
    this.taskBlockList = new ArrayList<>(taskList);
  }

  public int getTimeToComplete() {
    return this.timeToComplete;
  }

  public Date getDeadline() {
    return this.deadline;
  }

  public String getTitle() {
    return this.title;
  }

  public int getTaskID() {
    return taskID;
  }

  public boolean isComplete() {
    return this.isComplete;
  }

  @Override
  public String toString() {
    return "Task{" +
            "userID='" + userID + '\'' +
            ", deadline=" + deadline +
            ", timeToComplete=" + timeToComplete +
            ", title='" + title + '\'' +
            ", taskID=" + taskID +
            '}';
  }
}
