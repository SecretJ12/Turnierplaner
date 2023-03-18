package de.secretj12.turnierplaner.resources.jsonEntities.director;

import de.secretj12.turnierplaner.db.entities.Competition;

import java.util.UUID;

public class jDirectorCompetitionUpdate extends jDirectorCompetitionAdd {

    private UUID id;

    public jDirectorCompetitionUpdate() {}

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
