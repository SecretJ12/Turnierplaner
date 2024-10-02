package de.secretj12.turnierplaner.enums;

public enum AdminRole {
  USER("user"),
  REPORTER("reporter"),
  DIRECTOR("director"),
  ADMIN("admin");

  public final String label;

  AdminRole(String label) {
    this.label = label;
  }
}
