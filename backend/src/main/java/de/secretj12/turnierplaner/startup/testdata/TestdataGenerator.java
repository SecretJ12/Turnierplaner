package de.secretj12.turnierplaner.startup.testdata;

import de.secretj12.turnierplaner.db.entities.*;
import de.secretj12.turnierplaner.db.entities.competition.*;
import de.secretj12.turnierplaner.db.entities.groups.FinalOfGroup;
import de.secretj12.turnierplaner.db.entities.groups.Group;
import de.secretj12.turnierplaner.db.entities.groups.MatchOfGroup;
import de.secretj12.turnierplaner.db.entities.knockout.NextMatch;
import de.secretj12.turnierplaner.db.repositories.*;
import io.smallrye.mutiny.tuples.Tuple2;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class TestdataGenerator {
    @Inject
    TournamentRepository tournaments;
    @Inject
    CompetitionRepository competitions;
    @Inject
    PlayerRepository players;
    @Inject
    MatchRepository matchRepository;
    @Inject
    CourtRepositiory courtRepositiory;
    @Inject
    NextMatchRepository nextMatches;
    @Inject
    GroupRepository groupRepository;
    @Inject
    TeamRepository teamRepository;
    @Inject
    MatchOfGroupRepository matchOfGroupRepository;
    @Inject
    FinalOfGroupRepository finalOfGroupRepository;
    @Inject
    SetRepository sets;

    enum TDate {
        BEFORE_REGISTRATION, REGISTRATION_OPEN, BEFORE_GAMEPHASE, GAMEPHASE_OPEN, AFTER_GAMEPHASE
    }

    private final Random random = new Random();
    private final Faker faker = new Faker();

    private final static int NUMBEROFCOURTS = 4;
    private Court[] courts = new Court[4];

    @Transactional
    public void generateData() {
        courts = new Court[NUMBEROFCOURTS];
        for (int i = 0; i < NUMBEROFCOURTS; i++) {
            courts[i] = new Court();
            courts[i].setName("Platz " + (i + 1));
            courtRepositiory.persist(courts[i]);
        }

        List<CompetitionSettings> compSetting = new ArrayList<>();
        // @formatter:off
        compSetting.add(new CompetitionSettings("Single Groups", CompetitionType.GROUPS, CompetitionMode.SINGLES, Sex.MALE, 8, false, AGE_RESTR.NONE, false, 2));
        compSetting.add(new CompetitionSettings("Single U18", CompetitionType.GROUPS, CompetitionMode.SINGLES, Sex.MALE, 8, false, AGE_RESTR.U18, false, 2));
        compSetting.add(new CompetitionSettings("Single Knockout", CompetitionType.KNOCKOUT, CompetitionMode.SINGLES, Sex.MALE, 16, false, AGE_RESTR.NONE, false, 2));
        compSetting.add(new CompetitionSettings("Double", CompetitionType.KNOCKOUT, CompetitionMode.DOUBLES, Sex.MALE, 16, false, AGE_RESTR.NONE, false, 2));
        compSetting.add(new CompetitionSettings("Double random", CompetitionType.KNOCKOUT, CompetitionMode.DOUBLES, Sex.ANY, 32, true, AGE_RESTR.NONE, false, 2));
        compSetting.add(new CompetitionSettings("Double O50", CompetitionType.KNOCKOUT, CompetitionMode.DOUBLES, Sex.ANY, 8, false, AGE_RESTR.O50, true, 2));
        compSetting.add(new CompetitionSettings("Double Mixed", CompetitionType.KNOCKOUT, CompetitionMode.DOUBLES, Sex.ANY, 4, true, AGE_RESTR.O50, true, 2));
        // @formatter:on

        createTournament(TDate.BEFORE_REGISTRATION, "Clubmeisterschaft 2026", "Anmeldung ausstehend", true,
            compSetting);
        createTournament(TDate.REGISTRATION_OPEN, "Clubmeisterschaft 2025", "Anmeldung offen", true, compSetting);
        createTournament(TDate.BEFORE_GAMEPHASE, "Clubmeisterschaft 2024", "Anmeldung vorbei", true, compSetting);
        createTournament(TDate.GAMEPHASE_OPEN, "Clubmeisterschaft 2023", "Spielphase offen", true, compSetting);
        createTournament(TDate.AFTER_GAMEPHASE, "Clubmeisterschaft 2022", "Spielphase vorbei", true, compSetting);
        createTournament(TDate.BEFORE_REGISTRATION, "Clubmeisterschaft 2027", "Noch in der Planung", false,
            compSetting);
    }

    private void createTournament(TDate tDate, String name, String description, boolean visible,
                                  List<CompetitionSettings> compSettings) {
        Tournament tournament = new Tournament();
        tournament.setName(name);
        tournament.setVisible(visible);
        switch (tDate) {
            case BEFORE_REGISTRATION -> {
                tournament.setBeginRegistration(LocalDateTime.now().plusDays(10));
                tournament.setEndRegistration(LocalDateTime.now().plusDays(11));
                tournament.setBeginGamePhase(LocalDateTime.now().plusDays(12));
                tournament.setEndGamePhase(LocalDateTime.now().plusDays(13));
            }
            case REGISTRATION_OPEN -> {
                tournament.setBeginRegistration(LocalDateTime.now().minusDays(1));
                tournament.setEndRegistration(LocalDateTime.now().plusDays(10));
                tournament.setBeginGamePhase(LocalDateTime.now().plusDays(11));
                tournament.setEndGamePhase(LocalDateTime.now().plusDays(12));
            }
            case BEFORE_GAMEPHASE -> {
                tournament.setBeginRegistration(LocalDateTime.now().minusDays(2));
                tournament.setEndRegistration(LocalDateTime.now().minusDays(1));
                tournament.setBeginGamePhase(LocalDateTime.now().plusDays(10));
                tournament.setEndGamePhase(LocalDateTime.now().plusDays(11));
            }
            case GAMEPHASE_OPEN -> {
                tournament.setBeginRegistration(LocalDateTime.now().minusDays(3));
                tournament.setEndRegistration(LocalDateTime.now().minusDays(2));
                tournament.setBeginGamePhase(LocalDateTime.now().minusDays(1));
                tournament.setEndGamePhase(LocalDateTime.now().plusDays(10));
            }
            case AFTER_GAMEPHASE -> {
                tournament.setBeginRegistration(LocalDateTime.now().minusDays(4));
                tournament.setEndRegistration(LocalDateTime.now().minusDays(3));
                tournament.setBeginGamePhase(LocalDateTime.now().minusDays(2));
                tournament.setEndGamePhase(LocalDateTime.now().minusDays(1));
            }
        }
        tournament.setDescription(description);
        tournaments.persist(tournament);

        addCompetition(tournament, tDate, compSettings);
    }


    private final static int winBias = 3;

    private void createSets(Match match) {
        ArrayList<Set> setArrayList = new ArrayList<>();
        int numberOfSets = 2;
        int winDif = 0;
        List<Tuple2<Integer, Integer>> possibleResults = List.of(Tuple2.of(6, 0), Tuple2.of(6, 1), Tuple2.of(6, 2),
            Tuple2.of(6, 3), Tuple2.of(6, 4), Tuple2.of(6, 5), Tuple2.of(7, 5), Tuple2.of(7, 6));
        for (int i = 0; i < numberOfSets; i++) {
            Set.SetKey setKey = new Set.SetKey();
            setKey.setMatch(match);
            setKey.setIndex(i);
            Set set = new Set();
            set.setKey(setKey);

            int r = random.nextInt(possibleResults.size());
            switch (random.nextInt(Math.abs(winDif) + 2)) {
                case 0 -> {
                    set.setScoreA(possibleResults.get(r).getItem1());
                    set.setScoreB(possibleResults.get(r).getItem2());
                    winDif += winBias;
                }
                case 1 -> {
                    set.setScoreA(possibleResults.get(r).getItem1());
                    set.setScoreB(possibleResults.get(r).getItem2());
                    winDif -= winBias;
                }
                default -> {
                    if (winDif > 0) {
                        set.setScoreA(possibleResults.get(r).getItem1());
                        set.setScoreB(possibleResults.get(r).getItem2());
                        winDif += winBias;
                    } else {
                        set.setScoreA(possibleResults.get(r).getItem2());
                        set.setScoreB(possibleResults.get(r).getItem1());
                        winDif -= winBias;
                    }
                }
            }

            setArrayList.add(set);
            sets.persist(set);
        }
        match.setWinner(winDif < 0);
        if (winDif == 0) {
            Set.SetKey setKey = new Set.SetKey();
            setKey.setMatch(match);
            setKey.setIndex(numberOfSets);
            Set set = new Set();
            set.setKey(setKey);

            int winner;
            int looser;
            if (random.nextInt(10) < 9) {
                winner = 10;
                looser = random.nextInt(9);
            } else {
                winner = random.nextInt(11, 20);
                looser = winner - 2;
            }
            if (random.nextBoolean()) {
                set.setScoreA(winner);
                set.setScoreB(looser);
                match.setWinner(false);
            } else {
                set.setScoreA(looser);
                set.setScoreB(winner);
                match.setWinner(true);
            }

            setArrayList.add(set);
            sets.persist(set);
        }
        match.setFinished(true);
        match.setSets(setArrayList);
        matchRepository.persist(match);
    }

    private Player createPlayer(AGE_RESTR ageRestr) {
        Player player = new Player();
        String name = faker.harryPotter().character();
        player.setFirstName(name.split(" ")[0]);
        player.setLastName(name.contains(" ") ? name.split(" ", 2)[1] : "");
        player.setEmail(player.getFirstName() + "." + player.getLastName() + "@gmail.com");
        player.setPhone(faker.phoneNumber().cellPhone());
        player.setMailVerified(true);
        player.setAdminVerified(true);
        switch (ageRestr) {
            case NONE -> player.setBirthday(LocalDate.now().minusYears(random.nextInt(50)));
            case U18 -> player.setBirthday(LocalDate.now().minusYears(random.nextInt(18)));
            case O50 -> player.setBirthday(LocalDate.now().minusYears(random.nextInt(20) + 50));

        }
        return player;
    }

    private Team[] createTeams(Competition competition, CompetitionSettings competitionSettings) {
        Team[] teams = new Team[competitionSettings.getTeamNumbers()];
        for (int i = 0; i < competitionSettings.getTeamNumbers(); i++) {
            Player player = createPlayer(competitionSettings.getAgeRestr());
            if (competitionSettings.getSex() == Sex.ANY) {
                player.setSex(SexType.MALE);
            } else {
                player.setSex(SexType.FEMALE);
            }
            players.persist(player);

            teams[i] = new Team();
            teams[i].setCompetition(competition);
            teams[i].setPlayerA(player);

            if (competitionSettings.getCompetitionMode() == CompetitionMode.DOUBLES) {
                Player player2 = createPlayer(competitionSettings.getAgeRestr());
                if (competitionSettings.getSex() == Sex.ANY || competitionSettings.getSex() == Sex.FEMALE) {
                    player2.setSex(SexType.FEMALE);
                } else {
                    player2.setSex(SexType.FEMALE);
                }
                players.persist(player2);
                teams[i].setPlayerB(player2);
            }

            teamRepository.persist(teams[i]);
        }
        return teams;
    }

    private void addTeamsAndMatchesToGroup(Competition competition, CompetitionSettings compSettings) {
        Team[] groupTeams = createTeams(competition, compSettings);

        Group[] groups = new Group[compSettings.getNumberOfGroups()];
        for (int k = 0; k < compSettings.getNumberOfGroups(); k++) {
            groups[k] = new Group();
            groups[k].setIndex(k + 1);
            groups[k].setCompetition(competition);
            groupRepository.persist(groups[k]);
            for (int i = k * compSettings.getNumberOfGroups() / compSettings.getNumberOfGroups(); i < compSettings
                .getTeamNumbers() / compSettings.getNumberOfGroups() * (k + 1); i++) {
                for (int j = i + 1; j < compSettings.getTeamNumbers() / compSettings
                    .getNumberOfGroups() * (k + 1); j++) {
                    Match match = createMatch(courts[i * j % 4], competition);
                    match.setTeamA(groupTeams[i]);
                    match.setTeamB(groupTeams[j]);
                    int matchBegin = random.nextInt(3);
                    if (matchBegin != 0) {
                        match.setBegin(LocalDateTime.now());
                        match.setEnd(LocalDateTime.now().plusHours(1));
                        createSets(match);
                    } else {
                        match.setBegin(LocalDateTime.now().plusMinutes(random.nextInt(60)));
                        match.setEnd(LocalDateTime.now().plusHours(2));
                    }
                    matchRepository.persist(match);
                    MatchOfGroup matchOfGroup = new MatchOfGroup();
                    matchOfGroup.setGroup(groups[k]);
                    matchOfGroup.setMatch(match);
                    matchOfGroupRepository.persist(matchOfGroup);
                }
            }
        }
        // TODO support more than 2 groups ? How is the tree supposed to look like again with Nextmatch? @Jonas
        Match finalOfGroupMatch1 = createMatch(courts[random.nextInt(4)], competition);
        matchRepository.persist(finalOfGroupMatch1);
        FinalOfGroup finalOfGroup1 = new FinalOfGroup();
        finalOfGroup1.setNextMatch(finalOfGroupMatch1);
        finalOfGroup1.setGroupA(groups[0]);
        finalOfGroup1.setGroupB(groups[1]);
        finalOfGroup1.setPos(1);
        finalOfGroupRepository.persist(finalOfGroup1);

        Match finalOfGroupMatch2 = createMatch(courts[random.nextInt(4)], competition);
        matchRepository.persist(finalOfGroupMatch2);
        FinalOfGroup finalOfGroup2 = new FinalOfGroup();
        finalOfGroup2.setNextMatch(finalOfGroupMatch2);
        finalOfGroup2.setGroupA(groups[0]);
        finalOfGroup2.setGroupB(groups[1]);
        finalOfGroup2.setPos(2);
        finalOfGroupRepository.persist(finalOfGroup2);
    }

    private void addTeamsAndMatchesToKnockout(Competition competition, CompetitionSettings compSettings,
                                              boolean finished) {
        Team[] knockoutTeams = createTeams(competition, compSettings);

        // CREATE KNOCKOUT MATCHES
        Match[] currentMatches = new Match[compSettings.getTeamNumbers() / 2];
        Match[] previousMatches = new Match[compSettings.getTeamNumbers() / 2];
        for (int i = compSettings.getTeamNumbers() / 2; i > 0; i /= 2) {
            for (int j = 0; j < i; j++) {
                currentMatches[j] = createMatch(courts[j % 4], competition);

                if (i == compSettings.getTeamNumbers() / 2) {
                    currentMatches[j].setFinished(true);
                    currentMatches[j].setTeamA(knockoutTeams[j]);
                    currentMatches[j].setTeamB(knockoutTeams[j * 2 + 1]);
                }
                currentMatches[j].setFinished(false);

                matchRepository.persist(currentMatches[j]);

                if (i != compSettings.getTeamNumbers() / 2) {
                    if (previousMatches[j * 2].isFinished() && previousMatches[j * 2 + 1].isFinished())
                        if (finished || random.nextInt(10) > 0) createSets(currentMatches[j]);

                    NextMatch nextMatch = new NextMatch();
                    nextMatch.setPreviousA(previousMatches[j * 2]);
                    nextMatch.setPreviousB(previousMatches[j * 2 + 1]);
                    nextMatch.setNextMatch(currentMatches[j]);
                    nextMatches.persist(nextMatch);

                    currentMatches[j].setTeamA(getWinnerOfMatchIfExists(previousMatches[j * 2]));
                    currentMatches[j].setTeamB(getWinnerOfMatchIfExists(previousMatches[j * 2 + 1]));
                    matchRepository.persist(currentMatches[j]);
                } else if (finished || random.nextInt(10) > 0) createSets(currentMatches[j]);
            }

            if (i == 1) {
                currentMatches[1] = createMatch(courts[0 % 4], competition);
                currentMatches[1].setTeamA(getLooserOfMatchIfExists(previousMatches[0]));
                currentMatches[1].setTeamB(getLooserOfMatchIfExists(previousMatches[1]));

                matchRepository.persist(currentMatches[1]);
                if (previousMatches[0].isFinished() && previousMatches[1].isFinished())
                    createSets(currentMatches[1]);
                NextMatch nextThirdPlace = new NextMatch();
                nextThirdPlace.setPreviousA(previousMatches[0]);
                nextThirdPlace.setPreviousB(previousMatches[1]);
                nextThirdPlace.setNextMatch(currentMatches[1]);
                nextThirdPlace.setWinner(false);
                nextMatches.persist(nextThirdPlace);
            }

            previousMatches = currentMatches;
            currentMatches = new Match[compSettings.getTeamNumbers() / 2];
        }
    }

    private Team getWinnerOfMatchIfExists(Match match) {
        if (match.isFinished()) {
            if (match.getWinner()) return match.getTeamB();
            else return match.getTeamA();
        } else {
            return null;
        }
    }

    private Team getLooserOfMatchIfExists(Match match) {
        if (match.isFinished()) {
            if (match.getWinner()) return match.getTeamA();
            else return match.getTeamB();
        } else {
            return null;
        }
    }

    private Match createMatch(Court c, Competition competition) {
        Match match = new Match();
        match.setCompetition(competition);
        match.setCourt(c);
        match.setBegin(LocalDateTime.now());
        match.setEnd(LocalDateTime.now().plusDays(1));
        match.setFinished(false);
        matchRepository.persist(match);
        return match;
    }

    /**
     * Create the competitions to a tournament.
     * The method expects the tournament and a List of Tuple7 (Competition settings),
     * the CompetitionType (Knockout, Group), CompetitionMode (Double,Single), an integer (number of Teams)
     * This should be a power of 2 for Knockout, a Boolean (individual or joint sign up), number of Groups
     * This should also be a power of 2, and name of Competition
     *
     * @param t        the tournament to the competitions
     * @param compList A list of competition settings
     */
    private void addCompetition(Tournament t, TDate tDate,
                                List<CompetitionSettings> compList) {
        for (CompetitionSettings compSetting : compList) {
            Competition competition = new Competition();
            competition.setTournament(t);
            switch (compSetting.getAgeRestr()) {
                case NONE -> {
                    competition.setPlayerAhasMinAge(false);
                    competition.setPlayerAhasMaxAge(false);
                    competition.setPlayerBhasMinAge(false);
                    competition.setPlayerBhasMaxAge(false);
                }
                case U18 -> {
                    competition.setPlayerAhasMinAge(false);
                    competition.setPlayerAhasMaxAge(true);
                    competition.setPlayerAmaxAge(LocalDate.now().minusYears(19));
                    competition.setPlayerBhasMinAge(false);
                    competition.setPlayerBhasMaxAge(true);
                    competition.setPlayerBmaxAge(LocalDate.now().minusYears(19));
                }
                case O50 -> {
                    competition.setPlayerAhasMinAge(true);
                    competition.setPlayerAhasMaxAge(false);
                    competition.setPlayerAminAge(LocalDate.now().minusYears(50));
                    competition.setPlayerBhasMinAge(true);
                    competition.setPlayerBhasMaxAge(false);
                    competition.setPlayerBminAge(LocalDate.now().minusYears(50));
                }
            }
            competition.setName(compSetting.getName());

            // Set description
            if (compSetting.getCompetitionMode() == CompetitionMode.SINGLES) {
                competition.setMode(CompetitionMode.SINGLES);
                competition.setSignup(CompetitionSignUp.INDIVIDUAL);
                competition.setPlayerBdifferent(false);
                switch (compSetting.getSex()) {
                    case ANY -> throw new RuntimeException();
                    case MALE -> {
                        competition.setPlayerASex(Sex.MALE);
                        competition.setPlayerBSex(Sex.MALE);
                        competition.setDescription("Herren-Konkurrenz");
                    }
                    case FEMALE -> {
                        competition.setPlayerASex(Sex.FEMALE);
                        competition.setPlayerBSex(Sex.FEMALE);
                        competition.setDescription("Damen-Konkurrenz");
                    }
                }
            } else {
                competition.setMode(CompetitionMode.DOUBLES);
                if (compSetting.isDifferentConditions()) {
                    competition.setPlayerBdifferent(true);
                    if (compSetting.isRegisterIndividual()) {
                        competition.setDescription("Doppel, individuelle Anmeldung, verschiedene Bedingungen");
                        competition.setSignup(CompetitionSignUp.INDIVIDUAL);
                    } else {
                        competition.setDescription("Doppel, gemeinsame Anmeldung, verschiedene Bedingungen");
                        competition.setSignup(CompetitionSignUp.TOGETHER);
                    }
                } else {
                    competition.setPlayerBdifferent(false);
                    if (compSetting.isRegisterIndividual()) {
                        competition.setDescription("Doppel, individuelle Anmeldung, gleiche Bedingungen");
                        competition.setSignup(CompetitionSignUp.INDIVIDUAL);
                    } else {
                        competition.setDescription("Doppel, gemeinsame Anmeldung, gleiche Bedingungen");
                        competition.setSignup(CompetitionSignUp.TOGETHER);
                    }
                }
                switch (compSetting.getSex()) {
                    case ANY -> {
                        competition.setPlayerASex(Sex.MALE);
                        competition.setPlayerBSex(Sex.FEMALE);
                    }
                    case MALE -> {
                        competition.setPlayerASex(Sex.MALE);
                        competition.setPlayerBSex(Sex.MALE);
                    }
                    case FEMALE -> {
                        competition.setPlayerASex(Sex.FEMALE);
                        competition.setPlayerBSex(Sex.FEMALE);
                    }
                }
            }

            competition.setType(compSetting.getCompetitionType());
            competitions.persist(competition);
            switch (tDate) {
                case BEFORE_REGISTRATION -> {
                }
                case REGISTRATION_OPEN, BEFORE_GAMEPHASE -> createTeams(competition, compSetting);
                case GAMEPHASE_OPEN, AFTER_GAMEPHASE -> {
                    if (compSetting.getCompetitionType() == CompetitionType.KNOCKOUT) {
                        addTeamsAndMatchesToKnockout(competition, compSetting, tDate == TDate.AFTER_GAMEPHASE);
                    } else {
                        addTeamsAndMatchesToGroup(competition, compSetting);
                    }
                }
            }
        }
    }
}

