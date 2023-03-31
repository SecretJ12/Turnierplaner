package de.secretj12.turnierplaner.db.entities.competition;

import de.secretj12.turnierplaner.db.entities.Player;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "competition", referencedColumnName = "id", nullable = false)
    private Competition competition;

    @ManyToOne
    @JoinColumn(name = "player_a", referencedColumnName = "id")
    private Player playerA;
    @ManyToOne
    @JoinColumn(name = "player_b", referencedColumnName = "id")
    private Player playerB;

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
}
