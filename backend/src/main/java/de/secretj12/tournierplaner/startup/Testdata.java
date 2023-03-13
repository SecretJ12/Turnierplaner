package de.secretj12.tournierplaner.startup;

import de.secretj12.tournierplaner.entities.Competition;
import de.secretj12.tournierplaner.entities.CompetitionType;
import de.secretj12.tournierplaner.entities.Tournament;
import de.secretj12.tournierplaner.repositories.CompetitionRepository;
import de.secretj12.tournierplaner.repositories.TournamentRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

@ApplicationScoped
public class Testdata {

    @Inject
    TournamentRepository tournaments;
    @Inject
    CompetitionRepository competitions;

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
        tour2.setBeginRegistration(LocalDateTime.of(2023, 7, 1, 12, 12));
        tour2.setEndRegistration(LocalDateTime.of(2023, 7, 2, 12, 12));
        tour2.setBeginGamePhase(LocalDateTime.of(2023, 8, 1, 12, 12));
        tour2.setEndGamePhase(LocalDateTime.of(2023, 8, 2, 12, 12));
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
    }
}
