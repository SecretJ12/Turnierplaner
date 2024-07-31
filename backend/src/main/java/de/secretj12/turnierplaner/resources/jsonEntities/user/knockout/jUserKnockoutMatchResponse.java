package de.secretj12.turnierplaner.resources.jsonEntities.user.knockout;

import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserSet;

import java.time.LocalDateTime;
import java.util.List;

public class jUserKnockoutMatchResponse {
    private String court;
    private LocalDateTime begin;
    private LocalDateTime end;

    private Boolean finished;
    private Boolean winner;

    private String teamA_id;
    private String teamB_id;

    private boolean winningPlayer;
    private jUserKnockoutMatchResponse previousA;
    private jUserKnockoutMatchResponse previousB;

    private List<jUserSet> sets;

    public String getCourt() {
        return court;
    }

    public Boolean getFinished() {
        return finished;
    }

    public Boolean getWinner() {
        return winner;
    }

    public boolean isWinningPlayer() {
        return winningPlayer;
    }

    public jUserKnockoutMatchResponse getPreviousA() {
        return previousA;
    }

    public jUserKnockoutMatchResponse getPreviousB() {
        return previousB;
    }

    public List<jUserSet> getSets() {
        return sets;
    }

    public LocalDateTime getBegin() {
        return begin;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public String getTeamA_id() {
        return teamA_id;
    }

    public String getTeamB_id() {
        return teamB_id;
    }
}
