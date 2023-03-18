package de.secretj12.turnierplaner.db.entities;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

// TODO add attributes to allow setting age and sex restrictions (but optional!) -> implement everywhere
// TODO add attributes to set something like a plan published bit -> implement everywhere
@Entity
@Table(name = "competitions")
@NamedQueries({
        @NamedQuery(name="compByName",
                query="SELECT c FROM Competition c LEFT JOIN Tournament t ON c.tournament = t " +
                        "WHERE t.name = ?1 AND c.name = ?2"),
        @NamedQuery(name="listByName",
                query="SELECT c FROM Competition c LEFT JOIN Tournament t ON c.tournament = t WHERE t.name=  ?1")
})
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name")
    private String name;

    @ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @Column(name = "description")
    private String description;
    @Column(name = "type")
    private CompetitionType type;

    @ManyToMany
    @JoinTable(
            name = "participating_in",
            joinColumns = {
                    @JoinColumn(name = "competition_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "player_id")
            }
    )
    private List<Player> players;

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CompetitionType getType() {
        return type;
    }

    public void setType(CompetitionType type) {
        this.type = type;
    }
}
