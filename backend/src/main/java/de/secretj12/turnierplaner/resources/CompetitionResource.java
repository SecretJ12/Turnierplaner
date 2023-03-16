package de.secretj12.turnierplaner.resources;

import de.secretj12.turnierplaner.entities.Competition;
import de.secretj12.turnierplaner.entities.Player;
import de.secretj12.turnierplaner.entities.Tournament;
import de.secretj12.turnierplaner.repositories.CompetitionRepository;
import de.secretj12.turnierplaner.repositories.TournamentRepository;
import de.secretj12.turnierplaner.resources.FormEntities.ReducedCompetition;
import de.secretj12.turnierplaner.resources.FormEntities.ReducedPlayer;
import de.secretj12.turnierplaner.resources.FormEntities.RegisterPlayerForCompetition;
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
    public List<ReducedCompetition> getAllCompetitions(@QueryParam("tourName") String tourName) {
        if (canSee(tourName))
            return competitions.listByName(tourName).stream().map(ReducedCompetition::new).toList();
        return null;
    }

    @GET
    @Path("/details")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public ReducedCompetition getCompetition(@QueryParam("tourName") String tourName, @QueryParam("compName") String compName) {
        if (canSee(tourName))
            return new ReducedCompetition(competitions.getByName(tourName, compName));
        return null;
    }

    @GET
    @Path("/canEdit")
    @Produces(MediaType.TEXT_PLAIN)
    public Response canEdit() {
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
    public Response addCompetition(Competition competition) {
        if (competition.getTournament() == null)
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(),
                    "No tournament specified").build();
        if (competitions.getByName(competition.getTournament().getName(), competition.getName()) != null)
            return Response.status(Response.Status.CONFLICT).build();

        Tournament tournament = tournaments.getByName(competition.getTournament().getName());
        if (tournament == null)
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(),
                    "Tournament doesn't exist").build();

        competition.setTournament(tournament);
        competitions.persist(competition);
        return Response.ok("successfully added").build();
    }

    @POST
    @Path("/update")
    @RolesAllowed("director")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateCompetition(Competition competition) {
        Competition savedCompetition = competitions.getById(competition.getId());
        if (savedCompetition == null)
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(),
                    "Competition doesn't exist").build();

        competitions.persist(savedCompetition);
        savedCompetition.setName(competition.getName());
        savedCompetition.setDescription(competition.getDescription());
        savedCompetition.setType(competition.getType());

        return Response.ok("successfully changed").build();
    }

    @GET
    @Path("/registered")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ReducedPlayer> getRegisteredPlayers(@QueryParam("tourName") String tourName,
                                                    @QueryParam("compName") String compName) {
        // TODO only allow for if role > director or current date after begin of registration phase
        Competition competition = competitions.getByName(tourName, compName);
        System.out.println("Hello World");
        if (competition == null){
            System.out.println("Nicht gefunden");
            return null;
        }

        return competition.getPlayers().stream().map(ReducedPlayer::new)
                .sorted((A, B) -> {
                    if (A.getFirstName().equals(B.getFirstName()))
                        return A.getLastName().compareTo(B.getLastName());
                    else
                        return A.getFirstName().compareTo(B.getFirstName());
                }).toList();
    }

    @POST
    @Path("/register")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response registerPlayer(RegisterPlayerForCompetition reg) {
        // TODO only allow for role > director or current date in registration phase
        Competition competition = competitions.getByName(reg.getTourName(), reg.getCompName());
        System.out.println(reg.getTourName()+" " +reg.getCompName());
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
