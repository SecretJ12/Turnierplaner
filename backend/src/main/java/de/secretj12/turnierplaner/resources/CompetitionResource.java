package de.secretj12.turnierplaner.resources;

import de.secretj12.turnierplaner.db.entities.Match;
import de.secretj12.turnierplaner.db.entities.Player;
import de.secretj12.turnierplaner.db.entities.SexType;
import de.secretj12.turnierplaner.db.entities.Tournament;
import de.secretj12.turnierplaner.db.entities.competition.*;
import de.secretj12.turnierplaner.db.entities.groups.Group;
import de.secretj12.turnierplaner.db.repositories.CompetitionRepository;
import de.secretj12.turnierplaner.db.repositories.MatchRepository;
import de.secretj12.turnierplaner.db.repositories.TeamRepository;
import de.secretj12.turnierplaner.db.repositories.TournamentRepository;
import de.secretj12.turnierplaner.resources.jsonEntities.director.competition.jDirectorCompetitionAdd;
import de.secretj12.turnierplaner.resources.jsonEntities.director.competition.jDirectorCompetitionUpdate;
import de.secretj12.turnierplaner.resources.jsonEntities.user.competition.jUserCompetition;
import de.secretj12.turnierplaner.resources.jsonEntities.user.group.jUserGroupSystem;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserPlayerSignUpForm;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserTeam;
import de.secretj12.turnierplaner.resources.jsonEntities.user.knockout.jUserKnockoutSystem;
import io.quarkus.security.UnauthorizedException;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
    @Inject
    TeamRepository teams;

    @GET
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<jUserCompetition> getAllCompetitions(@PathParam("tourName") String tourName) {
        checkTournamentAccessibility(tourName);
        return competitions.listByName(tourName).stream().map(jUserCompetition::new).toList();
    }

    @GET
    @Path("/{compName}/details")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public jUserCompetition getCompetition(@PathParam("tourName") String tourName,
                                           @PathParam("compName") String compName) {
        checkTournamentAccessibility(tourName);

        if (securityIdentity.hasRole("director"))
            return new jDirectorCompetitionUpdate(competitions.getByName(tourName, compName));
        return new jUserCompetition(competitions.getByName(tourName, compName));
    }

    @GET
    @Path("/canEdit")
    @Produces(MediaType.TEXT_PLAIN)
    public Boolean canEdit(@PathParam("tourName") String tourName) {
        return securityIdentity.hasRole("director");
    }

    @POST
    @Path("/add")
    @RolesAllowed("director")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String addCompetition(@PathParam("tourName") String tourName, jDirectorCompetitionAdd competition) {
        if (competition.getName() == null)
            throw new BadRequestException("No competition specified");

        if (competitions.getByName(tourName, competition.getName()) != null)
            throw new BadRequestException("Competition already exists");

        Tournament tournament = tournaments.getByName(tourName);
        if (tournament == null)
            throw new BadRequestException("Tournament doesn't exist");

        if (competition.getPlayerA().isHasMinAge() && competition.getPlayerA().getMinAge() == null)
            throw new BadRequestException("Player A: Min age null although has min age");
        if (competition.getPlayerA().isHasMaxAge() && competition.getPlayerA().getMaxAge() == null)
            throw new BadRequestException("Player A: Max age null although has max age");
        if (competition.getPlayerB().isHasMinAge() && competition.getPlayerB().getMinAge() == null)
            throw new BadRequestException("Player B: Min age null although has min age");
        if (competition.getPlayerB().isHasMaxAge() && competition.getPlayerB().getMaxAge() == null)
            throw new BadRequestException("Player B: Max age null although has max age");

        Competition dbCompetition = new Competition();
        competition.toDB(dbCompetition);
        dbCompetition.setTournament(tournament);
        competitions.persist(dbCompetition);
        return "successfully added";
    }

    @POST
    @Path("/update")
    @RolesAllowed("director")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String updateCompetition(@PathParam("tourName") String tourName, jDirectorCompetitionUpdate competition) {
        Competition dbCompetition = competitions.getById(competition.getId());
        if (dbCompetition == null)
            throw new BadRequestException("Competition doesn't exist");
        if (!dbCompetition.getTournament().getName().equals(tourName))
            throw new BadRequestException("Tournament of competition is not the given");

        competition.toDB(dbCompetition);
        competitions.persist(dbCompetition);

        // TODO update players (remove all not fitting, warn in frontend if conditions are changed!)

        return "successfully changed";
    }

    @GET
    @Path("/{compName}/signedUpTeams")
    @Produces(MediaType.APPLICATION_JSON)
    public List<jUserTeam> getSignedUpPlayers(@PathParam("tourName") String tourName,
                                              @PathParam("compName") String compName) {
        checkTournamentAccessibility(tourName);

        Competition competition = competitions.getByName(tourName, compName);
        if (competition == null)
            throw new NotFoundException("Competition was not found");

        if (!securityIdentity.hasRole("director") &&
                (competition.getTournament().getBeginRegistration().isAfter(LocalDateTime.now())
                        || competition.getTournament().getBeginGamePhase().isBefore(LocalDateTime.now())))
            throw new NotAuthorizedException("Registration phase is not active");

        return competition.getTeams().stream().map(jUserTeam::new).toList();
    }

    @POST
    @Path("/{compName}/signUp")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String signUpPlayer(@PathParam("tourName") String tourName, @PathParam("compName") String compName,
                               jUserPlayerSignUpForm reg) {
        checkTournamentAccessibility(tourName);

        Competition competition = competitions.getByName(tourName, compName);
        if (competition == null)
            throw new BadRequestException("Competition doesn't exist");

        if (!securityIdentity.hasRole("director") && // or registration phase
                (competition.getTournament().getBeginRegistration().isAfter(LocalDateTime.now())
                        || competition.getTournament().getEndRegistration().isBefore(LocalDateTime.now())))
            throw new NotAuthorizedException("Registration phase is not active");

        if (competition.getMode() == CompetitionMode.SINGLES
                || (competition.getSignup() == CompetitionSignUp.INDIVIDUAL && !competition.isPlayerBdifferent())) {
            // single mode or double with individual registration but same constraints
            // -> every registration as player A and player B is null

            if (reg.getPlayerA() == null)
                throw new BadRequestException("Player A is null");
            if (reg.getPlayerB() != null)
                throw new BadRequestException("Player B is not null");

            Player playerA = players.playerRepository
                    .getByName(reg.getPlayerA().getFirstName(), reg.getPlayerA().getLastName());
            if (playerA == null)
                throw new BadRequestException("Player A does not exist");

            if (conditionsFailA(competition, playerA))
                throw new BadRequestException("Player A does not meed the conditions");

            List<Team> regTeams = competition.getTeams();
            if (regTeams != null && regTeams.stream()
                    .anyMatch(t -> (t.getPlayerA() != null && t.getPlayerA().getId().equals(playerA.getId()))
                            || (t.getPlayerB() != null && t.getPlayerB().getId().equals(playerA.getId()))))
                throw new WebApplicationException("Player already registered", Response.Status.CONFLICT);

            Team team = new Team();
            team.setPlayerA(playerA);
            team.setCompetition(competition);
            teams.persist(team);
        } else {
            // double mode
            if (competition.getSignup() == CompetitionSignUp.INDIVIDUAL && competition.isPlayerBdifferent()) {
                // double mode with individual registration and different constraints
                // -> each registration needs to be player A xor player B null

                if (reg.getPlayerA() == null && reg.getPlayerB() == null)
                    throw new BadRequestException("Player A and player B are null");
                if (reg.getPlayerA() != null && reg.getPlayerB() != null)
                    throw new BadRequestException("Player A and player B are not null");

                if (reg.getPlayerA() != null) {
                    Player playerA = players.playerRepository.getByName(reg.getPlayerA().getFirstName(), reg.getPlayerA().getLastName());
                    if (playerA == null)
                        throw new BadRequestException("Player A doesn't exist");

                    if (conditionsFailA(competition, playerA))
                        throw new BadRequestException("Player A is does not meet the conditions");

                    List<Team> regTeams = competition.getTeams();
                    if (regTeams != null && regTeams.stream()
                            .anyMatch(t -> (t.getPlayerA() != null && t.getPlayerA().getId().equals(playerA.getId()))
                                    || (t.getPlayerB() != null && t.getPlayerB().getId().equals(playerA.getId()))))
                        throw new WebApplicationException("Player already registered", Response.Status.CONFLICT);

                    Team team = new Team();
                    team.setPlayerA(playerA);
                    team.setCompetition(competition);
                    teams.persist(team);
                }
                if (reg.getPlayerB() != null) {
                    Player playerB = players.playerRepository.getByName(reg.getPlayerB().getFirstName(), reg.getPlayerB().getLastName());
                    if (playerB == null)
                        throw new BadRequestException("Player B does not exist");

                    if (conditionsFailB(competition, playerB))
                        throw new BadRequestException("Player B is does not meet the conditions");

                    List<Team> regTeams = competition.getTeams();
                    if (regTeams != null && regTeams.stream()
                            .anyMatch(t -> (t.getPlayerA() != null && t.getPlayerA().getId().equals(playerB.getId()))
                                    || (t.getPlayerB() != null && t.getPlayerB().getId().equals(playerB.getId()))))
                        throw new WebApplicationException("Player already registered", Response.Status.CONFLICT);

                    Team team = new Team();
                    team.setPlayerB(playerB);
                    team.setCompetition(competition);
                    teams.persist(team);
                }
            } else {
                // double mode with registration together and any constraints
                // -> needs player A and player B to be not null

                if (reg.getPlayerA() == null)
                    throw new BadRequestException("Player A is null");
                if (reg.getPlayerB() == null)
                    throw new BadRequestException("Player B is null");

                Player playerA = players.playerRepository.getByName(reg.getPlayerA().getFirstName(), reg.getPlayerA().getLastName());
                if (playerA == null)
                    throw new BadRequestException("Player A does not exist");
                Player playerB = players.playerRepository.getByName(reg.getPlayerB().getFirstName(), reg.getPlayerB().getLastName());
                if (playerB == null)
                    throw new BadRequestException("Player B does not exist");

                if (conditionsFailA(competition, playerA))
                    throw new BadRequestException("Player A is does not meet the conditions");
                if (conditionsFailB(competition, playerB))
                    throw new BadRequestException("Player B is does not meet the conditions");

                List<Team> regTeams = competition.getTeams();
                if (regTeams != null && regTeams.stream()
                        .anyMatch(t -> (t.getPlayerA() != null && t.getPlayerA().getId().equals(playerA.getId()))
                                || (t.getPlayerB() != null && t.getPlayerB().getId().equals(playerA.getId()))))
                    throw new WebApplicationException("Player already registered", Response.Status.CONFLICT);
                if (regTeams != null && regTeams.stream()
                        .anyMatch(t -> (t.getPlayerA() != null && t.getPlayerA().getId().equals(playerB.getId()))
                                || (t.getPlayerB() != null && t.getPlayerB().getId().equals(playerB.getId()))))
                    throw new WebApplicationException("Player already registered", Response.Status.CONFLICT);

                Team team = new Team();
                team.setPlayerA(playerA);
                team.setPlayerB(playerB);
                team.setCompetition(competition);
                teams.persist(team);
            }
        }

        // TODO notify by mail?
        return "Player registered";
    }

    private boolean conditionsFailA(Competition comp, Player player) {
        return (comp.getPlayerASex() == Sex.FEMALE && player.getSex() == SexType.MALE)
                || (comp.getPlayerASex() == Sex.MALE && player.getSex() == SexType.FEMALE)
                || (comp.playerAhasMinAge() && comp.getPlayerAminAge().isBefore(player.getBirthday()))
                || (comp.playerAhasMaxAge() && comp.getPlayerAminAge().isAfter(player.getBirthday()));
    }

    private boolean conditionsFailB(Competition comp, Player player) {
        return (comp.getPlayerBSex() == Sex.FEMALE && player.getSex() == SexType.MALE)
                || (comp.getPlayerBSex() == Sex.MALE && player.getSex() == SexType.FEMALE)
                || (comp.playerBhasMinAge() && comp.getPlayerBminAge().isBefore(player.getBirthday()))
                || (comp.playerBhasMaxAge() && comp.getPlayerBminAge().isAfter(player.getBirthday()));
    }

    @GET
    @Path("/{compName}/knockoutMatches")
    @Produces(MediaType.APPLICATION_JSON)
    public jUserKnockoutSystem getKnockoutMatches(@PathParam("tourName") String tourName, @PathParam("compName") String compName) {
        checkTournamentAccessibility(tourName);

        Competition competition = competitions.getByName(tourName, compName);
        if (competition == null)
            throw new NotFoundException("Could not found competition");
        if (competition.getType() != CompetitionType.KNOCKOUT)
            throw new WebApplicationException("Competition does not have type knockout",
                    Response.Status.METHOD_NOT_ALLOWED);

        Match finale = matches.getFinal(competition);
        Match thirdPlace = matches.getThirdPlace(competition);
        return new jUserKnockoutSystem(finale, thirdPlace);
    }

    @GET
    @Path("/{compName}/groupMatches")
    @Produces(MediaType.APPLICATION_JSON)
    public jUserGroupSystem getGroupMatches(@PathParam("tourName") String tourName, @PathParam("compName") String compName) {
        checkTournamentAccessibility(tourName);

        Competition competition = competitions.getByName(tourName, compName);
        if (competition == null)
            throw new NotFoundException("Competition as not found");
        if (competition.getType() != CompetitionType.GROUPS)
            throw new WebApplicationException("Competition does not have type groups",
                    Response.Status.METHOD_NOT_ALLOWED);

        List<Group> groups = competition.getGroups();
        if (groups == null)
            throw new NotFoundException("Found no groups");

        Match finale = matches.getFinal(competition);
        Match thirdPlace = matches.getThirdPlace(competition);
        if (finale == null || thirdPlace == null)
            throw new InternalServerErrorException("Finale or thirdPlace is null");
        return new jUserGroupSystem(groups, finale, thirdPlace);
    }

    private void checkTournamentAccessibility(String tourName) {
        Tournament tournament = tournaments.getByName(tourName);
        if (tournament == null)
            throw new NotFoundException("Tournament could not be found");
        if (!securityIdentity.hasRole("director") && !tournament.isVisible())
            throw new UnauthorizedException("Cannot access tournament");
    }
}
