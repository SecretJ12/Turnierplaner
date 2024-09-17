package de.secretj12.turnierplaner.db.entities;

import de.secretj12.turnierplaner.db.entities.competition.Competition;
import de.secretj12.turnierplaner.db.entities.competition.Team;
import de.secretj12.turnierplaner.db.entities.groups.FinalOfGroup;
import de.secretj12.turnierplaner.db.entities.groups.MatchOfGroup;
import de.secretj12.turnierplaner.db.entities.knockout.NextMatch;
import io.smallrye.common.constraint.NotNull;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "matches")
@NamedQueries({
               @NamedQuery(name = "deleteByComp",
                           query = """
                               DELETE FROM Match m WHERE m.competition = :comp"""),
               @NamedQuery(name = "nonGroupMatches",
                           query = """
                               FROM Match m WHERE m.competition = :comp
                               AND NOT EXISTS (FROM MatchOfGroup mog WHERE mog.match = m)
                               """),
               @NamedQuery(name = "filterMatches",
                           query = """
                               FROM Match m WHERE (:tour IS NULL OR m.competition.tournament = :tour)
                               AND (:comp IS NULL OR  m.competition = :comp)
                               AND (:player IS NULL OR m.teamA.playerA = :player OR m.teamA.playerB = :player
                                           OR m.teamB.playerA = :player OR m.teamB.playerB = :player)
                               AND :from <= m.begin
                               AND m.end <= :to
                               """)})
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "competition_id")
    private Competition competition;
    @ManyToOne
    @JoinColumn(name = "court")
    private Court court;

    @Column(name = "begin_time")
    private Instant begin;
    @Column(name = "end_time")
    private Instant end;

    @Column(name = "finished")
    private Boolean finished;
    @Column(name = "winner")
    private Boolean winner;

    @ManyToOne
    @JoinColumn(name = "team_a")
    private Team teamA;
    @ManyToOne
    @JoinColumn(name = "team_b")
    private Team teamB;

    @Column(name = "number")
    private int number;

    @OneToOne(mappedBy = "nextMatch", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private NextMatch dependentOn;

    @OneToMany(mappedBy = "previousA", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private Collection<NextMatch> previousOfA;

    @OneToMany(mappedBy = "previousB", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private Collection<NextMatch> previousOfB;

    @OneToOne(mappedBy = "nextMatch", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private FinalOfGroup finalOfGroup;

    @OneToOne(mappedBy = "match", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private MatchOfGroup group;

    @OneToMany(mappedBy = "key.match", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Set> sets;

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

    public Boolean getFinished() {
        return finished;
    }

    public Team getTeamA() {
        return teamA;
    }

    public void setTeamA(Team teamA) {
        this.teamA = teamA;
    }

    public Team getTeamB() {
        return teamB;
    }

    public void setTeamB(Team teamB) {
        this.teamB = teamB;
    }

    public NextMatch getDependentOn() {
        return dependentOn;
    }

    public void setDependentOn(NextMatch dependentOn) {
        this.dependentOn = dependentOn;
    }

    public Collection<NextMatch> getPreviousOfA() {
        return previousOfA;
    }

    public void setPreviousOfA(Collection<NextMatch> previousOfA) {
        this.previousOfA = previousOfA;
    }

    public Collection<NextMatch> getPreviousOfB() {
        return previousOfB;
    }

    public void setPreviousOfB(Collection<NextMatch> previousOfB) {
        this.previousOfB = previousOfB;
    }

    public FinalOfGroup getFinalOfGroup() {
        return finalOfGroup;
    }

    public void setFinalOfGroup(FinalOfGroup finalOfGroup) {
        this.finalOfGroup = finalOfGroup;
    }

    public MatchOfGroup getGroup() {
        return group;
    }

    public void setGroup(MatchOfGroup group) {
        this.group = group;
    }

    public List<Set> getSets() {
        return sets;
    }

    public void setSets(List<Set> sets) {
        this.sets = sets;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
