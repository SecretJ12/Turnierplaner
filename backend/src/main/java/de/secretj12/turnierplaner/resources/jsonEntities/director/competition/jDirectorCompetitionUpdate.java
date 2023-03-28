package de.secretj12.turnierplaner.resources.jsonEntities.director.competition;

import de.secretj12.turnierplaner.db.entities.competition.Competition;

import java.util.UUID;

public class jDirectorCompetitionUpdate extends jDirectorCompetition {

    private UUID id;

    public jDirectorCompetitionUpdate() {
    }

    public jDirectorCompetitionUpdate(Competition competition) {
        super(competition);
        this.id = competition.getId();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
