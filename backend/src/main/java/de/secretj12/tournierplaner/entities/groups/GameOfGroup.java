package de.secretj12.tournierplaner.entities.groups;

import de.secretj12.tournierplaner.entities.Match;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "game_of_group")
public class GameOfGroup {

    @EmbeddedId
    private FinalOfGroupKey finalofGroupKey;

    @Column(name = "position")
    private Integer position;

    @Embeddable
    public static class FinalOfGroupKey implements Serializable {
        @OneToOne
        @JoinColumn(name = "match_id", nullable = false)
        private Match match;
        @OneToOne
        @JoinColumns({
                @JoinColumn(name = "group_id", nullable = false)
        })
        private Group group;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FinalOfGroupKey that = (FinalOfGroupKey) o;
            return match.equals(that.match) && group.equals(that.group);
        }

        @Override
        public int hashCode() {
            return Objects.hash(match, group);
        }

        public Match getMatch() {
            return match;
        }

        public void setMatch(Match match) {
            this.match = match;
        }

        public Group getGroup() {
            return group;
        }

        public void setGroup(Group group) {
            this.group = group;
        }
    }

    public FinalOfGroupKey getFinalofGroupKey() {
        return finalofGroupKey;
    }

    public void setFinalofGroupKey(FinalOfGroupKey finalofGroupKey) {
        this.finalofGroupKey = finalofGroupKey;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
