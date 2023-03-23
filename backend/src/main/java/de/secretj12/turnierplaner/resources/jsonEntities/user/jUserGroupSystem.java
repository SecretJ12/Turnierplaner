package de.secretj12.turnierplaner.resources.jsonEntities.user;

import de.secretj12.turnierplaner.db.entities.groups.Group;

import java.util.List;

public class jUserGroupSystem {

    private List<jUserGroup> groups;
    // TODO knockout matches

    public jUserGroupSystem(List<Group> groups) {
        this.groups = groups.stream().map(jUserGroup::new).toList();
    }

    public List<jUserGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<jUserGroup> groups) {
        this.groups = groups;
    }
}
