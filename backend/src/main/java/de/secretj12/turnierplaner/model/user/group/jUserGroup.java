package de.secretj12.turnierplaner.model.user.group;

import de.secretj12.turnierplaner.db.entities.groups.Group;
import de.secretj12.turnierplaner.model.user.jUserMatch;
import de.secretj12.turnierplaner.model.user.jUserTeamGroupResult;
import de.secretj12.turnierplaner.tools.GroupTools;

import java.util.List;

public class jUserGroup {

    private byte index;
    private List<jUserMatch> matches;
    private List<jUserTeamGroupResult> results;

    public jUserGroup(Group group, GroupTools tools) {
        this.index = group.getIndex();
        this.matches = group.getMatches().stream().map(jUserMatch::new).toList();
        this.results = tools.determineGropuResults(group);
    }

    public byte getIndex() {
        return index;
    }

    public void setIndex(byte index) {
        this.index = index;
    }

    public List<jUserMatch> getMatches() {
        return matches;
    }

    public void setMatches(List<jUserMatch> matches) {
        this.matches = matches;
    }

    public List<jUserTeamGroupResult> getResults() {
        return results;
    }

    public void setResults(List<jUserTeamGroupResult> results) {
        this.results = results;
    }
}
