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
        StringBuilder query = new StringBuilder("FROM Match m WHERE TRUE");
        Parameters pars = new Parameters();
        if (tournament != null) {
            query.append(" AND m.competition.tournament = :tour");
            pars.and("tour", tournament);
        }
        if (competition != null) {
            query.append(" AND m.competition = :comp");
            pars.and("comp", competition);
        }

        if (player != null) {
            query.append(" AND (m.teamA.playerA = :player OR m.teamA.playerB = :player");
            query.append(" OR m.teamB.playerA = :player OR m.teamB.playerB = :player)");
            pars.and("player", player);
        }

        if (from != null) {
            query.append(" AND :from <= m.begin");
            pars.and("from", from);
        }
        if (to != null) {
            query.append(" AND m.end <= :to");
            pars.and("to", to);
        }

        return find(query.toString(), pars).stream().toList();
    }
}