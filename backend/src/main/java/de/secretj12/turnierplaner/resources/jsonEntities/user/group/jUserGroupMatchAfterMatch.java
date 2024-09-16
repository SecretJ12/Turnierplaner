package de.secretj12.turnierplaner.resources.jsonEntities.user.group;

import de.secretj12.turnierplaner.db.entities.Match;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserMatch;

public class jUserGroupMatchAfterMatch extends jUserMatch {

    private boolean winningPlayer;
    private jUserMatch previousA;
    private jUserMatch previousB;

    public jUserGroupMatchAfterMatch(Match match, boolean winner, boolean dependant) {
        super(match);

        this.winningPlayer = winner;
        if (dependant) {
            if (match.getDependentOn().getPreviousA().getFinalOfGroup() != null)
                this.previousA = new jUserGroupMatchAfterGroup(match.getDependentOn().getPreviousA());
            else
                this.previousA = new jUserGroupMatchAfterMatch(match.getDependentOn().getPreviousA(), match
                    .getDependentOn().isWinner());
            if (match.getDependentOn().getPreviousB().getFinalOfGroup() != null)
                this.previousB = new jUserGroupMatchAfterGroup(match.getDependentOn().getPreviousB());
            else
                this.previousB = new jUserGroupMatchAfterMatch(match.getDependentOn().getPreviousB(), match
                    .getDependentOn().isWinner());
        }
    }

    public jUserGroupMatchAfterMatch(Match match, boolean winner) {
        this(match, winner, true);
    }

    public boolean isWinningPlayer() {
        return winningPlayer;
    }

    public void setWinningPlayer(boolean winningPlayer) {
        this.winningPlayer = winningPlayer;
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
