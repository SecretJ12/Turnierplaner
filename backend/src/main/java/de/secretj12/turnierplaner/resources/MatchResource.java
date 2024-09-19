package de.secretj12.turnierplaner.resources;


import de.secretj12.turnierplaner.db.entities.Player;
import de.secretj12.turnierplaner.db.entities.Tournament;
import de.secretj12.turnierplaner.db.entities.competition.Competition;
import de.secretj12.turnierplaner.db.repositories.*;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserMatch;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Path("/matches")
public class MatchResource {

    @Inject
    TournamentRepository tournaments;
    @Inject
    CompetitionRepository competitions;
    @Inject
    MatchRepository matches;
    @Inject
    CourtRepositiory courts;
    @Inject
    PlayerRepository players;
    @Inject
    SecurityIdentity securityIdentity;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<jUserMatch> getMatches(
            @QueryParam("tour") String tourName,
            @QueryParam("comp") String compName,
            @QueryParam("player") String playerId,
            @QueryParam("from") String from,
            @QueryParam("to") String to
    ) {
        Tournament tournament = tournaments.getByName(tourName);
        Competition competition = competitions.getByName(tourName, compName);
        Player player = playerId.isEmpty() ? null : players.getById(UUID.fromString(playerId));
        Instant fromD = from.isEmpty() ? null : Instant.parse(from);
        Instant toD = to.isEmpty() ? null : Instant.parse(to);


        System.out.println(competition);
//        if (player == null && tournament == null)
//            throw new BadRequestException("Need to specify at least a tournament or a player");

        List<jUserMatch> matchList = matches.filterMatches(tournament, competition, player, fromD, toD);
        System.out.println(matchList.size());
        return matchList;
//        return this.matches.filterMatches(tournament, competition, player, fromD, toD).stream()
//            .map(jUserMatchEvent::new)
//            .toList();
    }
}
