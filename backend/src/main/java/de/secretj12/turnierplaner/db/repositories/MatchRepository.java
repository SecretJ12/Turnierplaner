package de.secretj12.turnierplaner.db.repositories;

import de.secretj12.turnierplaner.db.entities.Competition;
import de.secretj12.turnierplaner.db.entities.Match;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MatchRepository implements PanacheRepository<Match> {

    public Match getFinal(Competition competition) {
        PanacheQuery<Match> result = find("#findFinal", Parameters.with("compId", competition.getId()));
        System.out.println(result.count());
        if (result.count() != 1)
            return null;
        return result.firstResultOptional().orElse(null);
    }
}
