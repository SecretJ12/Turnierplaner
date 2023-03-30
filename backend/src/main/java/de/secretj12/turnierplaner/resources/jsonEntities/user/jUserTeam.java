package de.secretj12.turnierplaner.resources.jsonEntities.user;

import de.secretj12.turnierplaner.db.entities.competition.Team;

public class jUserTeam {

    private jUserPlayer playerA;
    private jUserPlayer playerB;

    public jUserTeam(Team team) {
        if (team.getPlayerA() != null)
            playerA = new jUserPlayer(team.getPlayerA());
        if (team.getPlayerB() != null)
            playerB = new jUserPlayer(team.getPlayerB());
    }

    public jUserPlayer getPlayerA() {
        return playerA;
    }

    public void setPlayerA(jUserPlayer playerA) {
        this.playerA = playerA;
    }

    public jUserPlayer getPlayerB() {
        return playerB;
    }

    public void setPlayerB(jUserPlayer playerB) {
        this.playerB = playerB;
    }
}
