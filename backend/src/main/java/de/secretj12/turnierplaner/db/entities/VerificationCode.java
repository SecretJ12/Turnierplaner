package de.secretj12.turnierplaner.db.entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "verificationcodes")
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @Column(name = "expiration_date")
    private LocalDateTime expiration_date;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public LocalDateTime getExpirationDate() {
        return expiration_date;
    }

    public void setExpirationDate(LocalDateTime expiration_date) {
        this.expiration_date = expiration_date;
    }
}
