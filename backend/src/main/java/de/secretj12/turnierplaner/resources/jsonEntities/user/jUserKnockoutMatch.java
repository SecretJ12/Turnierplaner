package de.secretj12.turnierplaner.resources.jsonEntities.user;

import de.secretj12.turnierplaner.db.entities.Match;

import java.time.LocalDateTime;

public class jUserKnockoutMatch {

    private String court;
    private LocalDateTime begin;
    private LocalDateTime end;

    private Boolean finished;
    private Boolean winner;

    private jUserPlayer playerA;
    private jUserPlayer playerB;

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

        if (match.getPlayerA() != null)
            this.playerA = new jUserPlayer(match.getPlayerA());
        if (match.getPlayerB() != null)
            this.playerB = new jUserPlayer(match.getPlayerB());

        if (dependant && match.getDependentOn() != null) {
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
