package de.secretj12.tournierplaner.startup;

import de.secretj12.tournierplaner.entities.*;
import de.secretj12.tournierplaner.repositories.CompetitionRepository;
import de.secretj12.tournierplaner.repositories.PlayerRepository;
import de.secretj12.tournierplaner.repositories.TournamentRepository;

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

    public Testdata() {

    }

    @Transactional
    public void createData() {
        Tournament tour1 = new Tournament();
        tour1.setName("Clubmeisterschaft 2022");
        tour1.setDescription("Anmeldung geschlossen");
        tour1.setBeginRegistration(LocalDateTime.of(2022, 1, 1, 12, 12));
        tour1.setEndRegistration(LocalDateTime.of(2022, 1, 2, 12, 12));
        tour1.setBeginGamePhase(LocalDateTime.of(2022, 2, 1, 12, 12));
        tour1.setEndGamePhase(LocalDateTime.of(2022, 2, 2, 12, 12));
        tour1.setVisible(true);
        tournaments.persist(tour1);


        Competition comp1 = new Competition();
        comp1.setName("Damen");
        comp1.setTournament(tour1);
        comp1.setDescription("Damen-Konkurrenz");
        comp1.setType(CompetitionType.GROUPS);
        competitions.persist(comp1);

        Competition comp2 = new Competition();
        comp2.setName("Herren");
        comp2.setTournament(tour1);
        comp2.setDescription("Herren-Konkurrenz");
        comp2.setType(CompetitionType.KNOCKOUT);
        competitions.persist(comp2);

        Tournament tour2 = new Tournament();
        tour2.setName("Clubmeisterschaft 2023");
        tour2.setDescription("Anmeldung offen");
        tour2.setBeginRegistration(LocalDateTime.now().minusDays(1));
        tour2.setEndRegistration(LocalDateTime.now().plusDays(1));
        tour2.setBeginGamePhase(LocalDateTime.now().plusDays(2));
        tour2.setEndGamePhase(LocalDateTime.now().plusDays(3));
        tour2.setVisible(false);
        tournaments.persist(tour2);

        Competition comp3 = new Competition();
        comp3.setName("Damen");
        comp3.setTournament(tour2);
        comp3.setDescription("Damen-Konkurrenz");
        comp3.setType(CompetitionType.GROUPS);
        competitions.persist(comp3);

        Competition comp4 = new Competition();
        comp4.setName("Herren");
        comp4.setTournament(tour2);
        comp4.setDescription("Herren-Konkurrenz");
        comp4.setType(CompetitionType.KNOCKOUT);
        competitions.persist(comp4);

        // too good to be left out
        Player p1 = new Player();
        p1.setFirstName("Rainer");
        p1.setLastName("Zufall");
        p1.setBirthday(LocalDate.now());
        p1.setSex(SexType.male);
        p1.setEmail("abc@def.ghi");
        p1.setPhone("+49 123 456789");
        p1.setMailVerified(true);
        p1.setAdminVerified(true);
        players.persist(p1);
        comp4.setPlayers(List.of(p1));
        competitions.persist(comp4);

        String[] firstNames = new String[]{"Charlene", "Amado", "Goldie", "Louis", "Carrol", "Everette", "Laverne", "Robt", "Abby", "German", "Katharine", "Vanessa", "Emilio", "Adela", "Mel", "Guy", "Cassandra", "Antonia", "Angie", "Mindy", "Brice", "Deshawn", "Shawna", "Bryant", "Zachariah", "Bernie", "Selena", "Lacey", "Darnell", "Cory", "Socorro", "Belinda", "Stephan", "Korey", "Josefina", "Joe", "Clyde", "Silas", "Moses", "Christian", "Dalton", "Adrienne", "Adan", "Helena", "Luann", "Chuck", "Leona", "Emmanuel", "Debra", "Jose", "Eldridge", "Lynnette", "Tony", "Katie", "Amber", "Carmela", "Paige", "Donn", "Alfred", "Reid", "Kerry", "Lillian", "Angel", "Bryant", "Gaylord", "Deidre", "Derrick", "Allison", "Jordon", "Alfreda", "Colby", "Leticia", "Lee", "Janna", "Ivory", "Hilda", "Lindsey", "Boris", "Stanford", "Johnnie", "Maura", "Joan", "Latisha", "Robin", "Letha", "Mariano", "Hong", "Leanna", "Caleb", "Rickey", "Alta", "Kari", "Rueben", "Hugh", "Charlotte", "Willian", "Edmond", "Trent", "Claudia", "Darla"};
        String[] lastNames = new String[]{"Brennan", "Meyers", "Mora", "Bailey", "Burns", "Trevino", "Horton", "Monroe", "Ali", "Jarvis", "Banks", "Leblanc", "Kidd", "Mooney", "Stout", "Kramer", "Galvan", "Barrett", "Steele", "Morgan", "English", "Pitts", "Fowler", "Vaughan", "Livingston", "May", "Holden", "Sandoval", "Freeman", "Hurley", "Lambert", "Watson", "Huff", "Ruiz", "Santana", "Conway", "Mcintosh", "Bates", "Greene", "Castaneda", "Mcclure", "Kemp", "Hanna", "Merritt", "Mccarthy", "Sampson", "Barnett", "Green", "Ewing", "Floyd", "Lambert", "Farley", "Berry", "Suarez", "Spears", "Martinez", "Cameron", "Ponce", "Roth", "Tate", "Reyes", "Mann", "Bell", "Singleton", "Fischer", "Reilly", "Maxwell", "Montoya", "Serrano", "Morrison", "Patrick", "Evans", "Crawford", "Shannon", "Calhoun", "Kemp", "Vasquez", "Dorsey", "Mills", "Bright", "Rivers", "Huffman", "Wiley", "Daugherty", "Mcintyre", "Mcmahon", "Boone", "Walls", "Campos", "Morton", "Avila", "Church", "Watkins", "Keller", "Klein", "Valentine", "Bartlett", "Chaney", "Wall", "Howard"};

        Random rn = new Random();
        for (int i = 0; i < 500; i++) {
            String fn = firstNames[rn.nextInt(firstNames.length)];
            String ln = lastNames[rn.nextInt(lastNames.length)];
            Player p = new Player();
            p.setFirstName(fn);
            p.setLastName(ln);
            p.setBirthday(LocalDate.now());
            p.setSex(SexType.male);
            p.setEmail(fn + "." + ln + "@mail.de");
            p.setPhone("+49 123 456789");
            p.setMailVerified(true);
            p.setAdminVerified(true);
            players.persist(p);
        }
    }
}

