package de.secretj12.turnierplaner.db.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "config")
public class Config {

  @Id private UUID id;

  @Column(name = "language")
  private String language;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }
}
