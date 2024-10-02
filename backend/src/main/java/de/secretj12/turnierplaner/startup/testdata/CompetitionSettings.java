package de.secretj12.turnierplaner.startup.testdata;

import de.secretj12.turnierplaner.enums.CompetitionMode;
import de.secretj12.turnierplaner.enums.CompetitionType;
import de.secretj12.turnierplaner.enums.SexFilter;

class CompetitionSettings {
  private String name;
  private CompetitionType competitionType;
  private CompetitionMode competitionMode;
  private SexFilter sexFilter;
  private int teamNumbers;
  private boolean registerIndividual;
  private AGE_RESTR ageRestr;
  private boolean differentConditions;
  private int numberOfGroups;

  public CompetitionSettings(
      String description,
      CompetitionType competitionType,
      CompetitionMode competitionMode,
      SexFilter sexFilter,
      int playerNumbers,
      boolean registerIndividual,
      AGE_RESTR ageRestr,
      boolean differentConditions,
      int numberOfGroups) {
    this.name = description;
    this.competitionType = competitionType;
    this.competitionMode = competitionMode;
    this.sexFilter = sexFilter;
    this.teamNumbers = playerNumbers;
    this.registerIndividual = registerIndividual;
    this.ageRestr = ageRestr;
    this.differentConditions = differentConditions;
    this.numberOfGroups = numberOfGroups;
  }

  public String getName() {
    return name;
  }

  public CompetitionType getCompetitionType() {
    return competitionType;
  }

  public CompetitionMode getCompetitionMode() {
    return competitionMode;
  }

  public SexFilter getSex() {
    return sexFilter;
  }

  public int getTeamNumbers() {
    return teamNumbers;
  }

  public boolean isRegisterIndividual() {
    return registerIndividual;
  }

  public AGE_RESTR getAgeRestr() {
    return ageRestr;
  }

  public boolean isDifferentConditions() {
    return differentConditions;
  }

  public int getNumberOfGroups() {
    return numberOfGroups;
  }
}
