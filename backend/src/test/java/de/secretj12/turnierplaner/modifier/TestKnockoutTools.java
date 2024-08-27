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

import java.time.Instant;
import java.time.temporal.ChronoUnit;

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
        tour.setBeginRegistration(Instant.now().minus(10, ChronoUnit.DAYS));
        tour.setEndRegistration(Instant.now().minus(5, ChronoUnit.DAYS));
        tour.setBeginGamePhase(Instant.now().plus(5, ChronoUnit.DAYS));
        tour.setEndGamePhase(Instant.now().plus(10, ChronoUnit.DAYS));
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
