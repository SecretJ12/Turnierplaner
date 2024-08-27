package de.secretj12.turnierplaner.resources.jsonEntities.user;

import de.secretj12.turnierplaner.db.entities.Tournament;

import java.time.Instant;

public class jUserTournament {
    private String name;
    private String description;
    private Instant beginRegistration;
    private Instant endRegistration;
    private Instant beginGamePhase;
    private Instant endGamePhase;

    public jUserTournament() {
    }

    public jUserTournament(Tournament dbTournament) {
        this.name = dbTournament.getName();
        this.description = dbTournament.getDescription();
        this.beginRegistration = dbTournament.getBeginRegistration();
        this.endRegistration = dbTournament.getEndRegistration();
        this.beginGamePhase = dbTournament.getBeginGamePhase();
        this.endGamePhase = dbTournament.getEndGamePhase();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getBeginRegistration() {
        return beginRegistration;
    }

    public void setBeginRegistration(Instant beginRegistration) {
        this.beginRegistration = beginRegistration;
    }

    public Instant getEndRegistration() {
        return endRegistration;
    }

    public void setEndRegistration(Instant endRegistration) {
        this.endRegistration = endRegistration;
    }

    public Instant getBeginGamePhase() {
        return beginGamePhase;
    }

    public void setBeginGamePhase(Instant beginGamePhase) {
        this.beginGamePhase = beginGamePhase;
    }

    public Instant getEndGamePhase() {
        return endGamePhase;
    }

    public void setEndGamePhase(Instant endGamePhase) {
        this.endGamePhase = endGamePhase;
    }
}
