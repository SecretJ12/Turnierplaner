package de.secretj12.tournierplaner.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

import de.secretj12.tournierplaner.entities.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class PlayerRepository implements PanacheRepository<Player> {

    public Player getByName(String name) {
        return find("last_name", name).firstResultOptional().orElse(null);
    }

    public List<Player> filter(String search) {
        return find("#filter", search).list();
    }
}
