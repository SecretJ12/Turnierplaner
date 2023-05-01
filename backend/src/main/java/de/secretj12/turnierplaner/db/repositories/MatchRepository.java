package de.secretj12.turnierplaner.db.repositories;

import de.secretj12.turnierplaner.db.entities.competition.Competition;
import de.secretj12.turnierplaner.db.entities.Match;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MatchRepository implements PanacheRepository<Match> {

    public Match getFinal(Competition competition) {
        PanacheQuery<Match> result = find("#findHead", Parameters.with("compId", competition.getId()).and("finale", true));
        if (result.count() != 1)
            return null;
        return result.firstResultOptional().orElse(null);
    }

    public Match getThirdPlace(Competition competition) {
        PanacheQuery<Match> result = find("#findHead", Parameters.with("compId", competition.getId()).and("finale", false));
        if (result.count() != 1)
            return null;
        return result.firstResultOptional().orElse(null);
    }
}
