package de.secretj12.turnierplaner.db.repositories;

import de.secretj12.turnierplaner.db.entities.Match;
import de.secretj12.turnierplaner.db.entities.Player;
import de.secretj12.turnierplaner.db.entities.Tournament;
import de.secretj12.turnierplaner.db.entities.competition.Competition;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class MatchRepository implements PanacheRepository<Match> {
    @Inject
    NextMatchRepository nextMatches;
    @Inject
    FinalOfGroupRepository finalOfGroups;
    @Inject
    MatchOfGroupRepository matchOfGroups;

    public Match findById(UUID id) {
        return find("id", id).firstResult();
    }

    public void deleteByComp(Competition competition) {
        delete("#deleteByComp", Parameters.with("comp", competition));
    }

    public List<Match> nonGroupMatches(Competition competition) {
        return find("#nonGroupMatches", Parameters.with("comp", competition)).list();
    }

    public List<Match> filterMatches(Tournament tournament, Competition competition,
                                     Player player,
                                     Instant from, Instant to) {
        return find("#filterMatches",
            Parameters
                .with("tour", tournament)
                .and("comp", competition)
                .and("player", player)
                .and("from", from)
                .and("to", to)
        ).list();
    }
}
