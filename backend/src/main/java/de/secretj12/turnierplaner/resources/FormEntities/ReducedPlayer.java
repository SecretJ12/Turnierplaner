package de.secretj12.turnierplaner.resources.FormEntities;

import de.secretj12.turnierplaner.entities.Player;

import java.util.UUID;

public class ReducedPlayer {
    private UUID id;
    private String firstName;

    private String lastName;

    public ReducedPlayer(UUID id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public ReducedPlayer(Player p) {
        this.id = p.getId();
        this.firstName = p.getFirstName();
        this.lastName = p.getLastName();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLast_name(String lastName) {
        this.lastName = lastName;
    }
}