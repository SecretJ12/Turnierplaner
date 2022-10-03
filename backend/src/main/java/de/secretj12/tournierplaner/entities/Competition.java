package de.secretj12.tournierplaner.entities;

import javax.persistence.*;

@Entity
@Table(name="competitions")
public class Competition {
    @Id @GeneratedValue
    @Column(name="id")
    private long id;

    @ManyToOne @MapsId
    @JoinColumn(name="tournament_id")
    private Tournament tournament_id;

    @Column(name="name")
    private String name;
    @Column(name="description")
    private String description;
    @Column(name="turnierform")
    private String turnierform;

}
