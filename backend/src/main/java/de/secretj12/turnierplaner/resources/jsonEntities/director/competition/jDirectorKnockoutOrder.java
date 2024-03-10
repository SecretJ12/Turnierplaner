package de.secretj12.turnierplaner.resources.jsonEntities.director.competition;

import de.secretj12.turnierplaner.db.entities.competition.Team;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserTeam;

import java.util.List;

public class jDirectorKnockoutOrder {

    private List<jUserTeam> teams;

    public jDirectorKnockoutOrder() {
    }

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
