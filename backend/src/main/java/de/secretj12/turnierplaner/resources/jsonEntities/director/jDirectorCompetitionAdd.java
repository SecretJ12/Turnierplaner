package de.secretj12.turnierplaner.resources.jsonEntities.director;

import de.secretj12.turnierplaner.db.entities.Competition;
import de.secretj12.turnierplaner.db.entities.Tournament;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserCompetition;

import java.util.UUID;

public class jDirectorCompetitionAdd extends jUserCompetition {

    private String tourName;

    public jDirectorCompetitionAdd() {}

    public jDirectorCompetitionAdd(Competition competition) {
        super(competition);
        this.tourName = competition.getTournament().getName();
    }

    public Competition toDB() {
        Competition competition = new Competition();
        competition.setName(getName());
        competition.setDescription(getDescription());
        competition.setType(getType());
        return competition;
    }

    public String getTourName() {
        return tourName;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }
}
