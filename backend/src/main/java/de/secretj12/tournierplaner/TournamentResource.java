package de.secretj12.tournierplaner;

import de.secretj12.tournierplaner.data.Tournament;
import org.jboss.logging.annotations.Param;

import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Path("/tournaments")
public class TournamentResource {

    private final Map<String, Tournament> tournaments = new HashMap<>();

    public TournamentResource() {
        tournaments.put("1", new Tournament("1", "2019", "Clubmeisterschaft von vor 2 Jahren"));
        tournaments.put("2", new Tournament("2", "2020", "Clubmeisterschaft von vor 1 Jahr"));
        tournaments.put("3", new Tournament("3", "2021", "Clubmeisterschaft vom letzten Jahr"));
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Tournament> getAllTournaments() {
        return tournaments.values();
    }

    @GET
    @Path("/{tourId}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Tournament getTournament(@Param String tourId) {
        return tournaments.get(tourId);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String addTournament(Tournament tournament) {
        tournaments.put(tournament.id, tournament);
        return "successfully added";
    }
}
