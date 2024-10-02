package de.secretj12.turnierplaner.model.director;

import de.secretj12.turnierplaner.db.entities.Player;
import de.secretj12.turnierplaner.enums.Sex;
import de.secretj12.turnierplaner.model.user.jUserPlayer;

public class jDirectorPlayer extends jUserPlayer {

  private Sex sex;

  public jDirectorPlayer(Player p) {
    super(p);
    this.sex = p.getSex();
  }

  public Sex getSex() {
    return sex;
  }

  public void setSex(Sex sex) {
    this.sex = sex;
  }
}
