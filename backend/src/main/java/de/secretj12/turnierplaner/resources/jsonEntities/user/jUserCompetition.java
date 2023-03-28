package de.secretj12.turnierplaner.resources.jsonEntities.user;

import de.secretj12.turnierplaner.db.entities.competition.Competition;

public class jUserCompetition {
    private String name;
    private String description;
    private jUserCompetitionType type;

    public jUserCompetition() {
    }

    public jUserCompetition(Competition competition) {
        this.name = competition.getName();
        this.description = competition.getDescription();
        switch (competition.getType()) {
            case GROUPS -> this.type = jUserCompetitionType.GROUPS;
            case KNOCKOUT -> this.type = jUserCompetitionType.KNOCKOUT;
        }
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

    public jUserCompetitionType getType() {
        return type;
    }

    public void setType(jUserCompetitionType type) {
        this.type = type;
    }
}
