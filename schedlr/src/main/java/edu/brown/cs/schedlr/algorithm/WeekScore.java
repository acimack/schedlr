package edu.brown.cs.schedlr.algorithm;

import java.util.List;

/**
 * A class to represent the score of a potential week schedule.
 */
public class WeekScore {

  // holds the last index for each day
  private final List<Integer> tasksPerDay;
  private final double score;

  /**
   * Constructor for WeekScore.
   * @param tasksPerDay - the list with the number of tasks for each day
   * @param score - the value that represents the score
   */
  public WeekScore(List<Integer> tasksPerDay, double score) {
    this.tasksPerDay = tasksPerDay;
    this.score = score;
  }

  /**
   * Accessor method for task list.
   *
   * @return int list representing how many tasks are on each day
   */
  public List<Integer> getTasksPerDay() {
    return this.tasksPerDay;
  }

  /**
   * Accessor method for score.
   *
   * @return the score for the potential week schedule.
   */
  public double getScore() {
    return this.score;
  }
}
