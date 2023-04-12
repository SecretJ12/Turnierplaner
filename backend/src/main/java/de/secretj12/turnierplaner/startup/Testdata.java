package de.secretj12.turnierplaner.startup;

import de.secretj12.turnierplaner.db.entities.*;
import de.secretj12.turnierplaner.db.entities.competition.*;
import de.secretj12.turnierplaner.db.entities.groups.FinalOfGroup;
import de.secretj12.turnierplaner.db.entities.groups.Group;
import de.secretj12.turnierplaner.db.entities.groups.MatchOfGroup;
import de.secretj12.turnierplaner.db.entities.knockout.NextMatch;
import de.secretj12.turnierplaner.db.repositories.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class Testdata {

    @Inject
    TournamentRepository tournaments;
    @Inject
    CompetitionRepository competitions;
    @Inject
    PlayerRepository players;
    @Inject
    MatchRepository matches;
    @Inject
    CourtRepositiory courts;
    @Inject
    NextMatchRepository nextMatches;
    @Inject
    GroupRepository groups;
    @Inject
    TeamRepository teams;
    @Inject
    MatchOfGroupRepository matchOfGroupRepository;
    @Inject
    FinalOfGroupRepository finalOfGroupRepository;

    public Testdata() {

    }

    @Transactional
    public void createData() {
        Court c1 = new Court();
        c1.setName("Platz 1");
        courts.persist(c1);
        Court c2 = new Court();
        c2.setName("Platz 2");
        courts.persist(c2);
        Court c3 = new Court();
        c3.setName("Platz 3");
        courts.persist(c3);
        Court c4 = new Court();
        c4.setName("Platz 4");
        courts.persist(c4);

        Player pm = new Player();
        pm.setFirstName("Rainer");
        pm.setLastName("Zufall");
        pm.setBirthday(LocalDate.now());
        pm.setSex(SexType.MALE);
        pm.setEmail("abc@def.ghi");
        pm.setPhone("+49 123 456789");
        pm.setMailVerified(true);
        pm.setAdminVerified(true);
        players.persist(pm);

        Player pw = new Player();
        pw.setFirstName("Klara");
        pw.setLastName("Fall");
        pw.setBirthday(LocalDate.now());
        pw.setSex(SexType.FEMALE);
        pw.setEmail("abc@def.ghi");
        pw.setPhone("+49 123 456789");
        pw.setMailVerified(true);
        pw.setAdminVerified(true);
        players.persist(pw);

        Tournament tour1 = new Tournament();
        tour1.setName("Clubmeisterschaft 2021");
        tour1.setDescription("Anmeldung ausstehend");
        tour1.setBeginRegistration(LocalDateTime.now().plusDays(10));
        tour1.setEndRegistration(LocalDateTime.now().plusDays(11));
        tour1.setBeginGamePhase(LocalDateTime.now().plusDays(12));
        tour1.setEndGamePhase(LocalDateTime.now().plusDays(13));
        tour1.setVisible(true);
        tournaments.persist(tour1);
        addDefaultCompetitions(tour1, pm);

        Tournament tour2 = new Tournament();
        tour2.setName("Clubmeisterschaft 2022");
        tour2.setDescription("Anmeldung offen");
        tour2.setBeginRegistration(LocalDateTime.now().minusDays(1));
        tour2.setEndRegistration(LocalDateTime.now().plusDays(10));
        tour2.setBeginGamePhase(LocalDateTime.now().plusDays(11));
        tour2.setEndGamePhase(LocalDateTime.now().plusDays(12));
        tour2.setVisible(true);
        tournaments.persist(tour2);

        Competition compSingle = new Competition();
        compSingle.setName("Single");
        compSingle.setTournament(tour2);
        compSingle.setDescription("Einzelkonkurrenz");
        compSingle.setType(CompetitionType.GROUPS);
        compSingle.setMode(CompetitionMode.SINGLES);
        compSingle.setSignup(CompetitionSignUp.INDIVIDUAL);
        compSingle.setPlayerASex(Sex.ANY);
        compSingle.setPlayerAhasMinAge(false);
        compSingle.setPlayerAhasMaxAge(false);
        compSingle.setPlayerBdifferent(false);
        compSingle.setPlayerBSex(Sex.FEMALE);
        compSingle.setPlayerBhasMinAge(false);
        compSingle.setPlayerBhasMaxAge(false);
        competitions.persist(compSingle);
        Team singleT1 = new Team();
        singleT1.setCompetition(compSingle);
        singleT1.setPlayerA(pm);
        teams.persist(singleT1);
        Team singleT2 = new Team();
        singleT2.setCompetition(compSingle);
        singleT2.setPlayerA(pw);
        teams.persist(singleT2);

        Competition compDoubleIndSame = new Competition();
        compDoubleIndSame.setName("DoubleIndSame");
        compDoubleIndSame.setTournament(tour2);
        compDoubleIndSame.setDescription("Doppel, individuelle Anmeldung, gleiche Bedingungen");
        compDoubleIndSame.setType(CompetitionType.GROUPS);
        compDoubleIndSame.setMode(CompetitionMode.DOUBLES);
        compDoubleIndSame.setSignup(CompetitionSignUp.INDIVIDUAL);
        compDoubleIndSame.setPlayerASex(Sex.ANY);
        compDoubleIndSame.setPlayerAhasMinAge(false);
        compDoubleIndSame.setPlayerAhasMaxAge(false);
        compDoubleIndSame.setPlayerBdifferent(false);
        compDoubleIndSame.setPlayerBSex(Sex.FEMALE);
        compDoubleIndSame.setPlayerBhasMinAge(false);
        compDoubleIndSame.setPlayerBhasMaxAge(false);
        competitions.persist(compDoubleIndSame);
        Team doubleIndSameT1 = new Team();
        doubleIndSameT1.setCompetition(compDoubleIndSame);
        doubleIndSameT1.setPlayerA(pm);
        teams.persist(doubleIndSameT1);
        Team doubleIndSameT2 = new Team();
        doubleIndSameT2.setCompetition(compDoubleIndSame);
        doubleIndSameT2.setPlayerA(pw);
        teams.persist(doubleIndSameT2);

        Competition compDoubleIndDif = new Competition();
        compDoubleIndDif.setName("DoubleIndDif");
        compDoubleIndDif.setTournament(tour2);
        compDoubleIndDif.setDescription("Doppel, individuelle Anmeldung, verschiedene Bedingungen");
        compDoubleIndDif.setType(CompetitionType.GROUPS);
        compDoubleIndDif.setMode(CompetitionMode.DOUBLES);
        compDoubleIndDif.setSignup(CompetitionSignUp.INDIVIDUAL);
        compDoubleIndDif.setPlayerASex(Sex.MALE);
        compDoubleIndDif.setPlayerAhasMinAge(false);
        compDoubleIndDif.setPlayerAhasMaxAge(false);
        compDoubleIndDif.setPlayerBdifferent(true);
        compDoubleIndDif.setPlayerBSex(Sex.FEMALE);
        compDoubleIndDif.setPlayerBhasMinAge(false);
        compDoubleIndDif.setPlayerBhasMaxAge(false);
        competitions.persist(compDoubleIndDif);
        Team doubleIndSameDifT1 = new Team();
        doubleIndSameDifT1.setCompetition(compDoubleIndDif);
        doubleIndSameDifT1.setPlayerA(pm);
        teams.persist(doubleIndSameDifT1);
        Team doubleIndSameDifT2 = new Team();
        doubleIndSameDifT2.setCompetition(compDoubleIndDif);
        doubleIndSameDifT2.setPlayerB(pw);
        teams.persist(doubleIndSameDifT2);

        Competition compDoubleTog = new Competition();
        compDoubleTog.setName("DoubleTog");
        compDoubleTog.setTournament(tour2);
        compDoubleTog.setDescription("Doppel, gemeinsame Anmeldung");
        compDoubleTog.setType(CompetitionType.GROUPS);
        compDoubleTog.setMode(CompetitionMode.DOUBLES);
        compDoubleTog.setSignup(CompetitionSignUp.TOGETHER);
        compDoubleTog.setPlayerASex(Sex.MALE);
        compDoubleTog.setPlayerAhasMinAge(false);
        compDoubleTog.setPlayerAhasMaxAge(false);
        compDoubleTog.setPlayerBdifferent(true);
        compDoubleTog.setPlayerBSex(Sex.FEMALE);
        compDoubleTog.setPlayerBhasMinAge(false);
        compDoubleTog.setPlayerBhasMaxAge(false);
        competitions.persist(compDoubleTog);
        Team doubleTogT1 = new Team();
        doubleTogT1.setCompetition(compDoubleTog);
        doubleTogT1.setPlayerA(pm);
        doubleTogT1.setPlayerB(pw);
        teams.persist(doubleTogT1);

        Tournament tour3 = new Tournament();
        tour3.setName("Clubmeisterschaft 2023");
        tour3.setDescription("Anmeldung vorbei");
        tour3.setBeginRegistration(LocalDateTime.now().minusDays(2));
        tour3.setEndRegistration(LocalDateTime.now().minusDays(1));
        tour3.setBeginGamePhase(LocalDateTime.now().plusDays(10));
        tour3.setEndGamePhase(LocalDateTime.now().plusDays(11));
        tour3.setVisible(true);
        tournaments.persist(tour3);
        addDefaultCompetitions(tour3, pm);

        Tournament tour4 = new Tournament();
        tour4.setName("Clubmeisterschaft 2024");
        tour4.setDescription("Spielphase offen");
        tour4.setBeginRegistration(LocalDateTime.now().minusDays(3));
        tour4.setEndRegistration(LocalDateTime.now().minusDays(2));
        tour4.setBeginGamePhase(LocalDateTime.now().minusDays(1));
        tour4.setEndGamePhase(LocalDateTime.now().plusDays(10));
        tour4.setVisible(true);
        tournaments.persist(tour4);

        Competition comp1 = new Competition();
        comp1.setName("Damen");
        comp1.setTournament(tour4);
        comp1.setDescription("Damen-Konkurrenz");
        comp1.setType(CompetitionType.GROUPS);
        Team t1 = new Team();
        t1.setPlayerA(pm);
        comp1.setTeams(List.of(t1));
        comp1.setMode(CompetitionMode.SINGLES);
        comp1.setSignup(CompetitionSignUp.INDIVIDUAL);
        comp1.setPlayerASex(Sex.FEMALE);
        comp1.setPlayerAhasMinAge(false);
        comp1.setPlayerAhasMaxAge(false);
        comp1.setPlayerBSex(Sex.FEMALE);
        comp1.setPlayerBdifferent(false);
        comp1.setPlayerBhasMinAge(false);
        comp1.setPlayerBhasMaxAge(false);
        competitions.persist(comp1);

        Team[] groupTeams = new Team[8];
        for (int i = 0; i < 8; i++) {
            String fn = String.valueOf((char) ('a' + i));
            fn += fn + fn;
            String ln = "" + (char) ('A' + i);
            ln += ln + ln;
            Player player = new Player();
            player.setFirstName(fn);
            player.setLastName(ln);
            player.setBirthday(LocalDate.now());
            player.setSex(SexType.MALE);
            player.setEmail(fn + "." + ln + "@mail.de");
            player.setPhone("+49 123 456789");
            player.setMailVerified(true);
            player.setAdminVerified(true);
            players.persist(player);
            groupTeams[i] = new Team();
            groupTeams[i].setCompetition(comp1);
            groupTeams[i].setPlayerA(player);
            teams.persist(groupTeams[i]);
        }

        Group[] groups = new Group[2];
        for (int i = 0; i < 2; i++) {
            groups[i] = new Group();
            groups[i].setIndex(i + 1);
            groups[i].setCompetition(comp1);
            this.groups.persist(groups[i]);
            for (int x = 4 * i; x < 4 * i + 4; x++) {
                for (int y = x + 1; y < 4 * i + 4; y++) {
                    Match match = createMatch(c1, comp1);
                    match.setTeamA(groupTeams[x]);
                    match.setTeamB(groupTeams[y]);
                    matches.persist(match);
                    MatchOfGroup matchOfGroup = new MatchOfGroup();
                    matchOfGroup.setGroup(groups[i]);
                    matchOfGroup.setMatch(match);
                    matchOfGroupRepository.persist(matchOfGroup);
                }
            }
        }

        Match finalOfGroupMatch1 = createMatch(c1, comp1);
        matches.persist(finalOfGroupMatch1);
        FinalOfGroup finalOfGroup1 = new FinalOfGroup();
        finalOfGroup1.setNextMatch(finalOfGroupMatch1);
        finalOfGroup1.setGroupA(groups[0]);
        finalOfGroup1.setGroupB(groups[1]);
        finalOfGroup1.setPos(1);
        finalOfGroupRepository.persist(finalOfGroup1);
        Match finalOfGroupMatch2 = createMatch(c1, comp1);
        matches.persist(finalOfGroupMatch2);
        FinalOfGroup finalOfGroup2 = new FinalOfGroup();
        finalOfGroup2.setNextMatch(finalOfGroupMatch2);
        finalOfGroup2.setGroupA(groups[0]);
        finalOfGroup2.setGroupB(groups[1]);
        finalOfGroup2.setPos(2);
        finalOfGroupRepository.persist(finalOfGroup2);

        Competition comp2 = new Competition();
        comp2.setName("Herren");
        comp2.setTournament(tour4);
        comp2.setDescription("Herren-Konkurrenz");
        comp2.setType(CompetitionType.KNOCKOUT);
        Team t2 = new Team();
        t2.setPlayerA(pm);
        comp2.setTeams(List.of(t2));
        comp2.setMode(CompetitionMode.SINGLES);
        comp2.setSignup(CompetitionSignUp.INDIVIDUAL);
        comp2.setPlayerASex(Sex.MALE);
        comp2.setPlayerAhasMinAge(false);
        comp2.setPlayerAhasMaxAge(false);
        comp2.setPlayerBSex(Sex.MALE);
        comp2.setPlayerBdifferent(false);
        comp2.setPlayerBhasMinAge(false);
        comp2.setPlayerBhasMaxAge(false);
        competitions.persist(comp2);

        Team[] knockoutTeams = new Team[8];
        for (int i = 0; i < 8; i++) {
            String fn = String.valueOf((char) ('a' + i));
            String ln = "knockout" + (char) ('A' + i);
            Player player = new Player();
            player.setFirstName(fn);
            player.setLastName(ln);
            player.setBirthday(LocalDate.now());
            player.setSex(SexType.MALE);
            player.setEmail(fn + "." + ln + "@mail.de");
            player.setPhone("+49 123 456789");
            player.setMailVerified(true);
            player.setAdminVerified(true);
            players.persist(player);
            knockoutTeams[i] = new Team();
            knockoutTeams[i].setCompetition(comp2);
            knockoutTeams[i].setPlayerA(player);
            teams.persist(knockoutTeams[i]);
        }

        Match[] quarterFinal = new Match[4];
        for (int i = 0; i < 4; i++) {
            quarterFinal[i] = createMatch(c1, comp2);
            quarterFinal[i].setTeamA(knockoutTeams[2 * i]);
            quarterFinal[i].setTeamB(knockoutTeams[2 * i + 1]);
            matches.persist(quarterFinal[i]);
        }
        Match[] semiFinal = new Match[2];
        for (int i = 0; i < 2; i++) {
            semiFinal[i] = createMatch(c2, comp2);

            NextMatch next = new NextMatch();
            next.setPreviousA(quarterFinal[2 * i]);
            next.setPreviousB(quarterFinal[2 * i + 1]);
            next.setNextMatch(semiFinal[i]);
            nextMatches.persist(next);
        }
        Match finalMatch = createMatch(c3, comp2);
        NextMatch nextFinal = new NextMatch();
        nextFinal.setPreviousA(semiFinal[0]);
        nextFinal.setPreviousB(semiFinal[1]);
        nextFinal.setNextMatch(finalMatch);
        nextMatches.persist(nextFinal);

        Match thirdPlaceMatch = createMatch(c4, comp2);
        NextMatch nextThirdPlace = new NextMatch();
        nextThirdPlace.setPreviousA(semiFinal[0]);
        nextThirdPlace.setPreviousB(semiFinal[1]);
        nextThirdPlace.setNextMatch(thirdPlaceMatch);
        nextFinal.setWinner(false);
        nextMatches.persist(nextThirdPlace);

        Tournament tour5 = new Tournament();
        tour5.setName("Clubmeisterschaft 2025");
        tour5.setDescription("Spielphase vorbei");
        tour5.setBeginRegistration(LocalDateTime.now().minusDays(4));
        tour5.setEndRegistration(LocalDateTime.now().minusDays(3));
        tour5.setBeginGamePhase(LocalDateTime.now().minusDays(2));
        tour5.setEndGamePhase(LocalDateTime.now().minusDays(1));
        tour5.setVisible(true);
        tournaments.persist(tour5);
        addDefaultCompetitions(tour5, pm);

        String[] firstNames = new String[]{"Charlene", "Amado", "Goldie", "Louis", "Carrol", "Everette", "Laverne", "Robt", "Abby", "German", "Katharine", "Vanessa", "Emilio", "Adela", "Mel", "Guy", "Cassandra", "Antonia", "Angie", "Mindy", "Brice", "Deshawn", "Shawna", "Bryant", "Zachariah", "Bernie", "Selena", "Lacey", "Darnell", "Cory", "Socorro", "Belinda", "Stephan", "Korey", "Josefina", "Joe", "Clyde", "Silas", "Moses", "Christian", "Dalton", "Adrienne", "Adan", "Helena", "Luann", "Chuck", "Leona", "Emmanuel", "Debra", "Jose", "Eldridge", "Lynnette", "Tony", "Katie", "Amber", "Carmela", "Paige", "Donn", "Alfred", "Reid", "Kerry", "Lillian", "Angel", "Bryant", "Gaylord", "Deidre", "Derrick", "Allison", "Jordon", "Alfreda", "Colby", "Leticia", "Lee", "Janna", "Ivory", "Hilda", "Lindsey", "Boris", "Stanford", "Johnnie", "Maura", "Joan", "Latisha", "Robin", "Letha", "Mariano", "Hong", "Leanna", "Caleb", "Rickey", "Alta", "Kari", "Rueben", "Hugh", "Charlotte", "Willian", "Edmond", "Trent", "Claudia", "Darla"};
        String[] lastNames = new String[]{"Brennan", "Meyers", "Mora", "Bailey", "Burns", "Trevino", "Horton", "Monroe", "Ali", "Jarvis", "Banks", "Leblanc", "Kidd", "Mooney", "Stout", "Kramer", "Galvan", "Barrett", "Steele", "Morgan", "English", "Pitts", "Fowler", "Vaughan", "Livingston", "May", "Holden", "Sandoval", "Freeman", "Hurley", "Lambert", "Watson", "Huff", "Ruiz", "Santana", "Conway", "Mcintosh", "Bates", "Greene", "Castaneda", "Mcclure", "Kemp", "Hanna", "Merritt", "Mccarthy", "Sampson", "Barnett", "Green", "Ewing", "Floyd", "Lambert", "Farley", "Berry", "Suarez", "Spears", "Martinez", "Cameron", "Ponce", "Roth", "Tate", "Reyes", "Mann", "Bell", "Singleton", "Fischer", "Reilly", "Maxwell", "Montoya", "Serrano", "Morrison", "Patrick", "Evans", "Crawford", "Shannon", "Calhoun", "Kemp", "Vasquez", "Dorsey", "Mills", "Bright", "Rivers", "Huffman", "Wiley", "Daugherty", "Mcintyre", "Mcmahon", "Boone", "Walls", "Campos", "Morton", "Avila", "Church", "Watkins", "Keller", "Klein", "Valentine", "Bartlett", "Chaney", "Wall", "Howard"};

        Random rn = new Random();
        for (int i = 0; i < 500; i++) {
            boolean male = rn.nextBoolean();

            String fn = firstNames[rn.nextInt(firstNames.length)] + (male?'m':'f');
            String ln = lastNames[rn.nextInt(lastNames.length)];
            Player pl = new Player();
            pl.setFirstName(fn);
            pl.setLastName(ln);
            pl.setBirthday(LocalDate.now());
            pl.setSex(male?SexType.MALE :SexType.FEMALE);
            pl.setEmail(fn + "." + ln + "@mail.de");
            pl.setPhone("+49 123 456789");
            pl.setMailVerified(true);
            pl.setAdminVerified(true);
            players.persist(pl);
        }
    }

    private Match createMatch(Court c1, Competition comp2) {
        Match match = new Match();
        match.setCompetition(comp2);
        match.setCourt(c1);
        match.setBegin(LocalDateTime.now());
        match.setEnd(LocalDateTime.now().plusDays(1));
        match.setFinished(false);
        matches.persist(match);
        return match;
    }

    private void addDefaultCompetitions(Tournament t, Player p) {
        Competition comp1 = new Competition();
        comp1.setName("Damen");
        comp1.setTournament(t);
        comp1.setDescription("Damen-Konkurrenz");
        comp1.setType(CompetitionType.GROUPS);
        comp1.setMode(CompetitionMode.SINGLES);
        comp1.setSignup(CompetitionSignUp.INDIVIDUAL);
        comp1.setPlayerASex(Sex.FEMALE);
        comp1.setPlayerAhasMinAge(false);
        comp1.setPlayerAhasMaxAge(false);
        comp1.setPlayerBSex(Sex.FEMALE);
        comp1.setPlayerBdifferent(false);
        comp1.setPlayerBhasMinAge(false);
        comp1.setPlayerBhasMaxAge(false);
        competitions.persist(comp1);
        Team t1 = new Team();
        t1.setCompetition(comp1);
        t1.setPlayerA(p);
        teams.persist(t1);

        Competition comp2 = new Competition();
        comp2.setName("Herren");
        comp2.setTournament(t);
        comp2.setDescription("Herren-Konkurrenz");
        comp2.setType(CompetitionType.KNOCKOUT);
        comp2.setMode(CompetitionMode.SINGLES);
        comp2.setSignup(CompetitionSignUp.INDIVIDUAL);
        comp2.setPlayerASex(Sex.MALE);
        comp2.setPlayerAhasMinAge(false);
        comp2.setPlayerAhasMaxAge(false);
        comp2.setPlayerBSex(Sex.MALE);
        comp2.setPlayerBdifferent(false);
        comp2.setPlayerBhasMinAge(false);
        comp2.setPlayerBhasMaxAge(false);
        competitions.persist(comp2);
        Team t2 = new Team();
        t2.setPlayerA(p);
        t2.setCompetition(comp2);
        teams.persist(t2);
    }
}

