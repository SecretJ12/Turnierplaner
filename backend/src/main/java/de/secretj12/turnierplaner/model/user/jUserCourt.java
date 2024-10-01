package de.secretj12.turnierplaner.model.user;

import de.secretj12.turnierplaner.db.entities.Court;
import de.secretj12.turnierplaner.enums.CourtType;

public class jUserCourt {

    private String name;
    private CourtType courtType;

    public jUserCourt() {
    }

    public jUserCourt(Court court) {
        this.name = court.getName();
        this.courtType = court.getCourtType();
    }

    public Court toDB() {
        Court court = new Court();
        court.setName(name);
        court.setCourtType(courtType);
        return court;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CourtType getCourtType() {
        return courtType;
    }

    public void setCourtType(CourtType courtType) {
        this.courtType = courtType;
    }
}
