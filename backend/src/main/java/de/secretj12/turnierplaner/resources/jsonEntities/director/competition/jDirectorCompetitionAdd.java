package de.secretj12.turnierplaner.resources.jsonEntities.director.competition;

import de.secretj12.turnierplaner.db.entities.competition.*;
import de.secretj12.turnierplaner.resources.jsonEntities.user.competition.jUserCompetition;

public class jDirectorCompetitionAdd extends jUserCompetition {

    public jDirectorCompetitionAdd() {
    }

    public jDirectorCompetitionAdd(Competition competition) {
        super(competition);
    }

    public void toDB(Competition competition) {
        competition.setName(getName());
        competition.setDescription(getDescription());
        switch (getType()) {
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
}
