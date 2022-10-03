package de.secretj12.tournierplaner.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="timeslot")
public class Timeslot {
    @Id
    @GeneratedValue
    @Column(name="id")
    private Long id;

    @Column(name="begin_time")
    private LocalDateTime begin;
    @Column(name="end_time")
    private LocalDateTime end;
    @ManyToOne @MapsId
    @JoinColumn(name="competition")
    private Competition competition;

}
