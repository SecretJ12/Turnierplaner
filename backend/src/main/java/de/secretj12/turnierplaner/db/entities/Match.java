package de.secretj12.turnierplaner.db.entities;

import de.secretj12.turnierplaner.db.entities.knockout.NextMatch;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "matches")
@NamedQueries(
        @NamedQuery(name = "findHead",
                query = "FROM Match m WHERE m.competition.id = :compId " +
                        "AND NOT EXISTS (FROM NextMatch n WHERE n.previousA = m OR n.previousB = m) " +
                        "AND EXISTS (FROM NextMatch n WHERE m.id = n.nextMatch AND n.winner = :finale)")
)
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumns({
            @JoinColumn(name = "competition_id")
    })
    private Competition competition;
    @ManyToOne
    @JoinColumn(name = "court")
    private Court court;

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

    @OneToOne(mappedBy = "nextMatch")
    private NextMatch dependentOn;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competitionId) {
        this.competition = competitionId;
    }

    public Court getCourt() {
        return court;
    }

    public void setCourt(Court courts) {
        this.court = courts;
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

    public Boolean isFinished() {
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

    public NextMatch getDependentOn() {
        return dependentOn;
    }

    public void setDependentOn(NextMatch dependentOn) {
        this.dependentOn = dependentOn;
    }
}
