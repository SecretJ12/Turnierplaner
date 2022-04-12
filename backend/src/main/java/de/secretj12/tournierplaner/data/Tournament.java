package de.secretj12.tournierplaner.data;

import io.quarkus.arc.impl.Identified;

public class Tournament {
    public String id;
    public String title;
    public String description;

    public Tournament() {
    }

    public Tournament(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }
}
