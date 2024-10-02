package de.secretj12.turnierplaner.model.user.competition;

import de.secretj12.turnierplaner.db.entities.competition.Competition;
import de.secretj12.turnierplaner.enums.*;
import jakarta.ws.rs.NotFoundException;

public class jUserCompetition {
  private String name;
  private String description;
  private CompetitionType type;
  private CompetitionMode mode;
  private CompetitionSignUp signUp;
  private NumberSets numberSets;
  private jUserConfigPlayerA playerA;
  private jUserConfigPlayerB playerB;
  private CreationProgress cProgress;

  public jUserCompetition() {}

  public jUserCompetition(Competition competition) {
    if (competition == null) throw new NotFoundException("Competition was not found");

    this.name = competition.getName();
    this.description = competition.getDescription();
    this.type = competition.getType();
    this.mode = competition.getMode();
    this.signUp = competition.getSignup();
    this.numberSets = competition.getNumberSets();
    this.playerA = new jUserConfigPlayerA();
    this.playerA.setSex(competition.getPlayerASex());
    this.playerA.setHasMinAge(competition.playerAhasMinAge());
    this.playerA.setMinAge(competition.getPlayerAminAge());
    this.playerA.setHasMaxAge(competition.playerAhasMaxAge());
    this.playerA.setMaxAge(competition.getPlayerAmaxAge());
    this.playerB = new jUserConfigPlayerB();
    this.playerB.setDifferent(competition.isPlayerBdifferent());
    this.playerB.setSex(competition.getPlayerBSex());
    this.playerB.setHasMinAge(competition.playerBhasMinAge());
    this.playerB.setMinAge(competition.getPlayerBminAge());
    this.playerB.setHasMaxAge(competition.playerBhasMaxAge());
    this.playerB.setMaxAge(competition.getPlayerBmaxAge());

    this.cProgress = competition.getcProgress();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public CompetitionType getType() {
    return type;
  }

  public void setType(CompetitionType type) {
    this.type = type;
  }

  public CompetitionMode getMode() {
    return mode;
  }

  public void setMode(CompetitionMode mode) {
    this.mode = mode;
  }

  public CompetitionSignUp getSignUp() {
    return signUp;
  }

  public void setSignUp(CompetitionSignUp signUp) {
    this.signUp = signUp;
  }

  public jUserConfigPlayerA getPlayerA() {
    return playerA;
  }

  public void setPlayerA(jUserConfigPlayerA playerA) {
    this.playerA = playerA;
  }

  public jUserConfigPlayerB getPlayerB() {
    return playerB;
  }

  public void setPlayerB(jUserConfigPlayerB playerB) {
    this.playerB = playerB;
  }

  public CreationProgress getcProgress() {
    return cProgress;
  }

  public void setcProgress(CreationProgress cProgress) {
    this.cProgress = cProgress;
  }

  public NumberSets getNumberSets() {
    return numberSets;
  }

  public void setNumberSets(NumberSets numberSets) {
    this.numberSets = numberSets;
  }
}
