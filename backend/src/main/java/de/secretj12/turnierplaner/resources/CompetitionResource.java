package de.secretj12.turnierplaner.resources;

import de.secretj12.turnierplaner.db.entities.Match;
import de.secretj12.turnierplaner.db.entities.Player;
import de.secretj12.turnierplaner.db.entities.SexType;
import de.secretj12.turnierplaner.db.entities.Tournament;
import de.secretj12.turnierplaner.db.entities.competition.*;
import de.secretj12.turnierplaner.db.entities.groups.Group;
import de.secretj12.turnierplaner.db.entities.knockout.NextMatch;
import de.secretj12.turnierplaner.db.repositories.*;
import de.secretj12.turnierplaner.resources.jsonEntities.director.competition.jDirectorCompetitionAdd;
import de.secretj12.turnierplaner.resources.jsonEntities.director.competition.jDirectorCompetitionUpdate;
import de.secretj12.turnierplaner.resources.jsonEntities.director.competition.jDirectorInitKnockout;
import de.secretj12.turnierplaner.resources.jsonEntities.user.competition.jUserCompetition;
import de.secretj12.turnierplaner.resources.jsonEntities.user.group.jUserGroupSystem;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserPlayer;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserPlayerRegistrationForm;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserPlayerSignUpForm;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserTeam;
import de.secretj12.turnierplaner.resources.jsonEntities.user.knockout.jUserKnockoutSystem;
import io.quarkus.security.UnauthorizedException;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.common.annotation.Blocking;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Path("/tournament/{tourName}/competition")
public class CompetitionResource {
    // TODO always check for current progress

    @Inject
    PlayerResource playersResource;
    @Inject
    PlayerRepository players;
    @Inject
    CompetitionRepository competitions;
    @Inject
    TournamentRepository tournaments;
    @Inject
    SecurityIdentity securityIdentity;
    @Inject
    MatchRepository matches;
    @Inject
    NextMatchRepository nextMatches;
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
    @Path("/prepare")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<jDirectorCompetitionUpdate> getPrepareCompetitions(@PathParam("tourName") String tourName) {
        if (securityIdentity.hasRole("director"))
            return competitions.listByName(tourName).stream().map(jDirectorCompetitionUpdate::new).toList();
        throw new UnauthorizedException("Not authorized");
    }

    @GET
    @Path("/{compName}/details")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public jUserCompetition getCompetition(@PathParam("tourName") String tourName, @PathParam(
        "compName") String compName) {
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
        if (competition.getName() == null) throw new BadRequestException("No competition specified");

        if (competitions.getByName(tourName, competition.getName()) != null)
            throw new BadRequestException("Competition already exists");

        Tournament tournament = tournaments.getByName(tourName);
        if (tournament == null) throw new BadRequestException("Tournament doesn't exist");

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
        dbCompetition.setcProgress(CreationProgress.PLAYER);
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
        if (dbCompetition == null) throw new BadRequestException("Competition doesn't exist");
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
    public List<jUserTeam> getSignedUpPlayers(@PathParam("tourName") String tourName, @PathParam(
        "compName") String compName) {
        checkTournamentAccessibility(tourName);

        Competition competition = competitions.getByName(tourName, compName);
        if (competition == null) throw new NotFoundException("Competition was not found");

        if (!securityIdentity.hasRole("director") && (competition.getTournament().getBeginRegistration().isAfter(LocalDateTime.now()) || competition.getTournament().getBeginGamePhase().isBefore(LocalDateTime.now())))
            throw new NotAuthorizedException("Registration phase is not active");

        return competition.getTeams().stream().map(jUserTeam::new).toList();
    }

    @POST
    @Path("/{compName}/signUp")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String signUpPlayer(@PathParam("tourName") String tourName, @PathParam(
        "compName") String compName, jUserPlayerSignUpForm reg) {
        // TODO better checks if team or members of it are already registered in team
        // TODO check if both layers are the same
        // TODO check if players match conditions
        checkTournamentAccessibility(tourName);

        Competition competition = competitions.getByName(tourName, compName);
        if (competition == null) throw new BadRequestException("Competition doesn't exist");

        if (!securityIdentity.hasRole("director") && // or registration phase
                (competition.getTournament().getBeginRegistration().isAfter(LocalDateTime.now()) || competition.getTournament().getEndRegistration().isBefore(LocalDateTime.now())))
            throw new NotAuthorizedException("Registration phase is not active");

        if (competition.getMode() == CompetitionMode.SINGLES || (competition.getSignup() == CompetitionSignUp.INDIVIDUAL && !competition.isPlayerBdifferent())) {
            // single mode or double with individual registration but same constraints
            // -> every registration as player A and player B is null

            if (reg.getPlayerA() == null) throw new BadRequestException("Player A is null");
            if (reg.getPlayerB() != null) throw new BadRequestException("Player B is not null");

            Player playerA = players.getByName(reg.getPlayerA().getFirstName(), reg.getPlayerA().getLastName());
            if (playerA == null) throw new BadRequestException("Player A does not exist");

            if (conditionsFailA(competition, playerA))
                throw new BadRequestException("Player A does not meed the conditions");

            List<Team> regTeams = competition.getTeams();
            if (regTeams != null && regTeams.stream().anyMatch(t -> (t.getPlayerA() != null && t.getPlayerA().getId().equals(playerA.getId())) || (t.getPlayerB() != null && t.getPlayerB().getId().equals(playerA.getId()))))
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
                    Player playerA = players.getByName(reg.getPlayerA().getFirstName(), reg.getPlayerA().getLastName());
                    if (playerA == null) throw new BadRequestException("Player A doesn't exist");

                    if (conditionsFailA(competition, playerA))
                        throw new BadRequestException("Player A is does not meet the conditions");

                    List<Team> regTeams = competition.getTeams();
                    if (regTeams != null && regTeams.stream().anyMatch(t -> (t.getPlayerA() != null && t.getPlayerA().getId().equals(playerA.getId())) || (t.getPlayerB() != null && t.getPlayerB().getId().equals(playerA.getId()))))
                        throw new WebApplicationException("Player already registered", Response.Status.CONFLICT);

                    Team team = new Team();
                    team.setPlayerA(playerA);
                    team.setCompetition(competition);
                    teams.persist(team);
                }
                if (reg.getPlayerB() != null) {
                    Player playerB = players.getByName(reg.getPlayerB().getFirstName(), reg.getPlayerB().getLastName());
                    if (playerB == null) throw new BadRequestException("Player B does not exist");

                    if (conditionsFailB(competition, playerB))
                        throw new BadRequestException("Player B is does not meet the conditions");

                    List<Team> regTeams = competition.getTeams();
                    if (regTeams != null && regTeams.stream().anyMatch(t -> (t.getPlayerA() != null && t.getPlayerA().getId().equals(playerB.getId())) || (t.getPlayerB() != null && t.getPlayerB().getId().equals(playerB.getId()))))
                        throw new WebApplicationException("Player already registered", Response.Status.CONFLICT);

                    Team team = new Team();
                    team.setPlayerB(playerB);
                    team.setCompetition(competition);
                    teams.persist(team);
                }
            } else {
                // double mode with registration together and any constraints
                // -> needs player A and player B to be not null

                if (reg.getPlayerA() == null) throw new BadRequestException("Player A is null");
                if (reg.getPlayerB() == null) throw new BadRequestException("Player B is null");

                Player playerA = players.getByName(reg.getPlayerA().getFirstName(), reg.getPlayerA().getLastName());
                if (playerA == null) throw new BadRequestException("Player A does not exist");
                Player playerB = players.getByName(reg.getPlayerB().getFirstName(), reg.getPlayerB().getLastName());
                if (playerB == null) throw new BadRequestException("Player B does not exist");

                if (conditionsFailA(competition, playerA))
                    throw new BadRequestException("Player A is does not meet the conditions");
                if (conditionsFailB(competition, playerB))
                    throw new BadRequestException("Player B is does not meet the conditions");

                List<Team> regTeams = competition.getTeams();
                if (regTeams != null && regTeams.stream().anyMatch(t -> (t.getPlayerA() != null && t.getPlayerA().getId().equals(playerA.getId())) || (t.getPlayerB() != null && t.getPlayerB().getId().equals(playerA.getId()))))
                    throw new WebApplicationException("Player already registered", Response.Status.CONFLICT);
                if (regTeams != null && regTeams.stream().anyMatch(t -> (t.getPlayerA() != null && t.getPlayerA().getId().equals(playerB.getId())) || (t.getPlayerB() != null && t.getPlayerB().getId().equals(playerB.getId()))))
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

    @POST
    @RolesAllowed("director")
    @Path("/{compName}/updateTeams")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<jUserTeam> updateTeams(@PathParam("tourName") String tourName, @PathParam(
        "compName") String compName, List<jUserTeam> teams) {
        updateTeamsHelper(tourName, compName, teams);

        return competitions.getByName(tourName, compName).getTeams().stream().map(jUserTeam::new).toList();
    }

    @Transactional
    protected void updateTeamsHelper(String tourName, String compName, List<jUserTeam> teams) {
        checkTournamentAccessibility(tourName);
        Competition competition = competitions.getByName(tourName, compName);
        if (competition == null) throw new BadRequestException("Competition doesn't exist");

        List<Team> curTeams = competition.getTeams();

        teams.forEach(team -> {
            Player playerA = team.getPlayerA() == null ? null : players.getByName(team.getPlayerA().getFirstName(), team.getPlayerA().getLastName());
            Player playerB = team.getPlayerB() == null ? null : players.getByName(team.getPlayerB().getFirstName(), team.getPlayerB().getLastName());

            var cTeamOp = curTeams.stream().filter(cT -> cT.getId().equals(team.getId())).findAny();
            if (cTeamOp.isEmpty()) {
                Team t = new Team();
                t.setPlayerA(playerA);
                t.setPlayerB(playerB);
                this.teams.persist(t);
            } else {
                var cTeam = cTeamOp.get();
                cTeam.setPlayerA(playerA);
                cTeam.setPlayerB(playerB);
                this.teams.persist(cTeam);
            }
        });
        curTeams.forEach(cTeam -> {
            var teamOp = teams.stream().filter(cT -> cT.getId().equals(cTeam.getId())).findAny();
            if (teamOp.isEmpty()) {
                this.teams.delete(cTeam);
            }
        });
    }

    @POST
    @Transactional
    @Path("/{compName}/signUpRegister/{playerSide}")
    @Blocking
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public jUserPlayer registerSignUpPlayer(@PathParam("tourName") String tourName, @PathParam(
        "compName") String compName, @PathParam("playerSide") String playerSide,
                                            jUserPlayerRegistrationForm playerForm) {
        Player newPlayer = playersResource.adminPlayerRegistration(playerForm);

        Competition competition = competitions.getByName(tourName, compName);

        Team team = new Team();
        if (playerSide.equals("playerA")) {
            team.setPlayerA(newPlayer);
        } else {
            team.setPlayerB(newPlayer);
        }
        team.setCompetition(competition);
        teams.persist(team);

        return new jUserPlayer(newPlayer);
    }

    private boolean conditionsFailA(Competition comp, Player player) {
        return (comp.getPlayerASex() == Sex.FEMALE && player.getSex() == SexType.MALE) || (comp.getPlayerASex() == Sex.MALE && player.getSex() == SexType.FEMALE) || (comp.playerAhasMinAge() && comp.getPlayerAminAge().isBefore(player.getBirthday())) || (comp.playerAhasMaxAge() && comp.getPlayerAminAge().isAfter(player.getBirthday()));
    }

    private boolean conditionsFailB(Competition comp, Player player) {
        return (comp.getPlayerBSex() == Sex.FEMALE && player.getSex() == SexType.MALE) || (comp.getPlayerBSex() == Sex.MALE && player.getSex() == SexType.FEMALE) || (comp.playerBhasMinAge() && comp.getPlayerBminAge().isBefore(player.getBirthday())) || (comp.playerBhasMaxAge() && comp.getPlayerBminAge().isAfter(player.getBirthday()));
    }

    @GET
    @Path("/{compName}/knockoutMatches")
    @Produces(MediaType.APPLICATION_JSON)
    public jUserKnockoutSystem getKnockoutMatches(@PathParam("tourName") String tourName, @PathParam(
        "compName") String compName) {
        checkTournamentAccessibility(tourName);

        Competition competition = competitions.getByName(tourName, compName);
        if (competition == null) throw new NotFoundException("Could not found competition");
        if (competition.getType() != CompetitionType.KNOCKOUT)
            throw new WebApplicationException("Competition does not have type knockout", Response.Status.METHOD_NOT_ALLOWED);

        Match finale = matches.getFinal(competition);
        Match thirdPlace = matches.getThirdPlace(competition);
        return new jUserKnockoutSystem(finale, thirdPlace);
    }

    @GET
    @Path("/{compName}/groupMatches")
    @Produces(MediaType.APPLICATION_JSON)
    public jUserGroupSystem getGroupMatches(@PathParam("tourName") String tourName, @PathParam(
        "compName") String compName) {
        checkTournamentAccessibility(tourName);

        Competition competition = competitions.getByName(tourName, compName);
        if (competition == null) throw new NotFoundException("Competition as not found");
        if (competition.getType() != CompetitionType.GROUPS)
            throw new NotAllowedException("Competition does not have type groups");

        List<Group> groups = competition.getGroups();
        if (groups == null) throw new NotFoundException("Found no groups");

        Match finale = matches.getFinal(competition);
        Match thirdPlace = matches.getThirdPlace(competition);
        if (finale == null || thirdPlace == null)
            throw new InternalServerErrorException("Finale or thirdPlace is null");
        return new jUserGroupSystem(groups, finale, thirdPlace);
    }

    private void checkTournamentAccessibility(String tourName) {
        Tournament tournament = tournaments.getByName(tourName);
        if (tournament == null) throw new NotFoundException("Tournament could not be found");
        if (!securityIdentity.hasRole("director") && !tournament.isVisible())
            throw new UnauthorizedException("Cannot access tournament");
    }

    @POST
    @Transactional
    @Path("/{compName}/initKnockout")
    @Produces(MediaType.TEXT_PLAIN)
    public boolean initializeMatchesKnockout(@PathParam("tourName") String tourName, @PathParam("compName") String compName, jDirectorInitKnockout init) {
        checkTournamentAccessibility(tourName);

        System.out.println(tourName);
        System.out.println(compName);

        List<Team> teamOrder = new ArrayList<>(init.getTeams().stream().map(t -> teams.getById(t.getId())).toList());
        if (teamOrder.stream().anyMatch(Objects::isNull))
            throw new NotFoundException("Player not found");

        int size = (int) Math.max(0, Math.ceil((Math.log(teamOrder.size())/Math.log(2)) - 1));

        int numAdd = (int) (Math.pow(2, size+1)-teamOrder.size());
        for (int i = 0; i < numAdd; i++) {
            teamOrder.add(null);
        }

        Competition competition = competitions.getByName(tourName, compName);

        generateTree(competition, size, teamOrder);

        competition.setcProgress(CreationProgress.GAMES);
        competitions.persist(competition);
        return true;
    }

    private Match generateTree(Competition competition, int size, List<Team> teams) {
        return generateTree(competition, true, size, teams, false);
    }
    private Match generateTree(Competition competition, boolean finale, int size, List<Team> teams, boolean reversed) {
        var splits = split(size, teams);

        Match match = new Match();
        if (size == 0) {
            match.setTeamA(teams.get(reversed ? 1 : 0));
            match.setTeamB(teams.get(reversed ? 0 : 1));
        }
        match.setCompetition(competition);
        matches.persist(match);

        if (size > 0) {
            Match matchA = generateTree(competition, false, size-1, reversed ? splits.y : splits.x, false);
            Match matchB = generateTree(competition, false, size-1, reversed ? splits.x : splits.y, true);

            NextMatch nMatch = new NextMatch();
            nMatch.setPreviousA(matchA);
            nMatch.setPreviousB(matchB);
            nMatch.setNextMatch(match);

            if (finale) {
                Match thirdPlace = new Match();
                thirdPlace.setCompetition(competition);
                matches.persist(thirdPlace);

                NextMatch nextMatchThird = new NextMatch();
                nextMatchThird.setPreviousA(matchA);
                nextMatchThird.setPreviousB(matchB);
                nextMatchThird.setNextMatch(thirdPlace);
                nextMatchThird.setWinner(false);
                nextMatches.persist(nextMatchThird);
            }

            nextMatches.persist(nMatch);
        }

        return match;
    }

    private Pair<List<Team>, List<Team>> split(int size, List<Team> teams) {
        if (teams.size() < 2) {
            throw new InternalServerErrorException("Wrong size of teams list");
        }
        if (size == 0) {
            List<Team> a = new ArrayList<>();
            a.add(teams.get(0));
            List<Team> b = new ArrayList<>();
            b.add(teams.get(1));
            return new Pair<>(a, b);
        }

        int count = (int) Math.pow(2, size+1);
        var fst = split(size-1, teams.subList(0, count/2));
        var snd = split(size-1, teams.subList(count/2, count).reversed());

        fst.x.addAll(snd.x.reversed());
        fst.y.addAll(snd.y.reversed());

        return new Pair<>(fst.x, fst.y);
    }

    public record Pair<X, Y>(X x, Y y) {
    }

}
