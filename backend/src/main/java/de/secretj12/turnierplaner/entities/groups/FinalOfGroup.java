package de.secretj12.turnierplaner.entities.groups;

import de.secretj12.turnierplaner.entities.Match;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "final_of_group")
public class FinalOfGroup {

    @EmbeddedId
    private FinalOfGroupKey finalofGroupKey;

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

    @Column(name = "position")
    private int end;

    public FinalOfGroupKey getFinalofGroupKey() {
        return finalofGroupKey;
    }

    public void setFinalofGroupKey(FinalOfGroupKey finalofGroupKey) {
        this.finalofGroupKey = finalofGroupKey;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
