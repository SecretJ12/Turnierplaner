package de.secretj12.turnierplaner.startup.testdata;

import de.secretj12.turnierplaner.db.entities.competition.CompetitionMode;
import de.secretj12.turnierplaner.db.entities.competition.CompetitionType;
import de.secretj12.turnierplaner.db.entities.competition.Sex;

class CompetitionSettings {
    private String name;
    private CompetitionType competitionType;
    private CompetitionMode competitionMode;
    private Sex sex;
    private int teamNumbers;
    private boolean registerIndividual;
    private AGE_RESTR ageRestr;
    private boolean differentConditions;
    private int numberOfGroups;

    public CompetitionSettings(String description, CompetitionType competitionType, CompetitionMode competitionMode,
                               Sex sex, int playerNumbers, boolean registerIndividual, AGE_RESTR ageRestr,
                               boolean differentConditions, int numberOfGroups) {
        this.name = description;
        this.competitionType = competitionType;
        this.competitionMode = competitionMode;
        this.sex = sex;
        this.teamNumbers = playerNumbers;
        this.registerIndividual = registerIndividual;
        this.ageRestr = ageRestr;
        this.differentConditions = differentConditions;
        this.numberOfGroups = numberOfGroups;
    }

    public String getName() {
        return name;
    }

    public CompetitionType getCompetitionType() {
        return competitionType;
    }

    public CompetitionMode getCompetitionMode() {
        return competitionMode;
    }

    public Sex getSex() {
        return sex;
    }

    public int getTeamNumbers() {
        return teamNumbers;
    }

    public boolean isRegisterIndividual() {
        return registerIndividual;
    }

    public AGE_RESTR getAgeRestr() {
        return ageRestr;
    }

    public boolean isDifferentConditions() {
        return differentConditions;
    }

    public int getNumberOfGroups() {
        return numberOfGroups;
    }
}
