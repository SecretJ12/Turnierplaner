package de.secretj12.turnierplaner.resources.jsonEntities.user;

import de.secretj12.turnierplaner.db.entities.Match;

import java.time.LocalDateTime;
import java.util.UUID;

public class jUserGroupMatch {

    private String court;
    private LocalDateTime begin;
    private LocalDateTime end;

    private Boolean finished;
    private Boolean winner;

    private UUID playerA;
    private UUID playerB;

    public jUserGroupMatch(Match match) {
        this.court = match.getCourt().getName();
        this.begin = match.getBegin();
        this.end = match.getEnd();

        this.finished = match.isFinished();
        this.winner = match.getWinner();

        if (match.getPlayerA() != null)
            this.playerA = match.getPlayerA().getId();
        if (match.getPlayerB() != null)
            this.playerB = match.getPlayerB().getId();
    }

    public String getCourt() {
        return court;
    }

    public void setCourt(String court) {
        this.court = court;
    }

    public LocalDateTime getBegin() {
        return begin;
    }

    public void setBegin(LocalDateTime begin) {
        this.begin = begin;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public Boolean getFinished() {
        return finished;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }

    public Boolean getWinner() {
        return winner;
    }

    public void setWinner(Boolean winner) {
        this.winner = winner;
    }

    public UUID getPlayerA() {
        return playerA;
    }

    public void setPlayerA(UUID playerA) {
        this.playerA = playerA;
    }

    public UUID getPlayerB() {
        return playerB;
    }

    public void setPlayerB(UUID playerB) {
        this.playerB = playerB;
    }
}