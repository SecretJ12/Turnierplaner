package de.secretj12.turnierplaner.resources.jsonEntities.user.group;

import de.secretj12.turnierplaner.db.entities.Match;
import de.secretj12.turnierplaner.db.entities.groups.Group;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserMatch;

import java.util.List;

public class jUserGroupSystem {

    private List<jUserGroup> groups;
    private jUserMatch finale;
    private jUserMatch thirdPlace;

    public jUserGroupSystem(List<Group> groups, Match finale, Match thirdPlace) {
        this.groups = groups.stream().map(jUserGroup::new).toList();

        if (finale.getFinalOfGroup() != null)
            this.finale = new jUserGroupMatchAfterGroup(finale);
        else
            this.finale = new jUserGroupMatchAfterMatch(finale, finale.getDependentOn().isWinner());
        if (thirdPlace != null)
            if (thirdPlace.getFinalOfGroup() != null)
                this.thirdPlace = new jUserGroupMatchAfterGroup(thirdPlace);
            else
                this.thirdPlace = new jUserGroupMatchAfterMatch(thirdPlace, thirdPlace.getDependentOn()
                    .isWinner(), false);
    }

    public List<jUserGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<jUserGroup> groups) {
        this.groups = groups;
    }

    public jUserMatch getFinale() {
        return finale;
    }

    public void setFinale(jUserMatch finale) {
        this.finale = finale;
    }

    public jUserMatch getThirdPlace() {
        return thirdPlace;
    }

    public void setThirdPlace(jUserMatch thirdPlace) {
        this.thirdPlace = thirdPlace;
    }
}