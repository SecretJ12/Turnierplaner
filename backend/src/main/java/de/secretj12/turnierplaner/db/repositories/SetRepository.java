package de.secretj12.turnierplaner.db.repositories;

import de.secretj12.turnierplaner.db.entities.Set;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SetRepository implements PanacheRepository<Set> {
    public Set findById(Set.SetKey key) {
        return find("key", key).firstResult();
    }
}
