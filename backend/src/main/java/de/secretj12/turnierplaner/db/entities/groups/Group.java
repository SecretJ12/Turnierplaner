package de.secretj12.turnierplaner.db.entities.groups;

import de.secretj12.turnierplaner.db.entities.competition.Competition;
import de.secretj12.turnierplaner.db.entities.Match;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;
    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER)
    private List<MatchOfGroup> matchesOfGroup;
    @Column(name = "index")
    private long index;
    @ManyToOne
    @JoinColumns(@JoinColumn(name = "competition_id"))
    private Competition competition;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public List<MatchOfGroup> getMatchesOfGroup() {
        return matchesOfGroup;
    }

    public void setMatchesOfGroup(List<MatchOfGroup> matchesOfGroup) {
        this.matchesOfGroup = matchesOfGroup;
    }

    public List<Match> getMatches() {
        return matchesOfGroup.stream().map(MatchOfGroup::getMatch).toList();
    }
}
