package de.secretj12.turnierplaner.db.repositories;

import de.secretj12.turnierplaner.db.entities.Player;
import de.secretj12.turnierplaner.db.entities.competition.Team;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class TeamRepository implements PanacheRepository<Team> {
    public Team getById(UUID uuid) {
        return find("id", uuid).firstResultOptional().orElse(null);
    }
}
