package de.secretj12.turnierplaner.model.user.knockout;

import de.secretj12.turnierplaner.db.entities.Match;
import jakarta.ws.rs.NotFoundException;

public class jUserKnockoutSystem {

  private jUserKnockoutMatch finale;
  private jUserKnockoutMatch thirdPlace;

  public jUserKnockoutSystem(Match finale, Match thirdPlace, boolean nullable) {
    if (finale == null && !nullable) throw new NotFoundException("Finale was not found");
    if (thirdPlace == null && !nullable)
      throw new NotFoundException("Game for third place was not found");

    this.finale = finale == null ? null : new jUserKnockoutMatch(finale);
    this.thirdPlace = thirdPlace == null ? null : new jUserKnockoutMatch(thirdPlace, false);
  }

  public jUserKnockoutMatch getFinale() {
    return finale;
  }

  public void setFinale(jUserKnockoutMatch finale) {
    this.finale = finale;
  }

  public jUserKnockoutMatch getThirdPlace() {
    return thirdPlace;
  }

  public void setThirdPlace(jUserKnockoutMatch thirdPlace) {
    this.thirdPlace = thirdPlace;
  }
}
