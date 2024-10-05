package de.secretj12.turnierplaner.db.entities;

import de.secretj12.turnierplaner.enums.CourtType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "courts")
public class Court {
    @Id
    @Column(name = "name")
    private String name;

    @Column(name = "courtType")
    private CourtType courtType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CourtType getCourtType() {
        return courtType;
    }

    public void setCourtType(CourtType courtType) {
        this.courtType = courtType;
    }
}
