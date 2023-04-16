package de.secretj12.turnierplaner.resources.jsonEntities.user.knockout;

import de.secretj12.turnierplaner.db.entities.Match;

import java.time.LocalDateTime;
import java.util.UUID;

public class jUserKnockoutMatch {

    private String court;
    private LocalDateTime begin;
    private LocalDateTime end;

    private Boolean finished;
    private Boolean winner;

    private UUID teamA;
    private UUID teamB;

    private boolean winningPlayer;
    private jUserKnockoutMatch previousA;
    private jUserKnockoutMatch previousB;

    public jUserKnockoutMatch(Match match) {
        this(match, true);
    }

    public jUserKnockoutMatch(Match match, boolean dependant) {
        this.court = match.getCourt().getName();
        this.begin = match.getBegin();
        this.end = match.getEnd();

        this.finished = match.isFinished();
        this.winner = match.getWinner();

        if (match.getTeamA() != null)
            this.teamA = match.getTeamA().getId();
        if (match.getTeamB() != null)
            this.teamB = match.getTeamB().getId();

        if (dependant && match.getDependentOn() != null) {
            this.winningPlayer = match.getDependentOn().isWinner();
            this.previousA = new jUserKnockoutMatch(match.getDependentOn().getPreviousA());
            this.previousB = new jUserKnockoutMatch(match.getDependentOn().getPreviousB());
        }
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

    public UUID getTeamA() {
        return teamA;
    }

    public void setTeamA(UUID teamA) {
        this.teamA = teamA;
    }

    public UUID getTeamB() {
        return teamB;
    }

    public void setTeamB(UUID teamB) {
        this.teamB = teamB;
    }

    public boolean isWinningPlayer() {
        return winningPlayer;
    }

    public void setWinningPlayer(boolean winningPlayer) {
        this.winningPlayer = winningPlayer;
    }

    public jUserKnockoutMatch getPreviousA() {
        return previousA;
    }

    public void setPreviousA(jUserKnockoutMatch previousA) {
        this.previousA = previousA;
    }

    public jUserKnockoutMatch getPreviousB() {
        return previousB;
    }

    public void setPreviousB(jUserKnockoutMatch previousB) {
        this.previousB = previousB;
    }
}
