package de.secretj12.turnierplaner.db.entities.groups;

import de.secretj12.turnierplaner.db.entities.competition.Competition;
import de.secretj12.turnierplaner.db.entities.Match;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER)
    private Set<MatchOfGroup> matchesOfGroup;
    @Column(name = "index")
    private long index;
    @ManyToOne
    @JoinColumns(@JoinColumn(name = "competition_id"))
    private Competition competition;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "groupA")
    private Set<FinalOfGroup> finalOfGroupA;
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "groupB")
    private Set<FinalOfGroup> finalOfGroupB;

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

    public Set<MatchOfGroup> getMatchesOfGroup() {
        return matchesOfGroup;
    }

    public void setMatchesOfGroup(Set<MatchOfGroup> matchesOfGroup) {
        this.matchesOfGroup = matchesOfGroup;
    }

    public Set<Match> getMatches() {
        if (matchesOfGroup == null)
            return Set.of();

        return matchesOfGroup.stream().map(MatchOfGroup::getMatch).collect(Collectors.toCollection(HashSet::new));
    }
}
