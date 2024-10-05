package de.secretj12.turnierplaner.db.repositories;

import de.secretj12.turnierplaner.db.entities.Config;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class ConfigRepository implements PanacheRepository<Config> {

    public Config findByUUID(UUID uuid) {
        return find("FROM Config v WHERE v.id = ?1", uuid).firstResultOptional().orElse(null);
    }
}
