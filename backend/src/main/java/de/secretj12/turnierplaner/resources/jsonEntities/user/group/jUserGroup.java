package de.secretj12.turnierplaner.resources.jsonEntities.user.group;

import de.secretj12.turnierplaner.db.entities.competition.Team;
import de.secretj12.turnierplaner.db.entities.groups.Group;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserMatch;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class jUserGroup {

    private long index;
    private List<UUID> teams;
    private List<jUserMatch> matches;

    public jUserGroup(Group group) {
        this.index = group.getIndex();
        this.teams = group.getMatches().stream()
                .flatMap(match -> Stream.of(match.getTeamA(), match.getTeamB())).distinct()
                .map(Team::getId).toList();
        this.matches = group.getMatches().stream().map(jUserMatch::new).toList();
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public List<UUID> getTeams() {
        return teams;
    }

    public void setTeams(List<UUID> teams) {
        this.teams = teams;
    }

    public List<jUserMatch> getMatches() {
        return matches;
    }

    public void setMatches(List<jUserMatch> matches) {
        this.matches = matches;
    }
}
