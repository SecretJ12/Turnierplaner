package de.secretj12.turnierplaner.resources;

import de.secretj12.turnierplaner.db.entities.Tournament;
import de.secretj12.turnierplaner.db.repositories.TournamentRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class TestCompetitionResource {

    @Inject
    TournamentRepository tournaments;

    @BeforeEach
    @Transactional
    public void addData() {
        Tournament tour = new Tournament();
        tour.setName("Clubmeisterschaft 2021");
        tour.setDescription("Anmeldung ausstehend");
        tour.setBeginRegistration(LocalDateTime.now().plusDays(10));
        tour.setEndRegistration(LocalDateTime.now().plusDays(11));
        tour.setBeginGamePhase(LocalDateTime.now().plusDays(12));
        tour.setEndGamePhase(LocalDateTime.now().plusDays(13));
        tour.setVisible(true);
        tournaments.persist(tour);
    }

    @AfterEach
    @Transactional
    public void delete() {
        tournaments.deleteAll();
    }

    @Test
    public void testCanCreateUnauthorized() {
        given()
            .get("/tournament/Clubmeisterschaft 2021/competition/canEdit")
            .then().assertThat()
            .statusCode(Response.Status.OK.getStatusCode())
            .body(is("false"));
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"user", "reporter", "director"})
    public void testCanCreateAuthorized() {
        given()
            .get("/tournament/Clubmeisterschaft 2021/competition/canEdit")
            .then().assertThat()
            .statusCode(Response.Status.OK.getStatusCode())
            .body(is("true"));
    }

    // TODO Tests
}
