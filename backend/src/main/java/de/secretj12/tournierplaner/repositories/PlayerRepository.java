package de.secretj12.tournierplaner.repositories;

import de.secretj12.tournierplaner.entities.Player;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@ApplicationScoped
public class PlayerRepository implements PanacheRepository<Player> {

    public Player getByName(String name) {
        return find("last_name", name).firstResultOptional().orElse(null);
    }

    public Stream<Player> filter(String search) {
        return find("#filter", search).page(0, 10).stream();
    }
}
