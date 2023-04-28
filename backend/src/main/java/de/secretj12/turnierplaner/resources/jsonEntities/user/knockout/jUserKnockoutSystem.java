package de.secretj12.turnierplaner.resources.jsonEntities.user.knockout;

import de.secretj12.turnierplaner.db.entities.Match;
import de.secretj12.turnierplaner.db.entities.competition.Team;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserTeam;
import jakarta.ws.rs.NotFoundException;

import java.util.*;

public class jUserKnockoutSystem {

    private List<jUserTeam> teams;
    private jUserKnockoutMatch finale;
    private jUserKnockoutMatch thirdPlace;

    public jUserKnockoutSystem(Match finale, Match thirdPlace) {
        if (finale == null)
            throw new NotFoundException("Finale was not found");
        if (thirdPlace == null)
            throw new NotFoundException("Game for third place was not found");

        Set<Team> teams = new HashSet<>();
        Queue<Match> matchQueue = new ArrayDeque<>();
        matchQueue.add(finale);
        matchQueue.add(thirdPlace);
        Match match;
        while ((match = matchQueue.poll()) != null) {
            teams.add(match.getTeamA());
            teams.add(match.getTeamB());
            if (match.getDependentOn() != null) {
                matchQueue.add(match.getDependentOn().getPreviousA());
                matchQueue.add(match.getDependentOn().getPreviousB());
            }
        }
        this.teams = teams.stream()
                .filter(Objects::nonNull)
                .map(jUserTeam::new).toList();
        this.finale = new jUserKnockoutMatch(finale);
        this.thirdPlace = new jUserKnockoutMatch(thirdPlace, false);
    }

    public List<jUserTeam> getTeams() {
        return teams;
    }

    public void setTeams(List<jUserTeam> teams) {
        this.teams = teams;
    }

    public jUserKnockoutMatch getFinale() {
        return finale;
    }

    public void setFinale(jUserKnockoutMatch finale) {
        this.finale = finale;
    }

    public jUserKnockoutMatch getThirdPlace() {
        return thirdPlace;
    }

    public void setThirdPlace(jUserKnockoutMatch thirdPlace) {
        this.thirdPlace = thirdPlace;
    }
}
