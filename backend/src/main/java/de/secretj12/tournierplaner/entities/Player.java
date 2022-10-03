package de.secretj12.tournierplaner.entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="players")
public class Player {
    @Id
    @GeneratedValue
    @Column(name="id")
    private long id;

    @Column(name="first_name")
    private String firstName;
    @Column(name="last_name")
    private String lastName;

    @Column(name="birthday")
    private LocalDateTime birthday;

    @ManyToMany @MapsId
    @JoinTable(
            name="participating_in",
            joinColumns = { @JoinColumn(name="competition_id") },
            inverseJoinColumns = { @JoinColumn(name="player_id") }
    )
    private List<Competition> competitions;
}
