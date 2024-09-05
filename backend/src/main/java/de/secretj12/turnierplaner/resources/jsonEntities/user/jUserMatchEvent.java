package de.secretj12.turnierplaner.resources.jsonEntities.user;

import de.secretj12.turnierplaner.db.entities.Match;
import de.secretj12.turnierplaner.resources.jsonEntities.user.competition.jUserCompetitionType;

public class jUserMatchEvent extends jUserMatch {

    /**
     * Name of the competition
     */
    public String compName;
    /**
     * Type of the match in competition
     * -> also group competitions contain knockout matches
     */
    public jUserCompetitionType type;
    /**
     * For knockout: round number
     * For group: group number
     */
    public long number;
    /**
     * Only set for knockout
     * Total number of rounds
     */
    public int total;

    /**
     * Only set for knockout
     * Set if the match is the final
     */
    public boolean isFinal;

    public jUserMatchEvent(Match match) {
        super(match);
    }

    public jUserMatchEvent() {
    }

    public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    public jUserCompetitionType getType() {
        return type;
    }

    public void setType(jUserCompetitionType type) {
        this.type = type;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }
}
