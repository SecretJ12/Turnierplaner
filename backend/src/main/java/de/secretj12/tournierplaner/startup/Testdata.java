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
        tour1.setDescription("Anmeldung noch bis zum 2.1.");
        tour1.setBeginRegistration(LocalDateTime.of(2022, 1, 1, 12, 12));
        tour1.setEndRegistration(LocalDateTime.of(2022, 1, 2, 12, 12));
        tour1.setBeginGamePhase(LocalDateTime.of(2022, 2, 1, 12, 12));
        tour1.setEndGamePhase(LocalDateTime.of(2022, 2, 2, 12, 12));
        tour1.setVisible(true);
        tournaments.persist(tour1);


        Competition comp1 = new Competition();
        Competition.CompetitionKey comp1Key = new Competition.CompetitionKey();
        comp1Key.setName("Damen");
        comp1Key.setTournament(tour1);
        comp1.setKey(comp1Key);
        comp1.setDescription("Damen-Konkurrenz");
        comp1.setType(CompetitionType.GROUPS);
        competitions.persist(comp1);

        Competition comp2 = new Competition();
        Competition.CompetitionKey comp2Key = new Competition.CompetitionKey();
        comp2Key.setName("Herren");
        comp2Key.setTournament(tour1);
        comp2.setKey(comp2Key);
        comp2.setDescription("Herren-Konkurrenz");
        comp2.setType(CompetitionType.KNOCKOUT);
        competitions.persist(comp2);
    }
}
