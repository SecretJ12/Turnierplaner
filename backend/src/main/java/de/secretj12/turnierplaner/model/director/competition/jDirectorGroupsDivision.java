package de.secretj12.turnierplaner.model.director.competition;

import de.secretj12.turnierplaner.db.entities.competition.Team;
import de.secretj12.turnierplaner.model.user.jUserTeam;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class jDirectorGroupsDivision {

  private List<Set<jUserTeam>> groups;

  public jDirectorGroupsDivision() {}

  public jDirectorGroupsDivision(List<Set<Team>> groups) {
    this.groups =
        groups.stream()
            .map(group -> group.stream().map(jUserTeam::new).collect(Collectors.toSet()))
            .toList();
  }

  public List<Set<jUserTeam>> getGroups() {
    return groups;
  }

  public void setGroups(List<Set<jUserTeam>> groups) {
    this.groups = groups;
  }
}
