package de.secretj12.turnierplaner.resources.jsonEntities.user;

import de.secretj12.turnierplaner.db.entities.groups.Group;

import java.util.List;
import java.util.stream.Stream;

public class jUserGroup {

    private long index;
    private List<jUserPlayer> player;
    private List<jUserGroupMatch> matches;

    public jUserGroup(Group group) {
        this.index = group.getIndex();
        this.player = group.getMatches().stream()
                .flatMap(match -> Stream.of(match.getTeamA().getPlayerA(), match.getTeamA().getPlayerB(), match.getTeamB().getPlayerB(), match.getTeamB().getPlayerA())).distinct()
                .map(jUserPlayer::new).toList();
        this.matches = group.getMatches().stream().map(jUserGroupMatch::new).toList();
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public List<jUserPlayer> getPlayer() {
        return player;
    }

    public void setPlayer(List<jUserPlayer> player) {
        this.player = player;
    }

    public List<jUserGroupMatch> getMatches() {
        return matches;
    }

    public void setMatches(List<jUserGroupMatch> matches) {
        this.matches = matches;
    }
}
