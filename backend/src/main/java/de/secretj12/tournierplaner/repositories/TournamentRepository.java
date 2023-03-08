package de.secretj12.tournierplaner.repositories;

import de.secretj12.tournierplaner.entities.Tournament;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class TournamentRepository implements PanacheRepository<Tournament> {

    public List<Tournament> listAllVisible() {
        return find("visible", true).list();
    }

    public Tournament getByName(String name) {
        return find("name", name).firstResultOptional().orElse(null);
    }

    public Tournament getById(UUID uuid) {
        return find("id", uuid).firstResultOptional().orElse(null);
    }
}
