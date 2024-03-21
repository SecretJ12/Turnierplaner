package de.secretj12.turnierplaner.resources.jsonEntities.director.competition;

import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserTeam;

import java.util.List;

public class jDirectorGroupsDivision {

    private List<List<jUserTeam>> groups;

    public List<List<jUserTeam>> getGroups() {
        return groups;
    }

    public void setGroups(List<List<jUserTeam>> groups) {
        this.groups = groups;
    }
}
