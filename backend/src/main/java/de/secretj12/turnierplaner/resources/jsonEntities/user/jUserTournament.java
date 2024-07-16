package de.secretj12.turnierplaner.resources.jsonEntities.user;

import de.secretj12.turnierplaner.db.entities.Tournament;

import java.time.LocalDateTime;

public class jUserTournament {
    private String name;
    private String description;
    private LocalDateTime beginRegistration;
    private LocalDateTime endRegistration;
    private LocalDateTime beginGamePhase;
    private LocalDateTime endGamePhase;

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

    public LocalDateTime getBeginRegistration() {
        return beginRegistration;
    }

    public void setBeginRegistration(LocalDateTime beginRegistration) {
        this.beginRegistration = beginRegistration;
    }

    public LocalDateTime getEndRegistration() {
        return endRegistration;
    }

    public void setEndRegistration(LocalDateTime endRegistration) {
        this.endRegistration = endRegistration;
    }

    public LocalDateTime getBeginGamePhase() {
        return beginGamePhase;
    }

    public void setBeginGamePhase(LocalDateTime beginGamePhase) {
        this.beginGamePhase = beginGamePhase;
    }

    public LocalDateTime getEndGamePhase() {
        return endGamePhase;
    }

    public void setEndGamePhase(LocalDateTime endGamePhase) {
        this.endGamePhase = endGamePhase;
    }
}
