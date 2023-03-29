package de.secretj12.turnierplaner.db.repositories;

import de.secretj12.turnierplaner.db.entities.Player;
import de.secretj12.turnierplaner.db.entities.SexType;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.stream.Stream;

@ApplicationScoped
public class PlayerRepository implements PanacheRepository<Player> {

    public Player getByName(String first_name, String last_name) {
        return find("FROM Player p WHERE p.firstName = :firstName and p.lastName = :lastName",
                Parameters.with("firstName", first_name).and("lastName", last_name).map()).firstResultOptional().orElse(null);
    }

    public Stream<Player> filter(String search, SexType sex, LocalDate minAge, LocalDate maxAge) {
        return find("#filter",
                Parameters.with("search", search).and("sex", sex)
                        .and("ignoreSex", sex == null)
                        .and("minAge", minAge).and("ignoreMinAge", minAge == null)
                        .and("maxAge", maxAge).and("ignoreMaxAge", maxAge == null))
                .page(0, 10).stream();
    }
}
