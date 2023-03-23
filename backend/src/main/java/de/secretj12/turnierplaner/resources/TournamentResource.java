package de.secretj12.turnierplaner.resources;

import de.secretj12.turnierplaner.db.entities.Tournament;
import de.secretj12.turnierplaner.db.repositories.TournamentRepository;
import de.secretj12.turnierplaner.resources.jsonEntities.director.jDirectorTournamentAdd;
import de.secretj12.turnierplaner.resources.jsonEntities.director.jDirectorTournamentUpdate;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserTournament;
import io.quarkus.security.identity.SecurityIdentity;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    public Response getAllTournaments() {
        // TODO think about sorting them, creation date?
        if (securityIdentity.hasRole("director"))
            return Response.ok(tournaments.listAll().stream().map(jDirectorTournamentAdd::new).toList()).build();
        else
            return Response.ok(tournaments.listAllVisible().stream().map(jUserTournament::new).toList()).build();
    }

    @GET
    @Path("/canCreate")
    @Produces(MediaType.TEXT_PLAIN)
    public boolean canCreate() {
        return securityIdentity.hasRole("director");
    }

    @GET
    @Path("/{tourName}/details")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTournament(@PathParam("tourName") String name) {
        Tournament tournament = tournaments.getByName(name);
        if (securityIdentity.hasRole("director"))
            return Response.ok(new jDirectorTournamentUpdate(tournament)).build();
        if (tournament.isVisible())
            return Response.ok(new jUserTournament(tournament)).build();
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    private Response checkDates(jDirectorTournamentAdd tournament) {
        if (tournament.getBeginRegistration().isAfter(tournament.getEndRegistration()))
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode())
                    .entity("Begin of registration phase needs to be before it's end").build();
        if (tournament.getEndRegistration().isAfter(tournament.getBeginGamePhase()))
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode())
                    .entity("End of registration phase needs to be before begin of game phase").build();
        if (tournament.getBeginGamePhase().isAfter(tournament.getEndGamePhase()))
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode())
                    .entity("Begin of game phase needs to be before it's end").build();
        return null;
    }

    @POST
    @Path("/add")
    @RolesAllowed("director")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addTournament(jDirectorTournamentAdd tournament) {
        System.out.println(tournament);
        if (tournament.getName() == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        if (tournaments.getByName(tournament.getName()) != null)
            return Response.status(Response.Status.CONFLICT).build();
        Response r = checkDates(tournament);
        if (r != null)
            return r;

        tournaments.persist(tournament.toDB());
        return Response.ok("successfully added").build();
    }

    @POST
    @Path("/update")
    @RolesAllowed("director")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateTournament(jDirectorTournamentUpdate tournament) {
        Tournament possibleDuplicate = tournaments.getByName(tournament.getName());
        if (possibleDuplicate != null &&
                !possibleDuplicate.getId().equals(tournament.getId()))
            return Response.status(Response.Status.CONFLICT).build();

        Tournament dbTournament = tournaments.getById(tournament.getId());
        if (dbTournament == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        Response r = checkDates(tournament);
        if (r != null)
            return r;

        tournaments.getById(tournament.getId());
        dbTournament.setName(tournament.getName());
        dbTournament.setDescription(tournament.getDescription());
        dbTournament.setVisible(tournament.isVisible());
        dbTournament.setBeginRegistration(tournament.getBeginRegistration());
        dbTournament.setEndRegistration(tournament.getEndRegistration());
        dbTournament.setBeginGamePhase(tournament.getBeginGamePhase());
        dbTournament.setEndGamePhase(tournament.getEndGamePhase());
        tournaments.persist(dbTournament);
        return Response.ok("successfully changed").build();
    }
}
