package de.secretj12.tournierplaner.entities.groups;

import de.secretj12.tournierplaner.entities.Competition;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "groups")
public class Group {

    @EmbeddedId
    private GroupKey key;

    @Embeddable
    public static class GroupKey implements Serializable {
        @ManyToOne
        @JoinColumns({@JoinColumn(name = "tournament_name"), @JoinColumn(name = "competition_name")})
        private Competition competition;
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(name = "id", nullable = false)
        private UUID id;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GroupKey groupKey = (GroupKey) o;
            return competition.equals(groupKey.competition) && id.equals(groupKey.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(competition, id);
        }

        public Competition getCompetition() {
            return competition;
        }

        public void setCompetition(Competition competition) {
            this.competition = competition;
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }
    }

    public GroupKey getKey() {
        return key;
    }

    public void setKey(GroupKey key) {
        this.key = key;
    }
}
