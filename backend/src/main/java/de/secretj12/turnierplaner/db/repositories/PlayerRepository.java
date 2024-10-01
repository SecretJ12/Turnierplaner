package de.secretj12.turnierplaner.db.repositories;

import de.secretj12.turnierplaner.db.entities.Player;
import de.secretj12.turnierplaner.db.entities.Sex;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Stream;

@ApplicationScoped
public class PlayerRepository implements PanacheRepository<Player> {


    public Player findById(UUID uuid) {
        return find("id", uuid).firstResultOptional().orElse(null);
    }

    public Player getByName(String first_name, String last_name) {
        return find("FROM Player p WHERE p.firstName = :firstName and p.lastName = :lastName", Parameters
            .with("firstName", first_name)
            .and("lastName", last_name))
                .firstResultOptional().orElse(null);
    }

    public Stream<Player> filter(String search, Sex sex, LocalDate minAge, LocalDate maxAge, boolean admin, int page,
                                 int pageSize) {
        return find("#filter", Parameters
            .with("search", search)
            .and("admin", admin)
            .and("sex", sex)
            .and("ignoreSex", sex == null)
            .and("minAge", minAge)
            .and("ignoreMinAge", minAge == null)
            .and("maxAge", maxAge)
            .and("ignoreMaxAge", maxAge == null))
                .page(page, pageSize).stream();
    }

    public Long countFilter(String search, Sex sex, LocalDate minAge, LocalDate maxAge, boolean admin) {
        return find("#countFilter", Parameters
            .with("search", search)
            .and("admin", admin)
            .and("sex", sex)
            .and("ignoreSex", sex == null)
            .and("minAge", minAge)
            .and("ignoreMinAge", minAge == null)
            .and("maxAge", maxAge)
            .and("ignoreMaxAge", maxAge == null))
                .count();
    }

    public Stream<Player> adminUnverified() {
        return find("#adminUnverified").page(0, 10).stream();
    }
}
