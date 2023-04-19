package de.secretj12.turnierplaner.db.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "sets")
public class Set {

    @EmbeddedId
    private SetKey key;

    @Column(name = "score_a")
    private int scoreA;
    @Column(name = "score_b")
    private int scoreB;

    public SetKey getKey() {
        return key;
    }

    public void setKey(SetKey key) {
        this.key = key;
    }

    public int getScoreA() {
        return scoreA;
    }

    public void setScoreA(int scoreA) {
        this.scoreA = scoreA;
    }

    public int getScoreB() {
        return scoreB;
    }

    public void setScoreB(int scoreB) {
        this.scoreB = scoreB;
    }

    @Embeddable
    public static class SetKey implements Serializable {
        @ManyToOne(cascade = {CascadeType.ALL})
        @JoinColumn(name = "match_id")
        private Match match;
        @Column(name = "id")
        private long id;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SetKey setKey = (SetKey) o;
            return match.equals(setKey.match) && id == (setKey.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(match, id);
        }

        public Match getMatch() {
            return match;
        }

        public void setMatch(Match match) {
            this.match = match;
        }
    }
}
