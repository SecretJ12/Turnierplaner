package de.secretj12.turnierplaner.resources.jsonEntities.user;

import de.secretj12.turnierplaner.db.entities.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class jUserMatch {

    private static final Logger log = LoggerFactory.getLogger(jUserMatch.class);
    private UUID id;
    private String court;
    private Instant begin;
    private Instant end;

    private Boolean finished;
    private Boolean winner;

    private UUID teamA;
    private UUID teamB;

    private List<jUserSet> sets;

    public jUserMatch() {
    }

    public jUserMatch(Match match) {
        this.id = match.getId();
        this.court = match.getCourt() != null ? match.getCourt().getName() : null;
        this.begin = match.getBegin();
        this.end = match.getEnd();

        this.finished = match.isFinished();
        this.winner = match.getWinner();

        if (match.getTeamA() != null)
            this.teamA = match.getTeamA().getId();
        if (match.getTeamB() != null)
            this.teamB = match.getTeamB().getId();

        this.sets = match.getSets().stream().map(jUserSet::new).toList();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCourt() {
        return court;
    }

    public void setCourt(String court) {
        this.court = court;
    }

    public Instant getBegin() {
        return begin;
    }

    public void setBegin(Instant begin) {
        this.begin = begin;
    }

    public Instant getEnd() {
        return end;
    }

    public void setEnd(Instant end) {
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

    public List<jUserSet> getSets() {
        return sets;
    }

    public void setSets(List<jUserSet> sets) {
        this.sets = sets;
    }
}
