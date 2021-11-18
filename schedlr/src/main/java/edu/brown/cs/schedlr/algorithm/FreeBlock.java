package edu.brown.cs.schedlr.algorithm;

import java.util.Date;

/**
 * Class representing an free time block object.
 */
public class FreeBlock implements ITimeBlock {
  private int eventID;
  private String name;
  private String label;
//  private int duration;

  private Date startTime;
  private Date endTime;

  private Date day;

  /**
   * Event object constructor.
   */
  public FreeBlock(Date day, Date start, Date end) {
//    this.duration = duration;

    this.day = day;
    this.startTime = start;
    this.endTime = end;
  }

  @Override
  public int getTotalTime() {
    // todo: or calculate using start and end time
    long diff = endTime.getTime() - startTime.getTime();
    long diffMinutes = diff / 60000;
    return Math.toIntExact(diffMinutes);
  }

  public Date getDay() {
    return this.day;
  }

//  public int getDuration() {
//    long diff = endTime.getTime() - startTime.getTime();
//    long diffMinutes = diff / (60 * 1000) % 60;
//    return Math.toIntExact(diffMinutes);
//  }

  public void setStartTime(Date start) {
    this.startTime = start;
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

  @Override
  public String toString() {
    return "FreeBlock{" + "\n" +
            "eventID=" + eventID + "\n" +
            ", name='" + name + '\'' + "\n" +
            ", label='" + label + '\'' + "\n" +
            ", startTime=" + startTime + "\n" +
            ", endTime=" + endTime + "\n" +
            ", day=" + day + "\n" +
            '}' + "\n";
  }
}
