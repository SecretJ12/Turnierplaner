package de.secretj12.turnierplaner.resources.jsonEntities.user;

import de.secretj12.turnierplaner.db.entities.Competition;
import de.secretj12.turnierplaner.db.entities.Match;
import de.secretj12.turnierplaner.db.entities.groups.Group;

import java.util.List;

public class jUserGroup {

    private long index;
    private List<jUserGroupMatch> matches;

    public jUserGroup(Group group) {
        this.index = group.getIndex();
        this.matches = group.getMatches().stream().map(jUserGroupMatch::new).toList();
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public List<jUserGroupMatch> getMatches() {
        return matches;
    }

    public void setMatches(List<jUserGroupMatch> matches) {
        this.matches = matches;
    }
}
