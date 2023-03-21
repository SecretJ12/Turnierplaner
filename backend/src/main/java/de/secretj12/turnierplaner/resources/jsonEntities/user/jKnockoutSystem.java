package de.secretj12.turnierplaner.resources.jsonEntities.user;

import de.secretj12.turnierplaner.db.entities.Match;

public class jKnockoutSystem {

    private jUserKnockoutMatch finale;
    private jUserKnockoutMatch thirdPlace;

    public jKnockoutSystem(Match finale, Match thirdPlace) {
        this.finale = new jUserKnockoutMatch(finale);
        this.thirdPlace = new jUserKnockoutMatch(thirdPlace, false);
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
