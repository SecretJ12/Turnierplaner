package de.secretj12.turnierplaner.resources.jsonEntities.director.competition;

import de.secretj12.turnierplaner.db.entities.competition.*;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserCompetition;

public class jDirectorCompetition extends jUserCompetition {

    private jDirectorMode mode;
    private jDirectorSignUp signUp;
    private jDirectorPlayerA playerA;
    private jDirectorPlayerB playerB;

    public jDirectorCompetition() {
    }

    public jDirectorCompetition(Competition competition) {
        super(competition);
        switch (competition.getMode()) {
            case SINGLES -> this.mode = jDirectorMode.SINGLE;
            case DOUBLES -> this.mode = jDirectorMode.DOUBLE;
        }
        switch (competition.getSignup()) {
            case INDIVIDUAL -> this.signUp = jDirectorSignUp.INDIVIDUAL;
            case TOGETHER -> this.signUp = jDirectorSignUp.TOGETHER;
        }
        this.playerA = new jDirectorPlayerA();
        switch (competition.getPlayerASex()) {
            case MALE -> this.playerA.setSex(jDirectorValidSex.MALE);
            case FEMALE -> this.playerA.setSex(jDirectorValidSex.FEMALE);
            case ANY -> this.playerA.setSex(jDirectorValidSex.ANY);
        }
        this.playerA.setHasMinAge(competition.playerAhasMinAge());
        this.playerA.setMinAge(competition.getPlayerAminAge());
        this.playerA.setHasMaxAge(competition.playerAhasMaxAge());
        this.playerA.setMaxAge(competition.getPlayerAmaxAge());
        this.playerB = new jDirectorPlayerB();
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

    public void toDB(Competition competition) {
        competition.setName(getName());
        competition.setDescription(getDescription());
        switch(getType()) {
            case KNOCKOUT -> competition.setType(CompetitionType.KNOCKOUT);
            case GROUPS -> competition.setType(CompetitionType.GROUPS);
        }
        switch (getMode()) {
            case SINGLE -> competition.setMode(CompetitionMode.SINGLES);
            case DOUBLE -> competition.setMode(CompetitionMode.DOUBLES);
        }
        switch (getSignUp()) {
            case INDIVIDUAL -> competition.setSignup(CompetitionSignUp.INDIVIDUAL);
            case TOGETHER -> competition.setSignup(CompetitionSignUp.TOGETHER);
        }
        switch (getPlayerA().getSex()) {
            case MALE -> competition.setPlayerASex(Sex.MALE);
            case FEMALE -> competition.setPlayerASex(Sex.FEMALE);
            case ANY -> competition.setPlayerASex(Sex.ANY);
        }
        competition.setPlayerAhasMinAge(getPlayerA().isHasMinAge());
        competition.setPlayerAminAge(getPlayerA().getMinAge());
        competition.setPlayerAhasMaxAge(getPlayerA().isHasMaxAge());
        competition.setPlayerAmaxAge(getPlayerA().getMaxAge());
        competition.setPlayerBdifferent(getPlayerB().isDifferent());
        switch (getPlayerB().getSex()) {
            case MALE -> competition.setPlayerBSex(Sex.MALE);
            case FEMALE -> competition.setPlayerBSex(Sex.FEMALE);
            case ANY -> competition.setPlayerBSex(Sex.ANY);
        }
        competition.setPlayerBhasMinAge(getPlayerB().isHasMinAge());
        competition.setPlayerBminAge(getPlayerB().getMinAge());
        competition.setPlayerBhasMaxAge(getPlayerB().isHasMaxAge());
        competition.setPlayerBmaxAge(getPlayerB().getMaxAge());
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

    public jDirectorPlayerA getPlayerA() {
        return playerA;
    }

    public void setPlayerA(jDirectorPlayerA playerA) {
        this.playerA = playerA;
    }

    public jDirectorPlayerB getPlayerB() {
        return playerB;
    }

    public void setPlayerB(jDirectorPlayerB playerB) {
        this.playerB = playerB;
    }
}
