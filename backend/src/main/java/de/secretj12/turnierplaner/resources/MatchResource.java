package de.secretj12.turnierplaner.resources;


import de.secretj12.turnierplaner.db.entities.Player;
import de.secretj12.turnierplaner.db.entities.Tournament;
import de.secretj12.turnierplaner.db.entities.competition.Competition;
import de.secretj12.turnierplaner.db.repositories.*;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserMatchEvent;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
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
    public List<jUserMatchEvent> getMatches(
                                            @QueryParam("tour") String tourName,
                                            @QueryParam("comp") String compName,
                                            @QueryParam("player") String playerId,
                                            @QueryParam("from") String from,
                                            @QueryParam("to") String to
    ) {
        Tournament tournament = tournaments.getByName(tourName);
        Competition competition = competitions.getByName(tourName, compName);
        Player player = playerId == null ? null : players.getById(UUID.fromString(playerId));
        Instant fromD = from == null ? Instant.MIN : Instant.parse(from);
        Instant toD = to == null ? Instant.MAX : Instant.parse(to);

        if (player == null && tournament == null)
            throw new BadRequestException("Need to specify at least a tournament or a player");

        return this.matches.filterMatches(tournament, competition, player, fromD, toD).stream()
            .map(jUserMatchEvent::new)
            .toList();
    }
}
