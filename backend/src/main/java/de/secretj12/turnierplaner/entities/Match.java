package de.secretj12.turnierplaner.entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "matches")
public class Match {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumns({
            @JoinColumn(name = "competition_id")
    })
    private Competition competitionId;
    @ManyToOne
    @JoinColumn(name = "court")
    private Court courts;

    @Column(name = "begin_time")
    private LocalDateTime begin;
    @Column(name = "end_time")
    private LocalDateTime end;

    @Column(name = "finished")
    private Boolean finished;
    @Column(name = "winner")
    private Boolean winner;


    @ManyToOne
    @JoinColumn(name = "player_a")
    private Player playerA;
    @ManyToOne
    @JoinColumn(name = "player_b")
    private Player playerB;

    @ManyToMany
    @JoinTable(
            name = "dependent_on",
            joinColumns = {@JoinColumn(name = "dependant")},
            inverseJoinColumns = {@JoinColumn(name = "dependency")}
    )
    private List<Match> dependentOn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Competition getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Competition competitionId) {
        this.competitionId = competitionId;
    }

    public Court getCourts() {
        return courts;
    }

    public void setCourts(Court courts) {
        this.courts = courts;
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

    public Player getPlayerA() {
        return playerA;
    }

    public void setPlayerA(Player playerA) {
        this.playerA = playerA;
    }

    public Player getPlayerB() {
        return playerB;
    }

    public void setPlayerB(Player playerB) {
        this.playerB = playerB;
    }

    public List<Match> getDependentOn() {
        return dependentOn;
    }

    public void setDependentOn(List<Match> dependentOn) {
        this.dependentOn = dependentOn;
    }
}
