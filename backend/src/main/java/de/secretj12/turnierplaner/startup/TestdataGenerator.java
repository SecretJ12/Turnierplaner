package de.secretj12.turnierplaner.startup;

import de.secretj12.turnierplaner.db.entities.*;
import de.secretj12.turnierplaner.db.entities.competition.*;
import de.secretj12.turnierplaner.db.entities.groups.FinalOfGroup;
import de.secretj12.turnierplaner.db.entities.groups.Group;
import de.secretj12.turnierplaner.db.entities.groups.MatchOfGroup;
import de.secretj12.turnierplaner.db.entities.knockout.NextMatch;
import de.secretj12.turnierplaner.db.repositories.*;
import io.smallrye.mutiny.tuples.Tuple9;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.datafaker.Faker;
import org.graalvm.collections.Pair;

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

    enum AGE_RESTR {
        NONE, U18, O50
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

        ArrayList<Tuple9<CompetitionType, CompetitionMode, Sex, Integer, Boolean, Boolean, Integer, String, AGE_RESTR>> compSetting = new ArrayList<>();
        compSetting.add(Tuple9.of(CompetitionType.GROUPS, CompetitionMode.SINGLES, Sex.MALE, 8, false, false, 2, "Single Groups", AGE_RESTR.NONE));
        compSetting.add(Tuple9.of(CompetitionType.GROUPS, CompetitionMode.SINGLES, Sex.MALE, 8, false, false, 2, "Single U18", AGE_RESTR.U18));
        compSetting.add(Tuple9.of(CompetitionType.KNOCKOUT, CompetitionMode.SINGLES, Sex.FEMALE, 16, false, false, 2, "Single Knockout", AGE_RESTR.NONE));
        compSetting.add(Tuple9.of(CompetitionType.KNOCKOUT, CompetitionMode.DOUBLES, Sex.ANY, 16, false, false, 2, "Double", AGE_RESTR.NONE));
        compSetting.add(Tuple9.of(CompetitionType.KNOCKOUT, CompetitionMode.DOUBLES, Sex.ANY, 32, true, false, 2, "Double random", AGE_RESTR.NONE));
        compSetting.add(Tuple9.of(CompetitionType.KNOCKOUT, CompetitionMode.DOUBLES, Sex.ANY, 8, false, true, 2, "Double O50", AGE_RESTR.O50));
        compSetting.add(Tuple9.of(CompetitionType.KNOCKOUT, CompetitionMode.DOUBLES, Sex.ANY, 4, true, true, 2, "Double Mixed", AGE_RESTR.NONE));

        createTournament(TDate.BEFORE_REGISTRATION, "Clubmeisterschaft 2026", "Anmeldung ausstehend", true, compSetting);
        createTournament(TDate.REGISTRATION_OPEN, "Clubmeisterschaft 2025", "Anmeldung offen", true, compSetting);
        createTournament(TDate.BEFORE_GAMEPHASE, "Clubmeisterschaft 2024", "Anmeldung vorbei", true, compSetting);
        createTournament(TDate.GAMEPHASE_OPEN, "Clubmeisterschaft 2023", "Spielphase offen", true, compSetting);
        createTournament(TDate.AFTER_GAMEPHASE, "Clubmeisterschaft 2022", "Spielphase vorbei", true, compSetting);
        createTournament(TDate.BEFORE_REGISTRATION, "Clubmeisterschaft 2027", "Noch in der Planung", false, compSetting);
    }

    private void createTournament(TDate tDate, String name, String description, boolean visible,
                                  List<Tuple9<CompetitionType, CompetitionMode, Sex, Integer, Boolean, Boolean, Integer, String, AGE_RESTR>> compSettings) {
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
        List<Pair<Integer, Integer>> possibleResults = List.of(Pair.create(6, 0), Pair.create(6, 1), Pair.create(6, 2), Pair.create(6, 3), Pair.create(6, 4), Pair.create(6, 5), Pair.create(7, 5), Pair.create(7, 6));
        for (int i = 0; i < numberOfSets; i++) {
            Set.SetKey setKey = new Set.SetKey();
            setKey.setMatch(match);
            setKey.setIndex(i);
            Set set = new Set();
            set.setKey(setKey);

            int r = random.nextInt(possibleResults.size());
            switch (random.nextInt(Math.abs(winDif) + 2)) {
                case 0 -> {
                    set.setScoreA(possibleResults.get(r).getLeft());
                    set.setScoreB(possibleResults.get(r).getRight());
                    winDif += winBias;
                }
                case 1 -> {
                    set.setScoreA(possibleResults.get(r).getRight());
                    set.setScoreB(possibleResults.get(r).getLeft());
                    winDif -= winBias;
                }
                default -> {
                    if (winDif > 0) {
                        set.setScoreA(possibleResults.get(r).getLeft());
                        set.setScoreB(possibleResults.get(r).getRight());
                        winDif += winBias;
                    } else {
                        set.setScoreA(possibleResults.get(r).getRight());
                        set.setScoreB(possibleResults.get(r).getLeft());
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

    private Player createPlayer() {
        Player player = new Player();
        player.setFirstName(faker.name().firstName());
        player.setLastName(faker.name().lastName());
        player.setEmail(player.getFirstName() + "." + player.getLastName() + "@gmail.com");
        player.setPhone(faker.phoneNumber().cellPhone());
        player.setMailVerified(true);
        player.setAdminVerified(true);
        player.setBirthday(LocalDate.now());
        return player;
    }

    private Team[] createTeams(Competition competition, Sex sex, int numberTeams, CompetitionMode mode) {
        Team[] teams = new Team[numberTeams];
        for (int i = 0; i < numberTeams; i++) {
            Player player = createPlayer();
            if (sex == Sex.ANY) {
                player.setSex(SexType.MALE);
            } else {
                player.setSex(SexType.FEMALE);
            }
            players.persist(player);

            teams[i] = new Team();
            teams[i].setCompetition(competition);
            teams[i].setPlayerA(player);

            if (mode == CompetitionMode.DOUBLES) {
                Player player2 = createPlayer();
                if (sex == Sex.ANY || sex == Sex.FEMALE) {
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

    private void addTeamsAndMatchesToGroup(Competition competition, int numberTeams, CompetitionMode mode, Sex sex,
                                           int numberGroups) {
        Team[] groupTeams = createTeams(competition, sex, numberTeams, mode);

        Group[] groups = new Group[numberGroups];
        for (int k = 0; k < numberGroups; k++) {
            groups[k] = new Group();
            groups[k].setIndex(k + 1);
            groups[k].setCompetition(competition);
            groupRepository.persist(groups[k]);
            for (int i = k * numberTeams / numberGroups; i < numberTeams / numberGroups * (k + 1); i++) {
                for (int j = i + 1; j < numberTeams / numberGroups * (k + 1); j++) {
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

    private void addTeamsAndMatchesToKnockout(Competition competition, int numberTeams, CompetitionMode mode, Sex sex,
                                              boolean finished) {
        Team[] knockoutTeams = createTeams(competition, sex, numberTeams, mode);

        // CREATE KNOCKOUT MATCHES
        Match[] currentMatches = new Match[numberTeams / 2];
        Match[] previousMatches = new Match[numberTeams / 2];
        for (int i = numberTeams / 2; i > 0; i /= 2) {
            for (int j = 0; j < i; j++) {
                currentMatches[j] = createMatch(courts[j % 4], competition);

                if (i == numberTeams / 2) {
                    currentMatches[j].setFinished(true);
                    currentMatches[j].setTeamA(knockoutTeams[j]);
                    currentMatches[j].setTeamB(knockoutTeams[j * 2 + 1]);
                }
                currentMatches[j].setFinished(false);

                matchRepository.persist(currentMatches[j]);

                if (i != numberTeams / 2) {
                    if (previousMatches[j * 2].isFinished() && previousMatches[j * 2 + 1].isFinished())
                        if (finished || random.nextInt(10) > 0)
                            createSets(currentMatches[j]);

                    NextMatch nextMatch = new NextMatch();
                    nextMatch.setPreviousA(previousMatches[j * 2]);
                    nextMatch.setPreviousB(previousMatches[j * 2 + 1]);
                    nextMatch.setNextMatch(currentMatches[j]);
                    nextMatches.persist(nextMatch);

                    currentMatches[j].setTeamA(getWinnerOfMatchIfExists(previousMatches[j * 2]));
                    currentMatches[j].setTeamB(getWinnerOfMatchIfExists(previousMatches[j * 2 + 1]));
                    matchRepository.persist(currentMatches[j]);
                } else if (finished || random.nextInt(10) > 0)
                    createSets(currentMatches[j]);
            }

            if (i == 1) {
                currentMatches[1] = createMatch(courts[0 % 4], competition);
                currentMatches[1].setTeamA(getLooserOfMatchIfExists(previousMatches[0]));
                currentMatches[1].setTeamB(getLooserOfMatchIfExists(previousMatches[1]));

                matchRepository.persist(currentMatches[1]);
                createSets(currentMatches[1]);
                NextMatch nextThirdPlace = new NextMatch();
                nextThirdPlace.setPreviousA(previousMatches[0]);
                nextThirdPlace.setPreviousB(previousMatches[1]);
                nextThirdPlace.setNextMatch(currentMatches[1]);
                nextThirdPlace.setWinner(false);
                nextMatches.persist(nextThirdPlace);
            }

            previousMatches = currentMatches;
            currentMatches = new Match[numberTeams / 2];
        }
    }

    private Team getWinnerOfMatchIfExists(Match match) {
        if (match.isFinished()) {
            if (match.getWinner())
                return match.getTeamB();
            else
                return match.getTeamA();
        } else {
            return null;
        }
    }

    private Team getLooserOfMatchIfExists(Match match) {
        if (match.isFinished()) {
            if (match.getWinner())
                return match.getTeamA();
            else
                return match.getTeamB();
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
                                List<Tuple9<CompetitionType, CompetitionMode, Sex, Integer, Boolean, Boolean, Integer, String, AGE_RESTR>> compList) {
        for (Tuple9<CompetitionType, CompetitionMode, Sex, Integer, Boolean, Boolean, Integer, String, AGE_RESTR> compSetting : compList) {
            Competition competition = new Competition();
            competition.setTournament(t);
            switch (compSetting.getItem9()) {
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
            competition.setName(compSetting.getItem8());

            // Set description
            if (compSetting.getItem2() == CompetitionMode.SINGLES) {
                competition.setMode(CompetitionMode.SINGLES);
                competition.setSignup(CompetitionSignUp.INDIVIDUAL);
                competition.setPlayerBdifferent(false);
                switch (compSetting.getItem3()) {
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
                if (compSetting.getItem6()) {
                    competition.setPlayerBdifferent(true);
                    if (compSetting.getItem5()) {
                        competition.setDescription("Doppel, individuelle Anmeldung, verschiedene Bedingungen");
                        competition.setSignup(CompetitionSignUp.INDIVIDUAL);
                    } else {
                        competition.setDescription("Doppel, gemeinsame Anmeldung, verschiedene Bedingungen");
                        competition.setSignup(CompetitionSignUp.TOGETHER);
                    }
                } else {
                    competition.setPlayerBdifferent(false);
                    if (compSetting.getItem5()) {
                        competition.setDescription("Doppel, individuelle Anmeldung, gleiche Bedingungen");
                        competition.setSignup(CompetitionSignUp.INDIVIDUAL);
                    } else {
                        competition.setDescription("Doppel, gemeinsame Anmeldung, gleiche Bedingungen");
                        competition.setSignup(CompetitionSignUp.TOGETHER);
                    }
                }
                switch (compSetting.getItem3()) {
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

            competition.setType(compSetting.getItem1());
            competitions.persist(competition);
            switch (tDate) {
                case BEFORE_REGISTRATION -> {
                }
                case REGISTRATION_OPEN, BEFORE_GAMEPHASE ->
                        createTeams(competition, compSetting.getItem3(), compSetting.getItem4(), compSetting.getItem2());
                case GAMEPHASE_OPEN, AFTER_GAMEPHASE -> {
                    if (compSetting.getItem1() == CompetitionType.KNOCKOUT) {
                        addTeamsAndMatchesToKnockout(competition, compSetting.getItem4(), compSetting.getItem2(), compSetting.getItem3(), tDate == TDate.AFTER_GAMEPHASE);
                    } else {
                        addTeamsAndMatchesToGroup(competition, compSetting.getItem4(), compSetting.getItem2(), compSetting.getItem3(), compSetting.getItem7());
                    }
                }
            }
        }
    }
}

