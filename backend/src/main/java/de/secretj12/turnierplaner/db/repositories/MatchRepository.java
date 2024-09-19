package de.secretj12.turnierplaner.db.repositories;

import de.secretj12.turnierplaner.db.entities.Match;
import de.secretj12.turnierplaner.db.entities.Player;
import de.secretj12.turnierplaner.db.entities.Tournament;
import de.secretj12.turnierplaner.db.entities.competition.Competition;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserMatch;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.ArrayList;
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

    @Inject
    EntityManager em;


    public Match findById(UUID id) {
        return find("id", id).firstResult();
    }

    public void deleteByComp(Competition competition) {
        delete("#deleteByComp", Parameters.with("comp", competition));
    }

    public List<Match> nonGroupMatches(Competition competition) {
        return find("#nonGroupMatches", Parameters.with("comp", competition)).list();
    }

    @Transactional
    public List<jUserMatch> filterMatches(Tournament tournament, Competition competition,
                                          Player player,
                                          Instant from, Instant to) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Match> q = cb.createQuery(Match.class);
        Root<Match> root = q.from(Match.class);
        q.select(root);
        Predicate predicate = applyAllValidPredicates(cb, root, q, tournament, competition, player, from, to);
        q.where(predicate);

        List<Match> result = em.createQuery(q).getResultList();
        return result.stream().map(jUserMatch::new).toList();

    }

    private Predicate applyAllValidPredicates(CriteriaBuilder cb, Root<Match> root, CriteriaQuery<Match> query, Tournament tournament, Competition competition, Player player, Instant from, Instant to) {
        List<Predicate> predicates = new ArrayList<>();
        if (competition != null) {
            predicates.add(cb.equal(root.get("competition"), competition));

        }

        if (tournament != null) {
            predicates.add(cb.equal(root.get("competition").get("tournament"), tournament));
        }

        if (player != null) {
            predicates.add(cb.or(
                    cb.equal(root.get("teamA").get("playerA"), player),
                    cb.equal(root.get("teamA").get("playerB"), player),
                    cb.equal(root.get("teamB").get("playerA"), player),
                    cb.equal(root.get("teamB").get("playerB"), player)
            ));
        }

        if (from != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("begin"), from));
        }

        if (to != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("end"), to));
        }

        Predicate finalPredicate = cb.and(predicates.toArray(new Predicate[0]));
        return finalPredicate;
    }
}