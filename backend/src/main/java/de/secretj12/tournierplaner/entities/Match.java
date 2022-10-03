package de.secretj12.tournierplaner.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="matches")
public class Match {
    @Id @GeneratedValue
    @Column(name="id")
    private Long id;

    @ManyToOne @MapsId
    @JoinColumn(name="competition_id")
    private Competition competitionId;

    @Column(name="court")
    private String court;
    @Column(name="begin_time")
    private LocalDateTime begin;
    @Column(name="end_time")
    private LocalDateTime end;

    @Column(name="finished")
    private Boolean finished;
    @Column(name="winner")
    private Boolean winner;
}
