package de.secretj12.turnierplaner.resources;

import de.secretj12.turnierplaner.db.entities.Tournament;
import de.secretj12.turnierplaner.db.repositories.TournamentRepository;
import de.secretj12.turnierplaner.resources.jsonEntities.user.jUserTournament;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class TestTournamentResource {

    @Inject
    TournamentRepository tournamentRepository;

    @BeforeEach
    @Transactional
    public void addDate() {
        Tournament tour = new Tournament();
        tour.setName("Clubmeisterschaft");
        tour.setDescription("Anmeldung ausstehend");
        tour.setBeginRegistration(LocalDateTime.now().plusDays(10));
        tour.setEndRegistration(LocalDateTime.now().plusDays(11));
        tour.setBeginGamePhase(LocalDateTime.now().plusDays(12));
        tour.setEndGamePhase(LocalDateTime.now().plusDays(13));
        tour.setVisible(true);
        tournamentRepository.persist(tour);
    }

    @AfterEach
    @Transactional
    public void clearData() {
        tournamentRepository.deleteAll();
    }

    @Test
    public void getAllTournamentsUnauthorized() {
        given()
            .get("/tournament/list")
            .then()
            .assertThat()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(arrayContaining(hasEntry("name", "Clubmeisterschaft")))
            .body(arrayContaining(not(hasEntry(is("visible"), Matchers.any(boolean.class)))));
    }

    @Test
    public void getAllTournamentsAuthorized() {
        given()
            .get("/tournament/list")
            .then()
            .assertThat()
            .body(arrayContaining(instanceOf(jUserTournament.class)))
            .body(arrayContaining(Matchers.hasEntry("name", "Clubmeisterschaft")));
    }

    @Test
    public void testCanCreateUnauthorized() {
        given()
            .get("/tournament/canCreate")
            .then()
            .assertThat()
            .statusCode(Response.Status.OK.getStatusCode())
            .body(is("false"));
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"user", "reporter", "director"})
    public void testCanCreateAuthorized() {
        given()
            .get("/tournament/canCreate")
            .then()
            .assertThat()
            .statusCode(Response.Status.OK.getStatusCode())
            .body(is("true"));
    }

    // TODO Tests
}
