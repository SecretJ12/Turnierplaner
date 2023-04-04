package de.secretj12.turnierplaner.resources.jsonEntities.user;

import de.secretj12.turnierplaner.db.entities.Match;

import java.util.List;

public class jUserKnockoutSystem {

    private List<jUserPlayer> player;
    private jUserKnockoutMatch finale;
    private jUserKnockoutMatch thirdPlace;

    public jUserKnockoutSystem(Match finale, Match thirdPlace) {
        this.finale = new jUserKnockoutMatch(finale);
        this.thirdPlace = new jUserKnockoutMatch(thirdPlace, false);
    }

    public List<jUserPlayer> getPlayer() {
        return player;
    }

    public void setPlayer(List<jUserPlayer> player) {
        this.player = player;
    }

    public jUserKnockoutMatch getFinale() {
        return finale;
    }

    public void setFinale(jUserKnockoutMatch finale) {
        this.finale = finale;
    }

    public jUserKnockoutMatch getThirdPlace() {
        return thirdPlace;
    }

    public void setThirdPlace(jUserKnockoutMatch thirdPlace) {
        this.thirdPlace = thirdPlace;
    }
}
