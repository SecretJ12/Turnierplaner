package de.secretj12.turnierplaner.resources.jsonEntities.user.group;

import de.secretj12.turnierplaner.db.entities.Match;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserMatch;

public class jUserGroupMatchAfterMatch extends jUserMatch {

    private jUserMatch previousA;
    private jUserMatch previousB;

    public jUserGroupMatchAfterMatch(Match match, boolean dependant) {
        super(match);

        if (dependant) {
            if (match.getDependentOn().getPreviousA().getFinalOfGroup() != null)
                this.previousA = new jUserGroupMatchAfterGroup(match.getDependentOn().getPreviousA());
            else
                this.previousA = new jUserGroupMatchAfterMatch(match.getDependentOn().getPreviousA());
            if (match.getFinalOfGroup() != null)
                this.previousB = new jUserGroupMatchAfterGroup(match.getDependentOn().getPreviousB());
            else
                this.previousB = new jUserGroupMatchAfterMatch(match.getDependentOn().getPreviousB());
        }
    }

    public jUserGroupMatchAfterMatch(Match match) {
        this(match, true);
    }

    public jUserMatch getPreviousA() {
        return previousA;
    }

    public void setPreviousA(jUserMatch previousA) {
        this.previousA = previousA;
    }

    public jUserMatch getPreviousB() {
        return previousB;
    }

    public void setPreviousB(jUserMatch previousB) {
        this.previousB = previousB;
    }
}
