package de.secretj12.turnierplaner.resources.jsonEntities.director;

import de.secretj12.turnierplaner.db.entities.Player;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserPlayer;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserSex;

public class jDirectorPlayer extends jUserPlayer {

    private jUserSex sex;

    public jDirectorPlayer(Player p) {
        super(p);
        this.sex = switch (p.getSex()) {
            case MALE -> jUserSex.MALE;
            case FEMALE -> jUserSex.FEMALE;
        };
    }

    public jUserSex getSex() {
        return sex;
    }

    public void setSex(jUserSex sex) {
        this.sex = sex;
    }
}
