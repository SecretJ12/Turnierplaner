package de.secretj12.tournierplaner.repositories;

import de.secretj12.tournierplaner.entities.Tournament;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class TournamentRepository implements PanacheRepository<Tournament> {

    public List<Tournament> listAll() {
        return listAll();
    }

    public Tournament getByName(String name) {
        return find("name", name).firstResultOptional().orElse(null);
    }
}
