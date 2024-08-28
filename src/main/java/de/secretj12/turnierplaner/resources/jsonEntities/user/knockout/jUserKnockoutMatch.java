package de.secretj12.turnierplaner.resources.jsonEntities.user.knockout;

import de.secretj12.turnierplaner.db.entities.Match;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserMatch;

public class jUserKnockoutMatch extends jUserMatch {

    private boolean winningPlayer;
    private jUserKnockoutMatch previousA;
    private jUserKnockoutMatch previousB;

    public jUserKnockoutMatch() {
    }

    public jUserKnockoutMatch(Match match) {
        this(match, true);
    }

    public jUserKnockoutMatch(Match match, boolean dependant) {
        super(match);

        if (dependant && match.getDependentOn() != null) {
            this.winningPlayer = match.getDependentOn().isWinner();
            this.previousA = new jUserKnockoutMatch(match.getDependentOn().getPreviousA());
            this.previousB = new jUserKnockoutMatch(match.getDependentOn().getPreviousB());
        }
    }

    public boolean isWinningPlayer() {
        return winningPlayer;
    }

    public void setWinningPlayer(boolean winningPlayer) {
        this.winningPlayer = winningPlayer;
    }

    public jUserKnockoutMatch getPreviousA() {
        return previousA;
    }

    public void setPreviousA(jUserKnockoutMatch previousA) {
        this.previousA = previousA;
    }

    public jUserKnockoutMatch getPreviousB() {
        return previousB;
    }

    public void setPreviousB(jUserKnockoutMatch previousB) {
        this.previousB = previousB;
    }
}
