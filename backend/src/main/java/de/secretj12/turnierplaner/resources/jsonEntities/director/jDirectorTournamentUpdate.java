package de.secretj12.turnierplaner.resources.jsonEntities.director;

import de.secretj12.turnierplaner.db.entities.Tournament;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserTournament;

import java.util.UUID;

public class jDirectorTournamentUpdate extends jDirectorTournamentAdd {
    private UUID id;

    public jDirectorTournamentUpdate() {}

    public jDirectorTournamentUpdate(Tournament dbTournament) {
        super(dbTournament);
        this.id = dbTournament.getId();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
