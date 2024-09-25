package de.secretj12.turnierplaner.resources;


import de.secretj12.turnierplaner.db.entities.Match;
import de.secretj12.turnierplaner.db.entities.Set;
import de.secretj12.turnierplaner.db.entities.Tournament;
import de.secretj12.turnierplaner.db.repositories.MatchRepository;
import de.secretj12.turnierplaner.db.repositories.SetRepository;
import de.secretj12.turnierplaner.db.repositories.TournamentRepository;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserSet;
import de.secretj12.turnierplaner.tools.CommonHelpers;
import io.quarkus.security.UnauthorizedException;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RolesAllowed("director")
@Path("/tournament/{tourName}/competition/{matchId}/set")
public class SetResource {
    @Inject
    TournamentRepository tournaments;
    @Inject
    SetRepository setRepository;
    @Inject
    MatchRepository matchRepository;


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public String updateMatches(@PathParam("tourName") String tourName, @PathParam("matchId") UUID matchId,
                                List<jUserSet> sets) {
        System.out.println("tourName: " + tourName);
        System.out.println("matchId: " + matchId);
        System.out.println("sets: " + sets);
        Tournament tournament = tournaments.getByName(tourName);
        Instant beginGamePhase = tournament.getBeginGamePhase();
        if (beginGamePhase != null && beginGamePhase.isAfter(Instant.now()))
            throw new UnauthorizedException("Cannot update matches before game phase has begun");

        Match match = matchRepository.findById(matchId);
        System.out.println(match);
        if (match == null)
            throw new InternalServerErrorException("Could find match");

        for (jUserSet jSet : sets) {
            Set.SetKey setKey = new Set.SetKey();
            setKey.setMatch(match);
            setKey.setIndex(jSet.getIndex());

            Set set = setRepository.findById(setKey);

            if (set == null) {
                set = new Set();
                set.setKey(setKey);
            }
            set.setScoreA(jSet.getScoreA());
            set.setScoreB(jSet.getScoreB());
            System.out.println(set.getScoreA());
            System.out.println(set.getScoreB());

            setRepository.persist(set);
        }
        System.out.println("Updated matches");

        return "Updated matches";
    }


}
