package de.secretj12.turnierplaner.enums;

public enum jAdminRole {
    USER("user"), REPORTER("reporter"), DIRECTOR("director"), ADMIN("admin");

    public final String label;

    jAdminRole(String label) {
        this.label = label;
    }
}
