package de.secretj12.turnierplaner.repositories;

import de.secretj12.turnierplaner.entities.Player;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@ApplicationScoped
public class PlayerRepository implements PanacheRepository<Player> {

    public Player getByName(String first_name, String last_name) {
        return find("first_name = :first_name and last_name = :last_name",
                Parameters.with("first_name", first_name).and("last_name",last_name).map()).firstResultOptional().orElse(null);
    }

    public Stream<Player> filter(String search) {
        return find("#filter", search).page(0, 10).stream();
    }
}
