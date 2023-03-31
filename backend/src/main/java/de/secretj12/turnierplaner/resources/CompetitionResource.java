package de.secretj12.turnierplaner.resources;

import de.secretj12.turnierplaner.db.entities.Match;
import de.secretj12.turnierplaner.db.entities.Player;
import de.secretj12.turnierplaner.db.entities.SexType;
import de.secretj12.turnierplaner.db.entities.Tournament;
import de.secretj12.turnierplaner.db.entities.competition.*;
import de.secretj12.turnierplaner.db.entities.groups.Group;
import de.secretj12.turnierplaner.db.repositories.CompetitionRepository;
import de.secretj12.turnierplaner.db.repositories.MatchRepository;
import de.secretj12.turnierplaner.db.repositories.TournamentRepository;
import de.secretj12.turnierplaner.resources.jsonEntities.director.competition.jDirectorCompetitionAdd;
import de.secretj12.turnierplaner.resources.jsonEntities.director.competition.jDirectorCompetitionUpdate;
import de.secretj12.turnierplaner.resources.jsonEntities.user.competition.jUserCompetition;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserGroupSystem;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserKnockoutSystem;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserPlayerSignUpForm;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserTeam;
import io.quarkus.security.identity.SecurityIdentity;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;

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
    public RestResponse<jUserCompetition> getCompetition(@PathParam("tourName") String tourName,
                                           @PathParam("compName") String compName) {
        if (securityIdentity.hasRole("director"))
            return ResponseBuilder
                    .ok((jUserCompetition) new jDirectorCompetitionUpdate(competitions.getByName(tourName, compName)))
                    .build();
        if (canSee(tourName))
            return ResponseBuilder
                    .ok(new jUserCompetition(competitions.getByName(tourName, compName)))
                    .build();
        return null;
    }

    @GET
    @Path("/canEdit")
    @Produces(MediaType.TEXT_PLAIN)
    public RestResponse<Boolean> canEdit(@PathParam("tourName") String tourName) {
        return ResponseBuilder.ok(securityIdentity.hasRole("director")).build();
    }

    @POST
    @Path("/add")
    @RolesAllowed("director")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public RestResponse<String> addCompetition(@PathParam("tourName") String tourName, jDirectorCompetitionAdd competition) {
        if (competition.getName() == null)
            return ResponseBuilder.create(RestResponse.Status.BAD_REQUEST, "No competition specified")
                    .build();

        if (competitions.getByName(tourName, competition.getName()) != null)
            return ResponseBuilder.create(RestResponse.Status.CONFLICT, "Competition already exists").build();

        Tournament tournament = tournaments.getByName(tourName);
        if (tournament == null)
            return ResponseBuilder.create(RestResponse.Status.BAD_REQUEST, "Tournament doesn't exist").build();

        if (competition.getPlayerA().isHasMinAge() && competition.getPlayerA().getMinAge() == null)
            return ResponseBuilder
                    .create(RestResponse.Status.BAD_REQUEST, "Player A: Min age null although has min age")
                    .build();
        if (competition.getPlayerA().isHasMaxAge() && competition.getPlayerA().getMaxAge() == null)
            return ResponseBuilder
                    .create(RestResponse.Status.BAD_REQUEST, "Player A: Max age null although has max age")
                    .build();
        if (competition.getPlayerB().isHasMinAge() && competition.getPlayerB().getMinAge() == null)
            return ResponseBuilder
                    .create(RestResponse.Status.BAD_REQUEST, "Player B: Min age null although has min age")
                    .build();
        if (competition.getPlayerB().isHasMaxAge() && competition.getPlayerB().getMaxAge() == null)
            return ResponseBuilder
                    .create(RestResponse.Status.BAD_REQUEST, "Player B: Max age null although has max age")
                    .build();

        Competition dbCompetition = new Competition();
        competition.toDB(dbCompetition);
        dbCompetition.setTournament(tournament);
        competitions.persist(dbCompetition);
        return ResponseBuilder.ok("successfully added").build();
    }

    @POST
    @Path("/update")
    @RolesAllowed("director")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public RestResponse<String> updateCompetition(@PathParam("tourName") String tourName, jDirectorCompetitionUpdate competition) {
        Competition dbCompetition = competitions.getById(competition.getId());
        if (dbCompetition == null)
            return ResponseBuilder.create(RestResponse.Status.BAD_REQUEST, "Competition doesn't exist").build();
        if (!dbCompetition.getTournament().getName().equals(tourName))
            return ResponseBuilder
                    .create(RestResponse.Status.BAD_REQUEST, "Tournament of competition is not the given")
                    .build();

        competition.toDB(dbCompetition);
        competitions.persist(dbCompetition);

        // TODO update players (remove all not fitting, warn in frontend if conditions are changed!)

        return ResponseBuilder.ok("successfully changed").build();
    }

    @GET
    @Path("/{compName}/signedUpPlayers")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<List<jUserTeam>> getSignedUpPlayers(@PathParam("tourName") String tourName,
                                       @PathParam("compName") String compName) {
        if (!canSee(tourName))
            return RestResponse.status(RestResponse.Status.UNAUTHORIZED);

        Competition competition = competitions.getByName(tourName, compName);
        if (competition == null)
            return null;

        if (!securityIdentity.hasRole("director") &&
                (competition.getTournament().getBeginRegistration().isAfter(LocalDateTime.now())
                        || competition.getTournament().getBeginGamePhase().isBefore(LocalDateTime.now())))
            return RestResponse.status(RestResponse.Status.UNAUTHORIZED);

        return ResponseBuilder.ok(competition.getTeams().stream().map(jUserTeam::new).toList()).build();
    }

    @POST
    @Path("/{compName}/signUp")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public RestResponse<String> signUpPlayer(@PathParam("tourName") String tourName, @PathParam("compName") String compName,
                                 jUserPlayerSignUpForm reg) {

        if (!canSee(tourName))
            return RestResponse.status(RestResponse.Status.UNAUTHORIZED);

        Competition competition = competitions.getByName(tourName, compName);
        if (competition == null)
            return ResponseBuilder.create(RestResponse.Status.BAD_REQUEST, "Competition doesn't exist").build();

        if (!securityIdentity.hasRole("director") && // or registration phase
                (competition.getTournament().getBeginRegistration().isAfter(LocalDateTime.now())
                        || competition.getTournament().getEndRegistration().isBefore(LocalDateTime.now())))
            return RestResponse.status(RestResponse.Status.UNAUTHORIZED);

        if (competition.getMode() == CompetitionMode.SINGLES
                || (competition.getSignup() == CompetitionSignUp.INDIVIDUAL && !competition.isPlayerBdifferent())) {
            // single mode or double with individual registration but same constraints
            // -> every registration as player A and player B is null

            if (reg.getPlayerA() == null)
                return ResponseBuilder.create(RestResponse.Status.BAD_REQUEST, "Player A is null").build();
            if (reg.getPlayerB() != null)
                return ResponseBuilder.create(RestResponse.Status.BAD_REQUEST, "Player B is not null").build();

            Player playerA = players.playerRepository
                    .getByName(reg.getPlayerA().getFirstName(), reg.getPlayerA().getLastName());
            if (playerA == null)
                return ResponseBuilder.create(RestResponse.Status.BAD_REQUEST, "Player A doesn't exist").build();

            if (conditionsFailA(competition, playerA))
                return ResponseBuilder
                        .create(RestResponse.Status.BAD_REQUEST, "Player A is does not meet the conditions")
                        .build();

            List<Team> regTeams = competition.getTeams();
            if (regTeams == null) {
                Team team = new Team();
                team.setPlayerA(playerA);
                competition.setTeams(List.of(team));
            } else if (regTeams.stream().anyMatch(t -> t.getPlayerA().getId().equals(playerA.getId()))) {
                return ResponseBuilder.create(RestResponse.Status.CONFLICT, "Player already registered").build();
            } else {
                Team team = new Team();
                team.setPlayerA(playerA);
                regTeams.add(team);
            }
            competitions.persist(competition);
        } else {
            // double mode
            if (competition.getSignup() == CompetitionSignUp.INDIVIDUAL && competition.isPlayerBdifferent()) {
                // double mode with individual registration and different constraints
                // -> each registration needs to be player A xor player B null

                if (reg.getPlayerA() == null && reg.getPlayerB() == null)
                    return ResponseBuilder
                            .create(RestResponse.Status.BAD_REQUEST, "Player A and player B are null")
                            .build();
                if (reg.getPlayerA() != null && reg.getPlayerB() != null)
                    return ResponseBuilder
                            .create(RestResponse.Status.BAD_REQUEST, "Player A and player B are not null")
                            .build();

                if (reg.getPlayerA() != null) {
                    Player playerA = players.playerRepository.getByName(reg.getPlayerA().getFirstName(), reg.getPlayerA().getLastName());
                    if (playerA == null)
                        return ResponseBuilder
                                .create(RestResponse.Status.BAD_REQUEST, "Player A doesn't exist")
                                .build();

                    if (conditionsFailA(competition, playerA))
                        return ResponseBuilder
                                .create(RestResponse.Status.BAD_REQUEST, "Player A is does not meet the conditions")
                                .build();

                    List<Team> regTeams = competition.getTeams();
                    if (regTeams == null) {
                        Team team = new Team();
                        team.setPlayerA(playerA);
                        competition.setTeams(List.of(team));
                    } else if (regTeams.stream().anyMatch(t ->
                            t.getPlayerA().getId().equals(playerA.getId())
                                    || t.getPlayerB().getId().equals(playerA.getId()))
                    ) {
                        return ResponseBuilder
                                .create(RestResponse.Status.CONFLICT, "Player already registered").build();
                    } else {
                        Team team = new Team();
                        team.setPlayerA(playerA);
                        regTeams.add(team);
                    }
                    competitions.persist(competition);
                }
                if (reg.getPlayerB() != null) {
                    Player playerB = players.playerRepository.getByName(reg.getPlayerB().getFirstName(), reg.getPlayerB().getLastName());
                    if (playerB == null)
                        return ResponseBuilder
                                .create(RestResponse.Status.BAD_REQUEST, "Player B doesn't exist")
                                .build();

                    if (conditionsFailB(competition, playerB))
                        return ResponseBuilder
                                .create(RestResponse.Status.BAD_REQUEST,
                                        "Player B is does not meet the conditions")
                                .build();

                    List<Team> regTeams = competition.getTeams();
                    if (regTeams == null) {
                        Team team = new Team();
                        team.setPlayerB(playerB);
                        competition.setTeams(List.of(team));
                    } else if (regTeams.stream().anyMatch(t ->
                            t.getPlayerA().getId().equals(playerB.getId())
                                    || t.getPlayerB().getId().equals(playerB.getId()))
                    ) {
                        return ResponseBuilder
                                .create(RestResponse.Status.CONFLICT, "Player already registered").build();
                    } else {
                        Team team = new Team();
                        team.setPlayerB(playerB);
                        regTeams.add(team);
                    }
                    competitions.persist(competition);
                }
            } else {
                // double mode with registration together and any constraints
                // -> needs player A and player B to be not null

                if (reg.getPlayerA() == null)
                    return ResponseBuilder.create(RestResponse.Status.BAD_REQUEST, "Player A is null").build();
                if (reg.getPlayerB() == null)
                    return ResponseBuilder.create(RestResponse.Status.BAD_REQUEST, "Player B is null").build();

                Player playerA = players.playerRepository.getByName(reg.getPlayerA().getFirstName(), reg.getPlayerA().getLastName());
                if (playerA == null)
                    return ResponseBuilder
                            .create(RestResponse.Status.BAD_REQUEST, "Player A doesn't exist").build();
                Player playerB = players.playerRepository.getByName(reg.getPlayerB().getFirstName(), reg.getPlayerB().getLastName());
                if (playerB == null)
                    return ResponseBuilder
                            .create(RestResponse.Status.BAD_REQUEST, "Player B doesn't exist").build();

                if (conditionsFailA(competition, playerA))
                    return ResponseBuilder
                            .create(RestResponse.Status.BAD_REQUEST, "Player A is does not meet the conditions")
                            .build();
                if (conditionsFailB(competition, playerB))
                    return ResponseBuilder
                            .create(RestResponse.Status.BAD_REQUEST, "Player B is does not meet the conditions")
                            .build();

                List<Team> regTeams = competition.getTeams();
                if (regTeams == null) {
                    Team team = new Team();
                    team.setPlayerA(playerA);
                    team.setPlayerB(playerB);
                    competition.setTeams(List.of(team));
                } else if (regTeams.stream().anyMatch(t ->
                        t.getPlayerA().getId().equals(playerA.getId())
                                || t.getPlayerB().getId().equals(playerA.getId()))
                ) {
                    return ResponseBuilder
                            .create(RestResponse.Status.CONFLICT, "Player A already registered")
                            .build();
                } else if (regTeams.stream().anyMatch(t ->
                        t.getPlayerA().getId().equals(playerB.getId())
                                || t.getPlayerB().getId().equals(playerB.getId()))
                ) {
                    return ResponseBuilder
                            .create(RestResponse.Status.CONFLICT, "Player B already registered")
                            .build();
                } else {
                    Team team = new Team();
                    team.setPlayerA(playerA);
                    team.setPlayerB(playerB);
                    regTeams.add(team);
                }
                competitions.persist(competition);
            }
        }

        // TODO notify by mail?
        return ResponseBuilder.ok("Player registered").build();
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
    public RestResponse<jUserKnockoutSystem> getKnockoutMatches(@PathParam("tourName") String tourName, @PathParam("compName") String compName) {
        if (!canSee(tourName))
            return RestResponse.status(Response.Status.UNAUTHORIZED);

        Competition competition = competitions.getByName(tourName, compName);
        if (competition == null)
            return RestResponse.status(Response.Status.NOT_FOUND);
        if (competition.getType() != CompetitionType.KNOCKOUT)
            return RestResponse.status(Response.Status.METHOD_NOT_ALLOWED);

        Match finale = matches.getFinal(competition);
        Match thirdPlace = matches.getThirdPlace(competition);
        return ResponseBuilder.ok(new jUserKnockoutSystem(finale, thirdPlace)).build();
    }

    @GET
    @Path("/{compName}/groupMatches")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<jUserGroupSystem> getGroupMatches(@PathParam("tourName") String tourName, @PathParam("compName") String compName) {
        if (!canSee(tourName))
            return RestResponse.status(Response.Status.UNAUTHORIZED);

        Competition competition = competitions.getByName(tourName, compName);
        if (competition == null)
            return RestResponse.status(Response.Status.NOT_FOUND);
        if (competition.getType() != CompetitionType.GROUPS)
            return RestResponse.status(Response.Status.METHOD_NOT_ALLOWED);

        List<Group> groups = competition.getGroups();
        if (groups == null)
            return RestResponse.status(Response.Status.NOT_FOUND);

        return ResponseBuilder.ok(new jUserGroupSystem(groups)).build();
    }

    private boolean canSee(String tourName) {
        return securityIdentity.hasRole("director") || tournaments.getByName(tourName).isVisible();
    }
}
