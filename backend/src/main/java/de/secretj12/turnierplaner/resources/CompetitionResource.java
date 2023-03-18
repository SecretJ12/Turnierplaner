package de.secretj12.turnierplaner.resources;

import de.secretj12.turnierplaner.db.entities.Competition;
import de.secretj12.turnierplaner.db.entities.Player;
import de.secretj12.turnierplaner.db.entities.Tournament;
import de.secretj12.turnierplaner.db.repositories.CompetitionRepository;
import de.secretj12.turnierplaner.db.repositories.TournamentRepository;
import de.secretj12.turnierplaner.resources.jsonEntities.director.jDirectorCompetitionAdd;
import de.secretj12.turnierplaner.resources.jsonEntities.director.jDirectorCompetitionUpdate;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserCompetition;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserPlayer;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserPlayerSignUpForm;
import io.quarkus.security.identity.SecurityIdentity;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/competition")
public class CompetitionResource {

    @Inject
    PlayerResource players;
    @Inject
    CompetitionRepository competitions;
    @Inject
    TournamentRepository tournaments;
    @Inject
    SecurityIdentity securityIdentity;

    @GET
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<jUserCompetition> getAllCompetitions(@QueryParam("tourName") String tourName) {
        if (canSee(tourName))
            return competitions.listByName(tourName).stream().map(jUserCompetition::new).toList();
        return null;
    }

    @GET
    @Path("/details")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public jUserCompetition getCompetition(@QueryParam("tourName") String tourName, @QueryParam("compName") String compName) {
        if (securityIdentity.hasRole("director"))
            return new jDirectorCompetitionUpdate(competitions.getByName(tourName, compName));
        if (canSee(tourName))
            return new jUserCompetition(competitions.getByName(tourName, compName));
        return null;
    }

    @GET
    @Path("/canEdit")
    @Produces(MediaType.TEXT_PLAIN)
    public Response canEdit() {
        // TODO don't return via status code but via text
        if (securityIdentity.hasRole("director"))
            return Response.ok("Authorized").build();
        else
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").build();
    }

    @POST
    @Path("/add")
    @RolesAllowed("director")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addCompetition(jDirectorCompetitionAdd competition) {
        if (competition.getName() == null)
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(),
                    "No tournament specified").build();

        if (competitions.getByName(competition.getTourName(), competition.getName()) != null)
            return Response.status(Response.Status.CONFLICT).build();

        Tournament tournament = tournaments.getByName(competition.getTourName());
        if (tournament == null)
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(),
                    "Tournament doesn't exist").build();

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
    public Response updateCompetition(jDirectorCompetitionUpdate competition) {
        Competition dbCompetition = competitions.getById(competition.getId());
        if (dbCompetition == null)
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(),
                    "Competition doesn't exist").build();

        competitions.persist(dbCompetition);
        dbCompetition.setName(competition.getName());
        dbCompetition.setDescription(competition.getDescription());
        dbCompetition.setType(competition.getType());

        return Response.ok("successfully changed").build();
    }

    @GET
    @Path("/signUpedPlayers") //TODO signUped rename?
    @Produces(MediaType.APPLICATION_JSON)
    public List<jUserPlayer> getSignedUpPlayers(@QueryParam("tourName") String tourName,
                                                @QueryParam("compName") String compName) {
        // TODO only allow for if role > director or current date after begin of registration phase
        Competition competition = competitions.getByName(tourName, compName);
        if (competition == null)
            return null;

        return competition.getPlayers().stream().map(jUserPlayer::new)
                .sorted((A, B) -> {
                    if (A.getFirstName().equals(B.getFirstName()))
                        return A.getLastName().compareTo(B.getLastName());
                    else
                        return A.getFirstName().compareTo(B.getFirstName());
                }).toList();
    }

    @POST
    @Path("/signUp")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response signUpPlayer(jUserPlayerSignUpForm reg) {
        // TODO only allow for role > director or current date in registration phase
        Competition competition = competitions.getByName(reg.getTourName(), reg.getCompName());
        Player player = players.playerRepository.getByName(reg.getFirstName(), reg.getLastName());
        if (competition == null)
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(),
                    "Competition doesn't exist").build();
        if (player == null)
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(),
                    "Player doesn't exist").build();

        List<Player> regPlayers = competition.getPlayers();
        if (regPlayers == null)
            regPlayers = List.of(player);
        else if(regPlayers.contains(player)){
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(),"Player already registered!").build();
        }
        else{
            regPlayers.add(player);
        }
        competition.setPlayers(regPlayers);
        competitions.persist(competition);
        // TODO verify by mail?

        return Response.ok().build();
    }

    private boolean canSee(String tourName) {
        return securityIdentity.hasRole("director") || tournaments.getByName(tourName).isVisible();
    }
}
