package edu.brown.cs.schedlr.algorithm;

import java.util.Date;

public class BusyBlock {

  private final Date start;
  private final Date end;

  public BusyBlock(Date start, Date end) {
    this.start = start;
    this.end = end;
  }

  public Date getStart() {
    return start;
  }

  public Date getEnd() {
    return end;
  }

  @Override
  public String toString() {
    return "BusyBlock{" + "\n" +
            "start=" + start + "\n" +
            ", end=" + end + "\n" +
            '}' + "\n";
  }
}
