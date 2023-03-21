package de.secretj12.turnierplaner.startup;

import de.secretj12.turnierplaner.db.entities.*;
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

        // too good to be left out
        Player p = new Player();
        p.setFirstName("Rainer");
        p.setLastName("Zufall");
        p.setBirthday(LocalDate.now());
        p.setSex(SexType.male);
        p.setEmail("abc@def.ghi");
        p.setPhone("+49 123 456789");
        p.setMailVerified(true);
        p.setAdminVerified(true);
        players.persist(p);

        Tournament tour1 = new Tournament();
        tour1.setName("Clubmeisterschaft 2021");
        tour1.setDescription("Anmeldung ausstehend");
        tour1.setBeginRegistration(LocalDateTime.now().plusDays(10));
        tour1.setEndRegistration(LocalDateTime.now().plusDays(11));
        tour1.setBeginGamePhase(LocalDateTime.now().plusDays(12));
        tour1.setEndGamePhase(LocalDateTime.now().plusDays(13));
        tour1.setVisible(true);
        tournaments.persist(tour1);
        addDefaultCompetitions(tour1, p);

        Tournament tour2 = new Tournament();
        tour2.setName("Clubmeisterschaft 2022");
        tour2.setDescription("Anmeldung offen");
        tour2.setBeginRegistration(LocalDateTime.now().minusDays(1));
        tour2.setEndRegistration(LocalDateTime.now().plusDays(10));
        tour2.setBeginGamePhase(LocalDateTime.now().plusDays(11));
        tour2.setEndGamePhase(LocalDateTime.now().plusDays(12));
        tour2.setVisible(true);
        tournaments.persist(tour2);
        addDefaultCompetitions(tour2, p);

        Tournament tour3 = new Tournament();
        tour3.setName("Clubmeisterschaft 2023");
        tour3.setDescription("Anmeldung vorbei");
        tour3.setBeginRegistration(LocalDateTime.now().minusDays(2));
        tour3.setEndRegistration(LocalDateTime.now().minusDays(1));
        tour3.setBeginGamePhase(LocalDateTime.now().plusDays(10));
        tour3.setEndGamePhase(LocalDateTime.now().plusDays(11));
        tour3.setVisible(true);
        tournaments.persist(tour3);
        addDefaultCompetitions(tour3, p);

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
        comp1.setPlayers(List.of(p));
        competitions.persist(comp1);

        // TODO add example for group system

        Competition comp2 = new Competition();
        comp2.setName("Herren");
        comp2.setTournament(tour4);
        comp2.setDescription("Herren-Konkurrenz");
        comp2.setType(CompetitionType.KNOCKOUT);
        comp2.setPlayers(List.of(p));
        competitions.persist(comp2);

        Player[] knockoutPlayers = new Player[8];
        for (int i = 0; i < 8; i++) {
            String fn = String.valueOf((char) ('a'+i));
            String ln = String.valueOf((char) ('A'+i));
            knockoutPlayers[i] = new Player();
            knockoutPlayers[i].setFirstName(fn);
            knockoutPlayers[i].setLastName(ln);
            knockoutPlayers[i].setBirthday(LocalDate.now());
            knockoutPlayers[i].setSex(SexType.male);
            knockoutPlayers[i].setEmail(fn + "." + ln + "@mail.de");
            knockoutPlayers[i].setPhone("+49 123 456789");
            knockoutPlayers[i].setMailVerified(true);
            knockoutPlayers[i].setAdminVerified(true);
            players.persist(knockoutPlayers[i]);
        }

        Match[] quarterFinal = new Match[4];
        for (int i = 0; i < 4; i++) {
            quarterFinal[i] = createMatch(c1, comp2);
            quarterFinal[i].setPlayerA(knockoutPlayers[2*i]);
            quarterFinal[i].setPlayerB(knockoutPlayers[2*i+1]);
            matches.persist(quarterFinal[i]);
        }
        Match[] semiFinal = new Match[2];
        for (int i = 0; i < 2; i++) {
            semiFinal[i] = createMatch(c2, comp2);

            NextMatch next = new NextMatch();
            next.setPreviousA(quarterFinal[2*i]);
            next.setPreviousB(quarterFinal[2*i+1]);
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
        addDefaultCompetitions(tour5, p);

        String[] firstNames = new String[]{"Charlene", "Amado", "Goldie", "Louis", "Carrol", "Everette", "Laverne", "Robt", "Abby", "German", "Katharine", "Vanessa", "Emilio", "Adela", "Mel", "Guy", "Cassandra", "Antonia", "Angie", "Mindy", "Brice", "Deshawn", "Shawna", "Bryant", "Zachariah", "Bernie", "Selena", "Lacey", "Darnell", "Cory", "Socorro", "Belinda", "Stephan", "Korey", "Josefina", "Joe", "Clyde", "Silas", "Moses", "Christian", "Dalton", "Adrienne", "Adan", "Helena", "Luann", "Chuck", "Leona", "Emmanuel", "Debra", "Jose", "Eldridge", "Lynnette", "Tony", "Katie", "Amber", "Carmela", "Paige", "Donn", "Alfred", "Reid", "Kerry", "Lillian", "Angel", "Bryant", "Gaylord", "Deidre", "Derrick", "Allison", "Jordon", "Alfreda", "Colby", "Leticia", "Lee", "Janna", "Ivory", "Hilda", "Lindsey", "Boris", "Stanford", "Johnnie", "Maura", "Joan", "Latisha", "Robin", "Letha", "Mariano", "Hong", "Leanna", "Caleb", "Rickey", "Alta", "Kari", "Rueben", "Hugh", "Charlotte", "Willian", "Edmond", "Trent", "Claudia", "Darla"};
        String[] lastNames = new String[]{"Brennan", "Meyers", "Mora", "Bailey", "Burns", "Trevino", "Horton", "Monroe", "Ali", "Jarvis", "Banks", "Leblanc", "Kidd", "Mooney", "Stout", "Kramer", "Galvan", "Barrett", "Steele", "Morgan", "English", "Pitts", "Fowler", "Vaughan", "Livingston", "May", "Holden", "Sandoval", "Freeman", "Hurley", "Lambert", "Watson", "Huff", "Ruiz", "Santana", "Conway", "Mcintosh", "Bates", "Greene", "Castaneda", "Mcclure", "Kemp", "Hanna", "Merritt", "Mccarthy", "Sampson", "Barnett", "Green", "Ewing", "Floyd", "Lambert", "Farley", "Berry", "Suarez", "Spears", "Martinez", "Cameron", "Ponce", "Roth", "Tate", "Reyes", "Mann", "Bell", "Singleton", "Fischer", "Reilly", "Maxwell", "Montoya", "Serrano", "Morrison", "Patrick", "Evans", "Crawford", "Shannon", "Calhoun", "Kemp", "Vasquez", "Dorsey", "Mills", "Bright", "Rivers", "Huffman", "Wiley", "Daugherty", "Mcintyre", "Mcmahon", "Boone", "Walls", "Campos", "Morton", "Avila", "Church", "Watkins", "Keller", "Klein", "Valentine", "Bartlett", "Chaney", "Wall", "Howard"};

        Random rn = new Random();
        for (int i = 0; i < 500; i++) {
            String fn = firstNames[rn.nextInt(firstNames.length)];
            String ln = lastNames[rn.nextInt(lastNames.length)];
            Player pl = new Player();
            pl.setFirstName(fn);
            pl.setLastName(ln);
            pl.setBirthday(LocalDate.now());
            pl.setSex(SexType.male);
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
        comp1.setPlayers(List.of(p));
        competitions.persist(comp1);

        Competition comp2 = new Competition();
        comp2.setName("Herren");
        comp2.setTournament(t);
        comp2.setDescription("Herren-Konkurrenz");
        comp2.setType(CompetitionType.KNOCKOUT);
        comp2.setPlayers(List.of(p));
        competitions.persist(comp2);
    }
}

