package de.secretj12.turnierplaner.resources.jsonEntities.user.competition;

import de.secretj12.turnierplaner.db.entities.competition.Competition;
import de.secretj12.turnierplaner.db.entities.competition.CreationProgress;
import de.secretj12.turnierplaner.resources.jsonEntities.director.competition.jDirectorMode;
import de.secretj12.turnierplaner.resources.jsonEntities.director.competition.jDirectorSignUp;
import de.secretj12.turnierplaner.resources.jsonEntities.director.competition.jDirectorValidSex;
import jakarta.ws.rs.NotFoundException;

public class jUserCompetition {
    private String name;
    private String description;
    private jUserCompetitionType type;
    private jDirectorMode mode;
    private jDirectorSignUp signUp;
    private jUserConfigPlayerA playerA;
    private jUserConfigPlayerB playerB;
    private CreationProgress cProgress;

    public jUserCompetition() {
    }

    public jUserCompetition(Competition competition) {
        if (competition == null)
            throw new NotFoundException("Competition was not found");

        this.name = competition.getName();
        this.description = competition.getDescription();
        this.type = switch (competition.getType()) {
            case GROUPS -> jUserCompetitionType.GROUPS;
            case KNOCKOUT -> jUserCompetitionType.KNOCKOUT;
        };
        this.mode = switch (competition.getMode()) {
            case SINGLES -> jDirectorMode.SINGLE;
            case DOUBLES -> jDirectorMode.DOUBLE;
        };
        this.signUp = switch (competition.getSignup()) {
            case INDIVIDUAL -> jDirectorSignUp.INDIVIDUAL;
            case TOGETHER -> jDirectorSignUp.TOGETHER;
        };
        this.playerA = new jUserConfigPlayerA();
        this.playerA.setSex(switch (competition.getPlayerASex()) {
            case MALE -> jDirectorValidSex.MALE;
            case FEMALE -> jDirectorValidSex.FEMALE;
            case ANY -> jDirectorValidSex.ANY;
            case null -> null;
        });
        this.playerA.setHasMinAge(competition.playerAhasMinAge());
        this.playerA.setMinAge(competition.getPlayerAminAge());
        this.playerA.setHasMaxAge(competition.playerAhasMaxAge());
        this.playerA.setMaxAge(competition.getPlayerAmaxAge());
        this.playerB = new jUserConfigPlayerB();
        this.playerB.setDifferent(competition.isPlayerBdifferent());
        this.playerB.setSex(switch (competition.getPlayerBSex()) {
            case MALE -> jDirectorValidSex.MALE;
            case FEMALE -> jDirectorValidSex.FEMALE;
            case ANY -> jDirectorValidSex.ANY;
            case null -> null;
        });
        this.playerB.setHasMinAge(competition.playerBhasMinAge());
        this.playerB.setMinAge(competition.getPlayerBminAge());
        this.playerB.setHasMaxAge(competition.playerBhasMaxAge());
        this.playerB.setMaxAge(competition.getPlayerBmaxAge());

        this.cProgress = competition.getcProgress();
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

    public jUserConfigPlayerA getPlayerA() {
        return playerA;
    }

    public void setPlayerA(jUserConfigPlayerA playerA) {
        this.playerA = playerA;
    }

    public jUserConfigPlayerB getPlayerB() {
        return playerB;
    }

    public void setPlayerB(jUserConfigPlayerB playerB) {
        this.playerB = playerB;
    }

    public CreationProgress getcProgress() {
        return cProgress;
    }

    public void setcProgress(CreationProgress cProgress) {
        this.cProgress = cProgress;
    }
}
