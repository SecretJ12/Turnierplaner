package de.secretj12.turnierplaner.db.entities;

import de.secretj12.turnierplaner.db.entities.competition.Competition;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "timeslots")
public class Timeslot {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "begin_time")
    private LocalDateTime begin;
    @Column(name = "end_time")
    private LocalDateTime end;
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "competition_id")
    })
    private Competition competition;

    @ManyToOne
    @JoinColumn(name = "court_name")
    private Court courts;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public Court getCourts() {
        return courts;
    }

    public void setCourts(Court courts) {
        this.courts = courts;
    }
}
