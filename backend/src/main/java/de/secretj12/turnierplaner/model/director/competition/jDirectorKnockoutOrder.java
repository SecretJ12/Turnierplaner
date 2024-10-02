package de.secretj12.turnierplaner.model.director.competition;

import de.secretj12.turnierplaner.db.entities.competition.Team;
import de.secretj12.turnierplaner.model.user.jUserTeam;
import java.util.List;

public class jDirectorKnockoutOrder {

  private List<jUserTeam> teams;

  public jDirectorKnockoutOrder() {}

  public jDirectorKnockoutOrder(List<Team> teams) {
    this.teams = teams.stream().map(jUserTeam::new).toList();
  }

  public List<jUserTeam> getTeams() {
    return teams;
  }

  public void setTeams(List<jUserTeam> teams) {
    this.teams = teams;
  }
}
