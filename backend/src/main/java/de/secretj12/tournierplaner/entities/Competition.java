package de.secretj12.tournierplaner.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "competitions")
public class Competition {
    @EmbeddedId
    private CompetitionKey key;

    @Column(name = "description")
    private String description;
    @Column(name = "turnierform")
    private String turnierform;

    @Embeddable
    public static class CompetitionKey implements Serializable {
        @ManyToOne(cascade = { CascadeType.ALL })
        @JoinColumn(name = "tournament")
        private Tournament tournament;
        @Column(name = "name")
        private String name;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CompetitionKey that = (CompetitionKey) o;
            return tournament.equals(that.tournament) && name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tournament, name);
        }

        public Tournament getTournament() {
            return tournament;
        }

        public void setTournament(Tournament tournament) {
            this.tournament = tournament;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public CompetitionKey getKey() {
        return key;
    }

    public void setKey(CompetitionKey key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTurnierform() {
        return turnierform;
    }

    public void setTurnierform(String turnierform) {
        this.turnierform = turnierform;
    }
}
