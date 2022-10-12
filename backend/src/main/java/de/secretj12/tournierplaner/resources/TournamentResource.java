package de.secretj12.tournierplaner.resources;

import de.secretj12.tournierplaner.entities.Tournament;
import de.secretj12.tournierplaner.repositories.TournamentRepository;
import io.quarkus.security.identity.SecurityIdentity;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Path("/tournament")
public class TournamentResource {

    @Inject
    TournamentRepository tournaments;
    @Inject
    SecurityIdentity securityIdentity;

    @GET
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Tournament> getAllTournaments() {
        return tournaments.listAll();
    }

    @GET
    @Path("/canCreate")
    @Produces(MediaType.TEXT_PLAIN)
    public Response canCreate() {
        if (securityIdentity.hasRole("director"))
            return Response.ok("Authorized").build();
        else
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").build();
    }

    @GET
    @Path("/details")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Tournament getTournament(@QueryParam("tourName") String name) {
        return tournaments.getByName(name);
    }

    @POST
    @Path("/add")
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
