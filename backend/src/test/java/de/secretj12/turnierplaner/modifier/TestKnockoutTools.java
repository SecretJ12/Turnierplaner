package de.secretj12.turnierplaner.modifier;

import de.secretj12.turnierplaner.db.entities.Player;
import de.secretj12.turnierplaner.db.entities.Tournament;
import de.secretj12.turnierplaner.db.entities.competition.Competition;
import de.secretj12.turnierplaner.db.entities.competition.CompetitionType;
import de.secretj12.turnierplaner.db.entities.competition.Team;
import de.secretj12.turnierplaner.db.repositories.CompetitionRepository;
import de.secretj12.turnierplaner.db.repositories.PlayerRepository;
import de.secretj12.turnierplaner.db.repositories.TeamRepository;
import de.secretj12.turnierplaner.db.repositories.TournamentRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;

@QuarkusTest
public class TestKnockoutTools {

    @Inject
    TournamentRepository tournamentRepository;
    @Inject
    CompetitionRepository competitionRepository;
    @Inject
    PlayerRepository playerRepository;
    @Inject
    TeamRepository teamRepository;

    @BeforeEach
    @Transactional
    public void addData() {
        Tournament tour = new Tournament();
        tour.setName("Clubmeisterschaft");
        tour.setDescription("Anmeldung abgeschlossen");
        tour.setBeginRegistration(LocalDateTime.now().minusDays(10));
        tour.setEndRegistration(LocalDateTime.now().minusDays(5));
        tour.setBeginGamePhase(LocalDateTime.now().plusDays(5));
        tour.setEndGamePhase(LocalDateTime.now().plusDays(10));
        tour.setVisible(true);
        tournamentRepository.persist(tour);

        Competition comp = new Competition();
        comp.setName("Herren");
        comp.setType(CompetitionType.KNOCKOUT);
        comp.setTournament(tour);
        competitionRepository.persist(comp);

        for (int i = 0; i < 8; i++) {
            Player pA = new Player();
            pA.setFirstName("firstA");
            pA.setLastName("lastA");
            playerRepository.persist(pA);
            Player pB = new Player();
            pB.setFirstName("firstB");
            pB.setLastName("lastB");
            playerRepository.persist(pB);

            Team team = new Team();
            team.setPlayerA(pA);
            team.setPlayerB(pB);
            team.setCompetition(comp);
            teamRepository.persist(team);
        }
    }

}
