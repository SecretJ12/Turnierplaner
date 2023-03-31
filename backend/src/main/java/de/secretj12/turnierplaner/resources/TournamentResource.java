package de.secretj12.turnierplaner.resources;

import de.secretj12.turnierplaner.db.entities.Tournament;
import de.secretj12.turnierplaner.db.repositories.TournamentRepository;
import de.secretj12.turnierplaner.resources.jsonEntities.director.jDirectorTournamentAdd;
import de.secretj12.turnierplaner.resources.jsonEntities.director.jDirectorTournamentUpdate;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserTournament;
import io.quarkus.security.identity.SecurityIdentity;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Comparator;
import java.util.List;

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
    public RestResponse<List<jUserTournament>> getAllTournaments() {
        if (securityIdentity.hasRole("director"))
            return ResponseBuilder.ok(tournaments.listAll().stream().map(jDirectorTournamentAdd::new)
                    .sorted(Comparator.comparing(jUserTournament::getBeginGamePhase))
                    .map(t -> (jUserTournament) t).toList()).build();
        else
            return ResponseBuilder.ok(tournaments.listAllVisible().stream().map(jUserTournament::new)
                    .sorted(Comparator.comparing(jUserTournament::getBeginGamePhase)).toList()).build();
    }

    @GET
    @Path("/canCreate")
    @Produces(MediaType.TEXT_PLAIN)
    public RestResponse<Boolean> canCreate() {
        return ResponseBuilder.ok(securityIdentity.hasRole("director")).build();
    }

    @GET
    @Path("/{tourName}/details")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<jUserTournament> getTournament(@PathParam("tourName") String name) {
        Tournament tournament = tournaments.getByName(name);
        if (securityIdentity.hasRole("director"))
            return ResponseBuilder.ok((jUserTournament) new jDirectorTournamentUpdate(tournament)).build();
        if (tournament.isVisible())
            return ResponseBuilder.ok(new jUserTournament(tournament)).build();
        return RestResponse.status(Response.Status.UNAUTHORIZED);
    }

    private RestResponse<String> checkDates(jDirectorTournamentAdd tournament) {
        if (tournament.getBeginRegistration().isAfter(tournament.getEndRegistration()))
            return ResponseBuilder.create(Response.Status.BAD_REQUEST,
                    "Begin of registration phase needs to be before it's end").build();
        if (tournament.getEndRegistration().isAfter(tournament.getBeginGamePhase()))
            return ResponseBuilder.create(Response.Status.BAD_REQUEST,
                    "End of registration phase needs to be before begin of game phase").build();
        if (tournament.getBeginGamePhase().isAfter(tournament.getEndGamePhase()))
            return ResponseBuilder.create(Response.Status.BAD_REQUEST,
                    "Begin of game phase needs to be before it's end").build();
        return null;
    }

    @POST
    @Path("/add")
    @RolesAllowed("director")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public RestResponse<String> addTournament(jDirectorTournamentAdd tournament) {
        if (tournament.getName() == null)
            return ResponseBuilder.create(Response.Status.BAD_REQUEST, "Tournament name is empty").build();
        if (tournaments.getByName(tournament.getName()) != null)
            return ResponseBuilder
                    .create(Response.Status.CONFLICT, "Tournament with this name already exists").build();
        RestResponse<String> r = checkDates(tournament);
        if (r != null)
            return r;

        tournaments.persist(tournament.toDB());
        return ResponseBuilder.ok("successfully added").build();
    }

    @POST
    @Path("/update")
    @RolesAllowed("director")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public RestResponse<String> updateTournament(jDirectorTournamentUpdate tournament) {
        Tournament possibleDuplicate = tournaments.getByName(tournament.getName());
        if (possibleDuplicate != null &&
                !possibleDuplicate.getId().equals(tournament.getId()))
            return ResponseBuilder
                    .create(Response.Status.CONFLICT, "Tournament with this name already exist")
                    .build();

        Tournament dbTournament = tournaments.getById(tournament.getId());
        if (dbTournament == null)
            return ResponseBuilder
                    .create(Response.Status.BAD_REQUEST, "Tournament with this id does not exist")
                    .build();
        RestResponse<String> r = checkDates(tournament);
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
        return ResponseBuilder.ok("successfully changed").build();
    }
}
