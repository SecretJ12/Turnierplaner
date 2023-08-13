package de.secretj12.turnierplaner.startup;

import de.secretj12.turnierplaner.db.entities.*;
import de.secretj12.turnierplaner.db.entities.competition.*;
import de.secretj12.turnierplaner.db.entities.groups.FinalOfGroup;
import de.secretj12.turnierplaner.db.entities.groups.Group;
import de.secretj12.turnierplaner.db.entities.groups.MatchOfGroup;
import de.secretj12.turnierplaner.db.entities.knockout.NextMatch;
import de.secretj12.turnierplaner.db.repositories.*;
import io.smallrye.mutiny.tuples.Tuple3;
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
        Tournament[] tournaments = new Tournament[6];
        tournaments[0] = createTournament(TDate.BEFORE_REGISTRATION, "Clubmeisterschaft 2026", "Anmeldung ausstehend", true);
        tournaments[1] = createTournament(TDate.REGISTRATION_OPEN, "Clubmeisterschaft 2025", "Anmeldung offen", true);
        tournaments[2] = createTournament(TDate.BEFORE_GAMEPHASE, "Clubmeisterschaft 2024", "Anmeldung vorbei", true);
        tournaments[3] = createTournament(TDate.GAMEPHASE_OPEN, "Clubmeisterschaft 2023", "Spielphase offen", true);
        tournaments[4] = createTournament(TDate.AFTER_GAMEPHASE, "Clubmeisterschaft 2022", "Spielphase vorbei", true);
        tournaments[5] = createTournament(TDate.BEFORE_REGISTRATION, "Clubmeisterschaft 2027", "Noch in der Planung", false);

        ArrayList<Tuple9<CompetitionType, CompetitionMode, Sex, Integer, Boolean, Boolean, Integer, String, AGE_RESTR>> compSetting = new ArrayList<>();
        compSetting.add(Tuple9.of(CompetitionType.GROUPS, CompetitionMode.SINGLES, Sex.MALE, 8, false, false, 2, "Single Groups", AGE_RESTR.NONE));
        compSetting.add(Tuple9.of(CompetitionType.GROUPS, CompetitionMode.SINGLES, Sex.MALE, 8, false, false, 2, "Single U18", AGE_RESTR.U18));
        compSetting.add(Tuple9.of(CompetitionType.KNOCKOUT, CompetitionMode.SINGLES, Sex.FEMALE, 16, false, false, 2, "Single Knockout", AGE_RESTR.NONE));
        compSetting.add(Tuple9.of(CompetitionType.KNOCKOUT, CompetitionMode.DOUBLES, Sex.ANY, 4, false, false, 2, "Double", AGE_RESTR.NONE));
        compSetting.add(Tuple9.of(CompetitionType.KNOCKOUT, CompetitionMode.DOUBLES, Sex.ANY, 4, true, false, 2, "Double random", AGE_RESTR.NONE));
        compSetting.add(Tuple9.of(CompetitionType.KNOCKOUT, CompetitionMode.DOUBLES, Sex.ANY, 4, false, true, 2, "Double O50", AGE_RESTR.O50));
        compSetting.add(Tuple9.of(CompetitionType.KNOCKOUT, CompetitionMode.DOUBLES, Sex.ANY, 4, true, true, 2, "Double Mixed", AGE_RESTR.NONE));

        addCompetition(tournaments[0], compSetting);
        addCompetition(tournaments[1], compSetting);
        addCompetition(tournaments[2], compSetting);
        addCompetition(tournaments[3], compSetting);
        addCompetition(tournaments[4], compSetting);
        addCompetition(tournaments[5], compSetting);
    }

    private Tournament createTournament(TDate tDate, String name, String description, boolean visible) {
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
        return tournament;
    }

    private void createSets(Match match, boolean started) {
        ArrayList<Set> setArrayList = new ArrayList<>();
        if (!started) {
            Set.SetKey setKey = new Set.SetKey();
            setKey.setMatch(match);
            setKey.setIndex(0);
            Set set = new Set();
            set.setKey(setKey);
            set.setScoreA(0);
            set.setScoreB(0);
            setArrayList.add(set);
            sets.persist(set);
        } else {
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
                        winDif++;
                    }
                    case 1 -> {
                        set.setScoreA(possibleResults.get(r).getRight());
                        set.setScoreB(possibleResults.get(r).getLeft());
                        winDif--;
                    }
                    default -> {
                        if (winDif > 0) {
                            set.setScoreA(possibleResults.get(r).getLeft());
                            set.setScoreB(possibleResults.get(r).getRight());
                            winDif++;
                        } else {
                            set.setScoreA(possibleResults.get(r).getRight());
                            set.setScoreB(possibleResults.get(r).getLeft());
                            winDif--;
                        }
                    }
                }

                setArrayList.add(set);
                sets.persist(set);
            }
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
                } else {
                    set.setScoreA(looser);
                    set.setScoreB(winner);
                }

                setArrayList.add(set);
                sets.persist(set);
            }
        }
        match.setSets(setArrayList);
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

    private Team[] createTeams(Competition competition, Sex sex, int numberTeams, boolean doublePlayer) {
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

            if (doublePlayer) {
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

    private void addTeamsAndMatchesToGroup(Competition competition, int numberTeams, boolean doublePlayer, Sex sex,
                                           int numberGroups) {
        Team[] groupTeams = createTeams(competition, sex, numberTeams, doublePlayer);

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
                        createSets(match, true);
                    } else {
                        match.setBegin(LocalDateTime.now().plusMinutes(random.nextInt(60)));
                        match.setEnd(LocalDateTime.now().plusHours(2));
                        createSets(match, false);
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


    private void addTeamsAndMatchesToKnockout(Competition competition, int numberTeams, boolean doublePlayer, Sex sex) {
        Team[] knockoutTeams = createTeams(competition, sex, numberTeams, doublePlayer);

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

                currentMatches[j].setWinner(false);
                matchRepository.persist(currentMatches[j]);
                createSets(currentMatches[j], true);
            }

            if (i == 1) {
                currentMatches[1] = createMatch(courts[0 % 4], competition);
                matchRepository.persist(currentMatches[1]);
                createSets(currentMatches[1], false);
                NextMatch nextThirdPlace = new NextMatch();
                nextThirdPlace.setPreviousA(previousMatches[0]);
                nextThirdPlace.setPreviousB(previousMatches[1]);
                nextThirdPlace.setNextMatch(currentMatches[1]);
                nextThirdPlace.setWinner(false);
                nextMatches.persist(nextThirdPlace);
            }

            if (i != numberTeams / 2) {
                for (int j = 0; j < i; j++) {
                    NextMatch nextMatch = new NextMatch();
                    nextMatch.setPreviousA(previousMatches[j * 2]);
                    nextMatch.setPreviousB(previousMatches[j * 2 + 1]);
                    nextMatch.setNextMatch(currentMatches[j]);
                    nextMatches.persist(nextMatch);
                }
            }
            previousMatches = currentMatches;
            currentMatches = new Match[numberTeams / 2];
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
    private void addCompetition(Tournament t,
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

            // SET COMP TYPE == KNOCKOUT
            if (compSetting.getItem1() == CompetitionType.KNOCKOUT) {
                competition.setType(CompetitionType.KNOCKOUT);
                competitions.persist(competition);
                addTeamsAndMatchesToKnockout(competition, compSetting.getItem4(), compSetting.getItem2() == CompetitionMode.DOUBLES, compSetting.getItem3());
            } else {
                competition.setType(CompetitionType.GROUPS);
                competitions.persist(competition);
                addTeamsAndMatchesToGroup(competition, compSetting.getItem4(), compSetting.getItem2() == CompetitionMode.DOUBLES, compSetting.getItem3(), compSetting.getItem7());
            }
        }
    }
}

