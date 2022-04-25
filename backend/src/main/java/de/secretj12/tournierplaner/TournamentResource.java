package de.secretj12.tournierplaner;

import de.secretj12.tournierplaner.entities.Tournament;
import de.secretj12.tournierplaner.repositories.TournamentRepository;
import org.jboss.logging.annotations.Param;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Path("/tournaments")
public class TournamentResource {

    @Inject
    TournamentRepository tournaments;

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Tournament> getAllTournaments() {
        return tournaments.listAll();
    }

    @GET
    @Path("/{tourId}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Tournament getTournament(@Param String tourId) {
        return tournaments.find("name", tourId).firstResultOptional().orElse(null);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public String addTournament(Tournament tournament) {
        tournaments.persist(tournament);
        return "successfully added";
    }
}
