package de.secretj12.turnierplaner.db.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "default_config")
public class DefaultConfig {

  @Id private int id;

  private String title;
  private String language;
  private boolean adminVerificationNeeded;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public boolean isAdminVerificationNeeded() {
    return adminVerificationNeeded;
  }

  public void setAdminVerificationNeeded(boolean adminVerificationNeeded) {
    this.adminVerificationNeeded = adminVerificationNeeded;
  }
}
