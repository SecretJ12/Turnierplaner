package de.secretj12.turnierplaner.resources.jsonEntities.user;

import de.secretj12.turnierplaner.db.entities.groups.Group;

import java.util.List;
import java.util.stream.Stream;

public class jUserGroup {

    private long index;
    private List<jUserTeam> teams;
    private List<jUserGroupMatch> matches;

    public jUserGroup(Group group) {
        this.index = group.getIndex();
        this.teams = group.getMatches().stream()
                .flatMap(match -> Stream.of(match.getTeamA(), match.getTeamB())).distinct()
                .map(jUserTeam::new).toList();
        this.matches = group.getMatches().stream().map(jUserGroupMatch::new).toList();
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public List<jUserTeam> getTeams() {
        return teams;
    }

    public void setTeams(List<jUserTeam> teams) {
        this.teams = teams;
    }

    public List<jUserGroupMatch> getMatches() {
        return matches;
    }

    public void setMatches(List<jUserGroupMatch> matches) {
        this.matches = matches;
    }
}
