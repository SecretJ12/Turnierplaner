package de.secretj12.turnierplaner.resources.jsonEntities.director;

import de.secretj12.turnierplaner.db.entities.Competition;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserCompetition;

public class jDirectorCompetitionAdd extends jUserCompetition {

    public jDirectorCompetitionAdd() {}

    public jDirectorCompetitionAdd(Competition competition) {
        super(competition);
    }

    public Competition toDB() {
        Competition competition = new Competition();
        competition.setName(getName());
        competition.setDescription(getDescription());
        competition.setType(getType());
        return competition;
    }
}
