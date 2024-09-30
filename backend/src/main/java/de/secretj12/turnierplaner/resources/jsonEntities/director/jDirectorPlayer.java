package de.secretj12.turnierplaner.resources.jsonEntities.director;

import de.secretj12.turnierplaner.db.entities.Player;
import de.secretj12.turnierplaner.db.entities.Sex;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserPlayer;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserSex;

import java.util.UUID;

public class jDirectorPlayer extends jUserPlayer {

    private jUserSex sex;

    public jDirectorPlayer(UUID id, String firstName, String lastName, Sex sex) {
        super(id, firstName, lastName);
    }

    public jDirectorPlayer(Player p) {
        super(p);
    }

    public jUserSex getSex() {
        return sex;
    }

    public void setSex(jUserSex sex) {
        this.sex = sex;
    }
}
