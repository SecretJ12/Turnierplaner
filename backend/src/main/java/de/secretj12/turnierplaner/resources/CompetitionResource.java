package de.secretj12.turnierplaner.resources;

import de.secretj12.turnierplaner.db.entities.*;
import de.secretj12.turnierplaner.db.entities.groups.Group;
import de.secretj12.turnierplaner.db.repositories.CompetitionRepository;
import de.secretj12.turnierplaner.db.repositories.MatchRepository;
import de.secretj12.turnierplaner.db.repositories.TournamentRepository;
import de.secretj12.turnierplaner.resources.jsonEntities.director.jDirectorCompetitionAdd;
import de.secretj12.turnierplaner.resources.jsonEntities.director.jDirectorCompetitionUpdate;
import de.secretj12.turnierplaner.resources.jsonEntities.user.*;
import io.quarkus.security.identity.SecurityIdentity;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

@Path("/tournament/{tourName}/competition")
public class CompetitionResource {

    @Inject
    PlayerResource players;
    @Inject
    CompetitionRepository competitions;
    @Inject
    TournamentRepository tournaments;
    @Inject
    SecurityIdentity securityIdentity;
    @Inject
    MatchRepository matches;

    @GET
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<jUserCompetition> getAllCompetitions(@PathParam("tourName") String tourName) {
        if (canSee(tourName))
            return competitions.listByName(tourName).stream().map(jUserCompetition::new).toList();
        return null;
    }

    @GET
    @Path("/{compName}/details")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public jUserCompetition getCompetition(@PathParam("tourName") String tourName,
                                           @PathParam("compName") String compName) {
        if (securityIdentity.hasRole("director"))
            return new jDirectorCompetitionUpdate(competitions.getByName(tourName, compName));
        if (canSee(tourName))
            return new jUserCompetition(competitions.getByName(tourName, compName));
        return null;
    }

    @GET
    @Path("/canEdit")
    @Produces(MediaType.TEXT_PLAIN)
    public boolean canEdit(@PathParam("tourName") String tourName) {
        return securityIdentity.hasRole("director");
    }

    @POST
    @Path("/add")
    @RolesAllowed("director")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addCompetition(@PathParam("tourName") String tourName, jDirectorCompetitionAdd competition) {
        if (competition.getName() == null)
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode())
                    .entity("No tournament specified").build();

        if (competitions.getByName(tourName, competition.getName()) != null)
            return Response.status(Response.Status.CONFLICT).build();

        Tournament tournament = tournaments.getByName(tourName);
        if (tournament == null)
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode())
                    .entity("Tournament doesn't exist").build();

        Competition dbCompetition = competition.toDB();
        dbCompetition.setTournament(tournament);
        competitions.persist(dbCompetition);
        return Response.ok("successfully added").build();
    }

    @POST
    @Path("/update")
    @RolesAllowed("director")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateCompetition(@PathParam("tourName") String tourName, jDirectorCompetitionUpdate competition) {
        Competition dbCompetition = competitions.getById(competition.getId());
        if (dbCompetition == null)
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(),
                    "Competition doesn't exist").build();
        if (!dbCompetition.getTournament().getName().equals(tourName))
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode())
                    .entity("Tournament of competition is not the given").build();

        dbCompetition.setName(competition.getName());
        dbCompetition.setDescription(competition.getDescription());
        dbCompetition.setType(competition.getType());
        competitions.persist(dbCompetition);

        return Response.ok("successfully changed").build();
    }

    @GET
    @Path("/{compName}/signedUpPlayers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSignedUpPlayers(@PathParam("tourName") String tourName,
                                       @PathParam("compName") String compName) {
        if (!canSee(tourName))
            return Response.status(Response.Status.UNAUTHORIZED).build();

        Competition competition = competitions.getByName(tourName, compName);
        if (competition == null)
            return null;

        if (!securityIdentity.hasRole("director") &&
                (competition.getTournament().getBeginRegistration().isAfter(LocalDateTime.now())
                        || competition.getTournament().getBeginGamePhase().isBefore(LocalDateTime.now())))
            return Response.status(Response.Status.UNAUTHORIZED).build();

        return Response.ok(competition.getPlayers().stream().map(jUserPlayer::new)
                .sorted((A, B) -> {
                    if (A.getFirstName().equals(B.getFirstName()))
                        return A.getLastName().compareTo(B.getLastName());
                    else
                        return A.getFirstName().compareTo(B.getFirstName());
                }).toList()).build();
    }

    @POST
    @Path("/{compName}/signUp")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response signUpPlayer(@PathParam("tourName") String tourName, @PathParam("compName") String compName,
                                 jUserPlayerSignUpForm reg) {
        if (!canSee(tourName))
            return Response.status(Response.Status.UNAUTHORIZED).build();

        Competition competition = competitions.getByName(tourName, compName);
        if (competition == null)
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode())
                    .entity("Competition doesn't exist").build();

        if (!securityIdentity.hasRole("director") &&
                (competition.getTournament().getBeginRegistration().isAfter(LocalDateTime.now())
                || competition.getTournament().getEndRegistration().isBefore(LocalDateTime.now())))
            return Response.status(Response.Status.UNAUTHORIZED).build();

        Player player = players.playerRepository.getByName(reg.getFirstName(), reg.getLastName());
        if (player == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Player doesn't exist").build();

        List<Player> regPlayers = competition.getPlayers();
        if (regPlayers == null)
            regPlayers = List.of(player);
        else if (regPlayers.contains(player)) {
            return Response.status(Response.Status.CONFLICT.getStatusCode())
                    .entity("Player already registered!").build();
        } else {
            regPlayers.add(player);
        }
        competition.setPlayers(regPlayers);
        competitions.persist(competition);

        // TODO notify by mail?

        return Response.ok().build();
    }

    @GET
    @Path("/{compName}/knockoutMatches")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKnockoutMatches(@PathParam("tourName") String tourName, @PathParam("compName") String compName) {
        if (!canSee(tourName))
            return Response.status(Response.Status.UNAUTHORIZED).build();

        Competition competition = competitions.getByName(tourName, compName);
        if (competition == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        if (competition.getType() != CompetitionType.KNOCKOUT)
            return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();

        Match finale = matches.getFinal(competition);
        Match thirdPlace = matches.getThirdPlace(competition);
        return Response.ok(new jKnockoutSystem(finale, thirdPlace)).build();
    }

    @GET
    @Path("/{compName}/groupMatches")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroupMatches(@PathParam("tourName") String tourName, @PathParam("compName") String compName) {
        if (!canSee(tourName))
            return Response.status(Response.Status.UNAUTHORIZED).build();

        Competition competition = competitions.getByName(tourName, compName);
        if (competition == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        if (competition.getType() != CompetitionType.GROUPS)
            return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();

        List<Group> groups = competition.getGroups();
        if (groups == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        return Response.ok(new jUserGroupSystem(groups)).build();
    }

    private boolean canSee(String tourName) {
        return securityIdentity.hasRole("director") || tournaments.getByName(tourName).isVisible();
    }
}
