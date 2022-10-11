package de.secretj12.tournierplaner.entities;

import javax.persistence.*;

@Entity
@Table(name = "courts")
public class Court {
    @Id
    @Column(name = "name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
