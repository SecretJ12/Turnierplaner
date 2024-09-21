package de.secretj12.turnierplaner.model;

import de.secretj12.turnierplaner.db.entities.Match;
import de.secretj12.turnierplaner.db.entities.Tournament;
import de.secretj12.turnierplaner.db.entities.competition.*;
import de.secretj12.turnierplaner.db.entities.knockout.NextMatch;
import de.secretj12.turnierplaner.db.repositories.*;
import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class TestDeletionBehaviour {

    @Inject
    TournamentRepository tournamentRepository;
    @Inject
    CompetitionRepository competitionRepository;
    @Inject
    MatchRepository matchRepository;
    @Inject
    NextMatchRepository nextMatchRepository;
    @Inject
    FinalOfGroupRepository finalOfGroupRepository;
    @Inject
    GroupRepository groupRepository;

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
        comp.setDescription("Empty description");
        comp.setMode(CompetitionMode.DOUBLES);
        comp.setType(CompetitionType.GROUPS);
        comp.setSignup(CompetitionSignUp.TOGETHER);
        comp.setPlayerASex(Sex.MALE);
        comp.setTournament(tour);
        comp.setcProgress(CreationProgress.GAMES);
        competitionRepository.persist(comp);
    }

    @AfterEach
    @Transactional
    public void deleteData() {
        matchRepository.deleteAll();
        competitionRepository.deleteAll();
        tournamentRepository.deleteAll();
    }

    @Test
    @Transactional
    public void nextDeletion() {
        Competition competition = competitionRepository.getByName("Clubmeisterschaft", "Herren");

        Match f = new Match();
        f.setCompetition(competition);
        matchRepository.persist(f);
        Match a = new Match();
        a.setCompetition(competition);
        matchRepository.persist(a);
        Match b = new Match();
        b.setCompetition(competition);
        matchRepository.persist(b);
        competition.setFinale(f);
        competitionRepository.persist(competition);

        NextMatch nm = new NextMatch();
        nm.setNextMatch(f);
        nm.setPreviousA(a);
        nm.setPreviousB(b);
        nextMatchRepository.persist(nm);

        Panache.getEntityManager().flush();
        Panache.getEntityManager().clear();

        assertEquals(3, matchRepository.listAll().size());
        competition = competitionRepository.getByName("Clubmeisterschaft", "Herren");
        matchRepository.delete(competition.getFinale().getDependentOn().getPreviousA());
        competition.setFinale(competition.getFinale().getDependentOn().getPreviousB());
        competitionRepository.persist(competition);

        Panache.getEntityManager().flush();
        Panache.getEntityManager().clear();

        assertEquals(1, matchRepository.listAll().size());
    }
}
