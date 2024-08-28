package de.secretj12.turnierplaner.db.entities;

import de.secretj12.turnierplaner.db.entities.competition.Competition;
import de.secretj12.turnierplaner.db.entities.competition.Team;
import de.secretj12.turnierplaner.db.entities.groups.FinalOfGroup;
import de.secretj12.turnierplaner.db.entities.groups.MatchOfGroup;
import de.secretj12.turnierplaner.db.entities.knockout.NextMatch;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "matches")
@NamedQueries({
               @NamedQuery(name = "findHead",
                           query = """
                               FROM Match m WHERE m.competition = :comp
                               AND NOT EXISTS (FROM NextMatch n WHERE n.previousA = m OR n.previousB = m)
                               AND (EXISTS (FROM NextMatch n WHERE m = n.nextMatch AND n.winner = :finale)
                                  OR EXISTS (FROM FinalOfGroup f WHERE m = f.nextMatch AND
                                      (f.pos = 1 AND :finale = true
                                        OR f.pos = 2 AND :finale = false)))
                               AND NOT EXISTS (FROM MatchOfGroup mog WHERE mog.match = m)"""),
               @NamedQuery(name = "deleteByComp",
                           query = """
                               DELETE FROM Match m WHERE m.competition = :comp"""),
               @NamedQuery(name = "nonGroupMatches",
                           query = """
                               FROM Match m WHERE m.competition = :comp
                               AND NOT EXISTS (FROM MatchOfGroup mog WHERE mog.match = m)
                               """)})
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumns(@JoinColumn(name = "competition_id"))
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

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToOne(mappedBy = "nextMatch", fetch = FetchType.LAZY)
    private NextMatch dependentOn;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "previousA", fetch = FetchType.LAZY)
    private Collection<NextMatch> previousOfA;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "previousB", fetch = FetchType.LAZY)
    private Collection<NextMatch> previousOfB;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToOne(mappedBy = "nextMatch", fetch = FetchType.LAZY)
    private FinalOfGroup finalOfGroup;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToOne(mappedBy = "match", fetch = FetchType.LAZY)
    private MatchOfGroup group;

    @OneToMany(mappedBy = "key.match", cascade = CascadeType.ALL, orphanRemoval = true)
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
}
