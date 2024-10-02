package de.secretj12.turnierplaner.model.director;

import java.time.Instant;
import java.util.UUID;

public class jDirectorScheduleMatch {
  private UUID id;
  private String court;
  private Instant begin;
  private Instant end;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getCourt() {
    return court;
  }

  public void setCourt(String court) {
    this.court = court;
  }

  public Instant getBegin() {
    return begin;
  }

  public void setBegin(Instant begin) {
    this.begin = begin;
  }

  public Instant getEnd() {
    return end;
  }

  public void setEnd(Instant end) {
    this.end = end;
  }
}
