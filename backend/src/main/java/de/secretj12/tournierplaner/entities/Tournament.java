package de.secretj12.tournierplaner.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tournaments")
public class Tournament {
    @Id
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;

    @Column(name = "begin_registration")
    private LocalDateTime beginRegistration;
    @Column(name = "end_registration")
    private LocalDateTime endRegistration;

    @Column(name = "begin_game_phase")
    private LocalDateTime beginGamePhase;
    @Column(name = "end_game_phase")
    private LocalDateTime endGamePhase;

    @Column(name = "visible")
    private boolean visible;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getBeginRegistration() {
        return beginRegistration;
    }

    public void setBeginRegistration(LocalDateTime beginRegistration) {
        this.beginRegistration = beginRegistration;
    }

    public LocalDateTime getEndRegistration() {
        return endRegistration;
    }

    public void setEndRegistration(LocalDateTime endRegistration) {
        this.endRegistration = endRegistration;
    }

    public LocalDateTime getBeginGamePhase() {
        return beginGamePhase;
    }

    public void setBeginGamePhase(LocalDateTime beginGamePhase) {
        this.beginGamePhase = beginGamePhase;
    }

    public LocalDateTime getEndGamePhase() {
        return endGamePhase;
    }

    public void setEndGamePhase(LocalDateTime endGamePhase) {
        this.endGamePhase = endGamePhase;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
