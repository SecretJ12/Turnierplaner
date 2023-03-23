package de.secretj12.turnierplaner.resources.jsonEntities.user;

import de.secretj12.turnierplaner.db.entities.Competition;
import de.secretj12.turnierplaner.db.entities.CompetitionType;

public class jUserCompetition {
    private String name;
    private String description;
    private CompetitionType type;

    public jUserCompetition() {
    }

    public jUserCompetition(Competition competition) {
        this.name = competition.getName();
        this.description = competition.getDescription();
        this.type = competition.getType();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CompetitionType getType() {
        return type;
    }

    public void setType(CompetitionType type) {
        this.type = type;
    }
}
