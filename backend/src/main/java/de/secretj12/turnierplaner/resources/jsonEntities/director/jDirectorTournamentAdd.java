package de.secretj12.turnierplaner.resources.jsonEntities.director;

import de.secretj12.turnierplaner.db.entities.Tournament;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserTournament;

public class jDirectorTournamentAdd extends jUserTournament {
    private boolean visible;

    public jDirectorTournamentAdd() {
    }

    public jDirectorTournamentAdd(Tournament dbTournament) {
        super(dbTournament);
        this.visible = dbTournament.isVisible();
    }

    public Tournament toDB() {
        Tournament tournament = new Tournament();
        tournament.setName(getName());
        tournament.setDescription(getDescription());
        tournament.setVisible(isVisible());
        tournament.setBeginRegistration(getBeginRegistration());
        tournament.setEndRegistration(getEndRegistration());
        tournament.setBeginGamePhase(getBeginGamePhase());
        tournament.setEndGamePhase(getEndGamePhase());
        return tournament;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
