package de.secretj12.turnierplaner.resources.jsonEntities.director.competition;

import de.secretj12.turnierplaner.enums.*;
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
        switch (getNumberSets()) {
            case THREE -> competition.setNumberSets(NumberSets.THREE);
            case FIVE -> competition.setNumberSets(NumberSets.FIVE);
        }
        switch (getType()) {
            case KNOCKOUT -> competition.setType(CompetitionType.KNOCKOUT);
            case GROUPS -> competition.setType(CompetitionType.GROUPS);
        }
        switch (getMode()) {
            case SINGLE -> competition.setMode(CompetitionMode.SINGLE);
            case DOUBLE -> competition.setMode(CompetitionMode.DOUBLE);
        }
        switch (getSignUp()) {
            case INDIVIDUAL -> competition.setSignup(CompetitionSignUp.INDIVIDUAL);
            case TOGETHER -> competition.setSignup(CompetitionSignUp.TOGETHER);
        }
        switch (getPlayerA().getSex()) {
            case MALE -> competition.setPlayerASex(SexFilter.MALE);
            case FEMALE -> competition.setPlayerASex(SexFilter.FEMALE);
            case ANY -> competition.setPlayerASex(SexFilter.ANY);
        }
        competition.setPlayerAhasMinAge(getPlayerA().isHasMinAge());
        competition.setPlayerAminAge(getPlayerA().getMinAge());
        competition.setPlayerAhasMaxAge(getPlayerA().isHasMaxAge());
        competition.setPlayerAmaxAge(getPlayerA().getMaxAge());
        competition.setPlayerBdifferent(getPlayerB().isDifferent());
        switch (getPlayerB().getSex()) {
            case MALE -> competition.setPlayerBSex(SexFilter.MALE);
            case FEMALE -> competition.setPlayerBSex(SexFilter.FEMALE);
            case ANY -> competition.setPlayerBSex(SexFilter.ANY);
        }
        competition.setPlayerBhasMinAge(getPlayerB().isHasMinAge());
        competition.setPlayerBminAge(getPlayerB().getMinAge());
        competition.setPlayerBhasMaxAge(getPlayerB().isHasMaxAge());
        competition.setPlayerBmaxAge(getPlayerB().getMaxAge());
    }
}
