package de.secretj12.turnierplaner.resources;

import de.secretj12.turnierplaner.db.entities.Tournament;
import de.secretj12.turnierplaner.db.repositories.TournamentRepository;
import de.secretj12.turnierplaner.resources.jsonEntities.director.jDirectorTournamentAdd;
import de.secretj12.turnierplaner.resources.jsonEntities.director.jDirectorTournamentUpdate;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserTournament;
import io.quarkus.security.UnauthorizedException;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
    public List<jUserTournament> getAllTournaments() {
        if (securityIdentity.hasRole("director"))
            return tournaments.streamAll()
                .map(jDirectorTournamentAdd::new)
                .sorted(Comparator.comparing(jUserTournament::getBeginGamePhase))
                .map(t -> (jUserTournament) t).toList();
        else
            return tournaments.listAllVisible()
                .stream()
                .map(jUserTournament::new)
                .sorted(Comparator.comparing(jUserTournament::getBeginGamePhase))
                .toList();
    }

    @GET
    @Path("/canCreate")
    @Produces(MediaType.TEXT_PLAIN)
    public Boolean canCreate() {
        return securityIdentity.hasRole("director");
    }

    @GET
    @Path("/{tourName}/details")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public jUserTournament getTournament(@PathParam("tourName") String name) {
        Tournament tournament = tournaments.getByName(name);
        checkTournamentAccessibility(tournament);
        if (securityIdentity.hasRole("director"))
            return new jDirectorTournamentUpdate(tournament);
        else
            return new jUserTournament(tournament);
    }

    private void checkDates(jDirectorTournamentAdd tournament) {
        if (tournament.getBeginRegistration().isAfter(tournament.getEndRegistration()))
            throw new BadRequestException("Begin of registration phase needs to be before it's end");
        if (tournament.getEndRegistration().isAfter(tournament.getBeginGamePhase()))
            throw new BadRequestException("End of registration phase needs to be before begin of game phase");
        if (tournament.getBeginGamePhase().isAfter(tournament.getEndGamePhase()))
            throw new BadRequestException("Begin of game phase needs to be before it's end");
    }

    @POST
    @Path("/add")
    @RolesAllowed("director")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String addTournament(jDirectorTournamentAdd tournament) {
        if (tournament.getName() == null)
            throw new BadRequestException("Tournament name is empty");
        if (tournaments.getByName(tournament.getName()) != null)
            throw new BadRequestException("Tournament with this name already exists");
        checkDates(tournament);

        tournaments.persist(tournament.toDB());
        return "successfully added";
    }

    @POST
    @Path("/update")
    @RolesAllowed("director")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String updateTournament(jDirectorTournamentUpdate tournament) {
        Tournament possibleDuplicate = tournaments.getByName(tournament.getName());
        if (possibleDuplicate != null && !possibleDuplicate.getId().equals(tournament.getId()))
            throw new WebApplicationException("Tournament with this name already exist", Response.Status.CONFLICT);

        Tournament dbTournament = tournaments.getById(tournament.getId());
        if (dbTournament == null)
            throw new NotFoundException("Tournament with this id does not exist");
        checkDates(tournament);

        tournaments.getById(tournament.getId());
        dbTournament.setName(tournament.getName());
        dbTournament.setDescription(tournament.getDescription());
        dbTournament.setVisible(tournament.isVisible());
        dbTournament.setBeginRegistration(tournament.getBeginRegistration());
        dbTournament.setEndRegistration(tournament.getEndRegistration());
        dbTournament.setBeginGamePhase(tournament.getBeginGamePhase());
        dbTournament.setEndGamePhase(tournament.getEndGamePhase());
        tournaments.persist(dbTournament);
        return "successfully changed";
    }

    private void checkTournamentAccessibility(Tournament tournament) {
        if (tournament == null)
            throw new NotFoundException("Tournament could not be found");
        if (!securityIdentity.hasRole("director") && !tournament.isVisible())
            throw new UnauthorizedException("Cannot access tournament");
    }
}
