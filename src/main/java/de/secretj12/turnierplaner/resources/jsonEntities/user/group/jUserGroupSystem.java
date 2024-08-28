package de.secretj12.turnierplaner.resources.jsonEntities.user.group;

import de.secretj12.turnierplaner.db.entities.Match;
import de.secretj12.turnierplaner.db.entities.groups.Group;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserMatch;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserTeam;

import java.util.List;
import java.util.stream.Stream;

public class jUserGroupSystem {

    private List<jUserTeam> teams;
    private List<jUserGroup> groups;
    private jUserMatch finale;
    private jUserMatch thirdPlace;

    public jUserGroupSystem(List<Group> groups, Match finale, Match thirdPlace) {
        this.teams = groups.stream()
            .flatMap(group -> group.getMatches().stream()
                .flatMap(match -> Stream.of(match.getTeamA(), match.getTeamB())))
            .distinct()
            .map(jUserTeam::new)
            .toList();
        this.groups = groups.stream().map(jUserGroup::new).toList();

        if (finale.getFinalOfGroup() != null)
            this.finale = new jUserGroupMatchAfterGroup(finale);
        else
            this.finale = new jUserGroupMatchAfterMatch(finale, finale.getDependentOn().isWinner());
        if (thirdPlace != null)
            if (thirdPlace.getFinalOfGroup() != null)
                this.thirdPlace = new jUserGroupMatchAfterGroup(thirdPlace);
            else
                this.thirdPlace = new jUserGroupMatchAfterMatch(thirdPlace, thirdPlace.getDependentOn()
                    .isWinner(), false);
    }

    public List<jUserTeam> getTeams() {
        return teams;
    }

    public void setTeams(List<jUserTeam> teams) {
        this.teams = teams;
    }

    public List<jUserGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<jUserGroup> groups) {
        this.groups = groups;
    }

    public jUserMatch getFinale() {
        return finale;
    }

    public void setFinale(jUserMatch finale) {
        this.finale = finale;
    }

    public jUserMatch getThirdPlace() {
        return thirdPlace;
    }

    public void setThirdPlace(jUserMatch thirdPlace) {
        this.thirdPlace = thirdPlace;
    }
}
