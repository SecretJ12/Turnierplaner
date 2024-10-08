package de.secretj12.turnierplaner.db.entities.groups;

import de.secretj12.turnierplaner.db.entities.Match;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "match_of_group")
public class MatchOfGroup {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", referencedColumnName = "id", nullable = false)
    private Group group;

    @MapsId
    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "match", referencedColumnName = "id", nullable = false)
    private Match match;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }
}
