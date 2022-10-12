package de.secretj12.tournierplaner.resources;

import de.secretj12.tournierplaner.entities.Tournament;
import de.secretj12.tournierplaner.repositories.TournamentRepository;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
    @Path("/{tourName}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Tournament getTournament(@PathParam("tourName") String name) {
        return tournaments.getByName(name);
    }

    @POST
    @RolesAllowed("director")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addTournament(Tournament tournament) {
        if (getTournament(tournament.getName()) != null)
            return Response.status(Response.Status.CONFLICT).build();
        tournaments.persist(tournament);
        return Response.ok("successfully added").build();
    }
}
