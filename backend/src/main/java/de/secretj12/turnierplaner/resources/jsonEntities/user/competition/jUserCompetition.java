package de.secretj12.turnierplaner.resources.jsonEntities.user.competition;

import de.secretj12.turnierplaner.db.entities.competition.Competition;
import de.secretj12.turnierplaner.resources.jsonEntities.director.competition.jDirectorMode;
import de.secretj12.turnierplaner.resources.jsonEntities.director.competition.jDirectorSignUp;
import de.secretj12.turnierplaner.resources.jsonEntities.director.competition.jDirectorValidSex;

public class jUserCompetition {
    private String name;
    private String description;
    private jUserCompetitionType type;
    private jDirectorMode mode;
    private jDirectorSignUp signUp;
    private jUserPlayerA playerA;
    private jUserPlayerB playerB;

    public jUserCompetition() {
    }

    public jUserCompetition(Competition competition) {
        this.name = competition.getName();
        this.description = competition.getDescription();
        switch (competition.getType()) {
            case GROUPS -> this.type = jUserCompetitionType.GROUPS;
            case KNOCKOUT -> this.type = jUserCompetitionType.KNOCKOUT;
        }
        switch (competition.getMode()) {
            case SINGLES -> this.mode = jDirectorMode.SINGLE;
            case DOUBLES -> this.mode = jDirectorMode.DOUBLE;
        }
        switch (competition.getSignup()) {
            case INDIVIDUAL -> this.signUp = jDirectorSignUp.INDIVIDUAL;
            case TOGETHER -> this.signUp = jDirectorSignUp.TOGETHER;
        }
        this.playerA = new jUserPlayerA();
        switch (competition.getPlayerASex()) {
            case MALE -> this.playerA.setSex(jDirectorValidSex.MALE);
            case FEMALE -> this.playerA.setSex(jDirectorValidSex.FEMALE);
            case ANY -> this.playerA.setSex(jDirectorValidSex.ANY);
        }
        this.playerA.setHasMinAge(competition.playerAhasMinAge());
        this.playerA.setMinAge(competition.getPlayerAminAge());
        this.playerA.setHasMaxAge(competition.playerAhasMaxAge());
        this.playerA.setMaxAge(competition.getPlayerAmaxAge());
        this.playerB = new jUserPlayerB();
        this.playerB.setDifferent(competition.isPlayerBdifferent());
        switch (competition.getPlayerBSex()) {
            case MALE -> this.playerB.setSex(jDirectorValidSex.MALE);
            case FEMALE -> this.playerB.setSex(jDirectorValidSex.FEMALE);
            case ANY -> this.playerB.setSex(jDirectorValidSex.ANY);
        }
        this.playerB.setHasMinAge(competition.playerBhasMinAge());
        this.playerB.setMinAge(competition.getPlayerBminAge());
        this.playerB.setHasMaxAge(competition.playerBhasMaxAge());
        this.playerB.setMaxAge(competition.getPlayerBmaxAge());
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

    public jDirectorMode getMode() {
        return mode;
    }

    public void setMode(jDirectorMode mode) {
        this.mode = mode;
    }

    public jDirectorSignUp getSignUp() {
        return signUp;
    }

    public void setSignUp(jDirectorSignUp signUp) {
        this.signUp = signUp;
    }

    public jUserPlayerA getPlayerA() {
        return playerA;
    }

    public void setPlayerA(jUserPlayerA playerA) {
        this.playerA = playerA;
    }

    public jUserPlayerB getPlayerB() {
        return playerB;
    }

    public void setPlayerB(jUserPlayerB playerB) {
        this.playerB = playerB;
    }
}
