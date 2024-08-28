package de.secretj12.turnierplaner.resources.jsonEntities.director;

import de.secretj12.turnierplaner.db.entities.Tournament;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserCourt;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class jDirectorTournamentUpdate extends jDirectorTournamentAdd {
    private UUID id;
    private Set<jUserCourt> courts;

    public jDirectorTournamentUpdate() {
    }

    public jDirectorTournamentUpdate(Tournament dbTournament) {
        super(dbTournament);
        this.id = dbTournament.getId();
        this.courts = dbTournament.getCourts().stream().map(jUserCourt::new).collect(Collectors.toSet());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Set<jUserCourt> getCourts() {
        return courts;
    }

    public void setCourts(Set<jUserCourt> courts) {
        this.courts = courts;
    }
}
