package de.secretj12.turnierplaner.resources.jsonEntities.director.competition;

import de.secretj12.turnierplaner.db.entities.competition.Team;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserTeam;

import java.util.List;

public class jDirectorGroupsDivision {

    private List<List<jUserTeam>> groups;

    public jDirectorGroupsDivision() {
    }

    public jDirectorGroupsDivision(List<List<Team>> groups) {
        this.groups = groups.stream().map(group -> group.stream().map(jUserTeam::new).toList()).toList();
    }

    public List<List<jUserTeam>> getGroups() {
        return groups;
    }

    public void setGroups(List<List<jUserTeam>> groups) {
        this.groups = groups;
    }
}
