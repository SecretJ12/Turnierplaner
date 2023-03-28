package de.secretj12.turnierplaner.db.entities.groups;

import de.secretj12.turnierplaner.db.entities.competition.Competition;
import de.secretj12.turnierplaner.db.entities.Match;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "game_in_group")
    private List<Match> matches;
    @Column(name = "index")
    private long index;
    @ManyToOne
    @JoinColumns({@JoinColumn(name = "competition_id")})
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

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }
}
