package de.secretj12.turnierplaner.resources;

import de.secretj12.turnierplaner.entities.Tournament;
import de.secretj12.turnierplaner.repositories.TournamentRepository;
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
        if (securityIdentity.hasRole("director"))
            return tournaments.listAll();
        else
            return tournaments.listAllVisible();
    }

    @GET
    @Path("/canCreate")
    @RolesAllowed("director")
    @Produces(MediaType.TEXT_PLAIN)
    public Response canCreate() {
        return Response.ok("Authorized").build();
    }

    @GET
    @Path("/details")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Tournament getTournament(@QueryParam("tourName") String name) {
        Tournament tournament = tournaments.getByName(name);
        if (securityIdentity.hasRole("director") || tournament.isVisible())
            return tournament;
        return null;
    }

    private Response checkDates(Tournament tournament) {
        if (tournament.getBeginRegistration().isAfter(tournament.getEndRegistration()))
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(),
                            "Begin of registration phase needs to be before it's end")
                    .build();
        if (tournament.getEndRegistration().isAfter(tournament.getBeginGamePhase()))
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(),
                            "End of registration phase needs to be before begin of game phase")
                    .build();
        if (tournament.getBeginGamePhase().isAfter(tournament.getEndGamePhase()))
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(),
                            "Begin of game phase needs to be before it's end")
                    .build();
        return null;
    }

    @POST
    @Path("/add")
    @RolesAllowed("director")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addTournament(Tournament tournament) {

        // TODO check if name exists
        if (getTournament(tournament.getName()) != null)
            return Response.status(Response.Status.CONFLICT).build();
        Response r = checkDates(tournament);
        if (r != null)
            return r;

        tournaments.persist(tournament);
        return Response.ok("successfully added").build();
    }

    @POST
    @Path("/update")
    @RolesAllowed("director")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateTournament(Tournament tournament) {
        if (!getTournament(tournament.getName()).getId()
                .equals(tournament.getId()))
            return Response.status(Response.Status.CONFLICT).build();
        Tournament tour = tournaments.getById(tournament.getId());
        Response r = checkDates(tournament);
        if (r != null)
            return r;

        tournaments.getById(tournament.getId());
        tour.setName(tournament.getName());
        tour.setDescription(tournament.getDescription());
        tour.setVisible(tournament.isVisible());
        tour.setBeginRegistration(tournament.getBeginRegistration());
        tour.setEndRegistration(tournament.getEndRegistration());
        tour.setBeginGamePhase(tournament.getBeginGamePhase());
        tour.setEndGamePhase(tournament.getEndGamePhase());
        tournaments.persist(tour);
        return Response.ok("successfully changed").build();
    }
}
