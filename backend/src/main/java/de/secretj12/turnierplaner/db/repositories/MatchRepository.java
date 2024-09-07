package de.secretj12.turnierplaner.db.repositories;

import de.secretj12.turnierplaner.db.entities.Match;
import de.secretj12.turnierplaner.db.entities.competition.Competition;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class MatchRepository implements PanacheRepository<Match> {

    public Match findById(UUID id) {
        return find("id", id).firstResult();
    }

    public Match getFinal(Competition competition) {
        PanacheQuery<Match> result = find("#findHead", Parameters.with("comp", competition).and("finale", true));
        if (result.count() != 1)
            return null;
        return result.firstResultOptional().orElse(null);
    }

    public Match getThirdPlace(Competition competition) {
        PanacheQuery<Match> result = find("#findHead", Parameters.with("comp", competition).and("finale", false));
        if (result.count() != 1)
            return null;
        return result.firstResultOptional().orElse(null);
    }

    public void deleteByComp(Competition competition) {
        delete("#deleteByComp", Parameters.with("comp", competition));
    }

    public void delete(Match m) {
        // normal delete by object sadly does not work :(
        delete("DELETE FROM Match m WHERE m = :match", Parameters.with("match", m));
    }

    public List<Match> nonGroupMatches(Competition competition) {
        return find("#nonGroupMatches", Parameters.with("comp", competition)).list();
    }
}
