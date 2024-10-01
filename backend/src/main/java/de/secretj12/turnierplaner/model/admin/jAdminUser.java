package de.secretj12.turnierplaner.model.admin;

import de.secretj12.turnierplaner.enums.jAdminRole;
import org.keycloak.representations.idm.UserRepresentation;

public class jAdminUser {

    private String id;
    private String username;

    private jAdminRole role;

    public jAdminUser() {
    }

    public jAdminUser(UserRepresentation user, jAdminRole role) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public jAdminRole getRole() {
        return role;
    }

    public void setRole(jAdminRole role) {
        this.role = role;
    }
}
