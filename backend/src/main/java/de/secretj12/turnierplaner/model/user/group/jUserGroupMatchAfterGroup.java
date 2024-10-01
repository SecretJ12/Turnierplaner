package de.secretj12.turnierplaner.model.user.group;

import de.secretj12.turnierplaner.db.entities.Match;
import de.secretj12.turnierplaner.model.user.jUserMatch;

public class jUserGroupMatchAfterGroup extends jUserMatch {

    private int pos;
    private long groupA;
    private long groupB;

    public jUserGroupMatchAfterGroup(Match match) {
        super(match);

        this.pos = match.getFinalOfGroup().getPos();
        this.groupA = match.getFinalOfGroup().getGroupA().getIndex();
        this.groupB = match.getFinalOfGroup().getGroupB().getIndex();
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public long getGroupA() {
        return groupA;
    }

    public void setGroupA(long groupA) {
        this.groupA = groupA;
    }

    public long getGroupB() {
        return groupB;
    }

    public void setGroupB(long groupB) {
        this.groupB = groupB;
    }
}
