package de.secretj12.turnierplaner.resources;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.secretj12.turnierplaner.db.entities.Court;
import de.secretj12.turnierplaner.db.entities.Tournament;
import de.secretj12.turnierplaner.db.repositories.CourtRepositiory;
import de.secretj12.turnierplaner.db.repositories.TournamentRepository;
import de.secretj12.turnierplaner.enums.CourtType;
import de.secretj12.turnierplaner.model.director.jDirectorTournamentUpdate;
import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class TestTournamentResource {

  @Inject TournamentRepository tournaments;
  @Inject CourtRepositiory courts;

  @BeforeEach
  @Transactional
  public void addData() {
    Tournament tour = new Tournament();
    tour.setName("Clubmeisterschaft");
    tour.setDescription("Anmeldung ausstehend");
    tour.setBeginRegistration(Instant.now().plus(10, ChronoUnit.DAYS));
    tour.setEndRegistration(Instant.now().plus(11, ChronoUnit.DAYS));
    tour.setBeginGamePhase(Instant.now().plus(12, ChronoUnit.DAYS));
    tour.setEndGamePhase(Instant.now().plus(13, ChronoUnit.DAYS));
    tour.setVisible(true);
    tournaments.persist(tour);
    Tournament tour2 = new Tournament();
    tour2.setName("Clubmeisterschaft2");
    tour2.setDescription("Anmeldung ausstehend");
    tour2.setBeginRegistration(Instant.now().plus(10, ChronoUnit.DAYS));
    tour2.setEndRegistration(Instant.now().plus(11, ChronoUnit.DAYS));
    tour2.setBeginGamePhase(Instant.now().plus(12, ChronoUnit.DAYS));
    tour2.setEndGamePhase(Instant.now().plus(13, ChronoUnit.DAYS));
    tour2.setVisible(false);
    tournaments.persist(tour2);

    for (int i = 0; i < 5; i++) {
      Court court = new Court();
      court.setCourtType(CourtType.CLAY);
      court.setName("Court " + i);
      courts.persist(court);
    }
  }

  @AfterEach
  @Transactional
  public void clearData() {
    tournaments.deleteAll();
    courts.deleteAll();
  }

  @Test
  public void getAllTournamentsUnauthorized() {
    given()
        .get("/tournament/list")
        .then()
        .assertThat()
        .statusCode(200)
        .contentType(MediaType.APPLICATION_JSON)
        .body("$.size()", is(1), "[0].name", is("Clubmeisterschaft"), "[0].visible", nullValue());
  }

  @Test
  @TestSecurity(user = "testuser", roles = "director")
  public void getAllTournamentsAuthorized() {
    given()
        .get("/tournament/list")
        .then()
        .assertThat()
        .body(
            "$.size()",
            is(2),
            "name",
            everyItem(startsWith("Clubmeisterschaft")),
            "visible",
            everyItem(anything()));
  }

  @Test
  public void testCanCreateUnauthorized() {
    given()
        .get("/config/isDirector")
        .then()
        .assertThat()
        .statusCode(Response.Status.OK.getStatusCode())
        .body(is("false"));
  }

  @Test
  @TestSecurity(
      user = "testUser",
      roles = {"director"})
  public void testCanCreateAuthorized() {
    given()
        .get("/config/isDirector")
        .then()
        .statusCode(Response.Status.OK.getStatusCode())
        .body(is("true"));
  }

  @Test
  public void getDetails() {
    given()
        .get("/tournament/Clubmeisterschaft/details")
        .then()
        .assertThat()
        .statusCode(Response.Status.OK.getStatusCode())
        .body("name", is("Clubmeisterschaft"), "visible", nullValue());
  }

  @Test
  @TestSecurity(
      user = "testUser",
      roles = {"director"})
  public void getDetailsAsDirector() {
    given()
        .get("/tournament/Clubmeisterschaft/details")
        .then()
        .assertThat()
        .statusCode(Response.Status.OK.getStatusCode())
        .body("name", is("Clubmeisterschaft"), "visible", anything());
  }

  @Test
  @TestSecurity(
      user = "testUser",
      roles = {"director"})
  public void getDetailsAsDirectorNotFound() {
    given()
        .get("/tournament/ClubmeisterschaftNotExisting/details")
        .then()
        .assertThat()
        .statusCode(Response.Status.NOT_FOUND.getStatusCode());
  }

  @Test
  public void getDetailsAsDirectorInvisible() {
    given()
        .get("/tournament/Clubmeisterschaft2/details")
        .then()
        .assertThat()
        .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
  }

  @Test
  public void addTournamentUnauthorized() {
    given()
        .contentType(ContentType.JSON)
        .body(
            """
            {
                "name": "Clubmeisterschaft 2024",
                "visible": "true",
                "description": "Anmeldephase offen",
                "beginRegistration": "2024-04-01T23:15:30.000Z",
                "endRegistration": "2024-04-02T23:15:30.000Z",
                "beginGamePhase": "2024-04-03T23:15:30.000Z",
                "endGamePhase": "2024-04-04T23:15:30.000Z"
            }
            """)
        .post("/tournament/add")
        .then()
        .assertThat()
        .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
  }

  @Test
  @TestSecurity(
      user = "testUser",
      roles = {"director"})
  public void createTournament() {
    given()
        .contentType(ContentType.JSON)
        .body(
            """
            {
                "name": "Clubmeisterschaft 2024",
                "visible": "true",
                "description": "Anmeldephase offen",
                "beginRegistration": "2024-04-01T23:15:30.000Z",
                "endRegistration": "2024-04-02T23:15:30.000Z",
                "beginGamePhase": "2024-04-03T23:15:30.000Z",
                "endGamePhase": "2024-04-04T23:15:30.000Z"
            }
            """)
        .post("/tournament/add")
        .then()
        .assertThat()
        .statusCode(Response.Status.OK.getStatusCode());

    Tournament tour = tournaments.getByName("Clubmeisterschaft 2024");
    assertNotNull(tour);
    assertEquals(tour.getDescription(), "Anmeldephase offen");
    assertEquals(
        tour.getBeginRegistration(),
        LocalDateTime.of(2024, 4, 1, 23, 15, 30).toInstant(ZoneOffset.UTC));
  }

  @Test
  @TestSecurity(
      user = "testUser",
      roles = {"director"})
  public void createTournamentNoName() {
    given()
        .contentType(ContentType.JSON)
        .body(
            """
            {
                "visible": "true",
                "description": "Anmeldephase offen",
                "beginRegistration": "2024-04-01T23:15:30.000Z",
                "endRegistration": "2024-04-02T23:15:30.000Z",
                "beginGamePhase": "2024-04-03T23:15:30.000Z",
                "endGamePhase": "2024-04-04T23:15:30.000Z"
            }
            """)
        .post("/tournament/add")
        .then()
        .assertThat()
        .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
  }

  @Test
  @TestSecurity(
      user = "testUser",
      roles = {"director"})
  public void createTournamentAlreadyExists() {
    given()
        .contentType(ContentType.JSON)
        .body(
            """
            {
                "name": "Clubmeisterschaft",
                "visible": "true",
                "description": "Anmeldephase offen",
                "beginRegistration": "2024-04-01T23:15:30.000Z",
                "endRegistration": "2024-04-02T23:15:30.000Z",
                "beginGamePhase": "2024-04-03T23:15:30.000Z",
                "endGamePhase": "2024-04-04T23:15:30.000Z"
            }
            """)
        .post("/tournament/add")
        .then()
        .assertThat()
        .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
  }

  @Test
  @TestSecurity(
      user = "testUser",
      roles = {"director"})
  public void createTournamentWrongDates() {
    given()
        .contentType(ContentType.JSON)
        .body(
            """
            {
                "name": "Clubmeisterschaft 2024",
                "visible": "true",
                "description": "Anmeldephase offen",
                "beginRegistration": "2024-04-01T23:15:30.000Z",
                "endRegistration": "2024-03-31T23:15:30.000Z",
                "beginGamePhase": "2024-04-03T23:15:30.000Z",
                "endGamePhase": "2024-04-04T23:15:30.000Z"
            }
            """)
        .post("/tournament/add")
        .then()
        .assertThat()
        .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

    given()
        .contentType(ContentType.JSON)
        .body(
            """
            {
                "name": "Clubmeisterschaft 2024",
                "visible": "true",
                "description": "Anmeldephase offen",
                "beginRegistration": "2024-04-01T23:15:30.000Z",
                "endRegistration": "2024-04-03T23:30:30.000Z",
                "beginGamePhase": "2024-04-03T23:15:30.000Z",
                "endGamePhase": "2024-04-04T23:15:30.000Z"
            }
            """)
        .post("/tournament/add")
        .then()
        .assertThat()
        .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

    given()
        .contentType(ContentType.JSON)
        .body(
            """
            {
                "name": "Clubmeisterschaft 2024",
                "visible": "true",
                "description": "Anmeldephase offen",
                "beginRegistration": "2024-04-01T23:15:30.000Z",
                "endRegistration": "2024-04-02T23:15:30.000Z",
                "beginGamePhase": "2024-04-05T23:15:30.000Z",
                "endGamePhase": "2024-04-04T23:15:30.000Z"
            }
            """)
        .post("/tournament/add")
        .then()
        .assertThat()
        .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
  }

  @Test
  @TestSecurity(
      user = "testUser",
      roles = {"director"})
  public void updateTournament() {
    UUID id =
        given()
            .get("/tournament/Clubmeisterschaft/details")
            .then()
            .extract()
            .as(jDirectorTournamentUpdate.class)
            .getId();

    given()
        .contentType(ContentType.JSON)
        .body(
            """
            {
                "id": "%s",
                "name": "Clubmeisterschaft 2024",
                "visible": "true",
                "description": "Anmeldephase offen",
                "beginRegistration": "2024-04-01T23:15:30.000Z",
                "endRegistration": "2024-04-02T23:15:30.000Z",
                "beginGamePhase": "2024-04-03T23:15:30.000Z",
                "endGamePhase": "2024-04-04T23:15:30.000Z"
            }
            """
                .formatted(id))
        .post("/tournament/update")
        .then()
        .assertThat()
        .statusCode(Response.Status.OK.getStatusCode());

    Tournament tour = tournaments.getByName("Clubmeisterschaft 2024");
    assertNotNull(tour);
    assertEquals(tour.getDescription(), "Anmeldephase offen");
    assertEquals(
        tour.getBeginRegistration(),
        LocalDateTime.of(2024, 4, 1, 23, 15, 30).toInstant(ZoneOffset.UTC));
  }

  @Test
  @TestSecurity(
      user = "testUser",
      roles = {"director"})
  public void updateTournamentDuplicate() {
    UUID id =
        given()
            .get("/tournament/Clubmeisterschaft/details")
            .then()
            .extract()
            .as(jDirectorTournamentUpdate.class)
            .getId();

    given()
        .contentType(ContentType.JSON)
        .body(
            """
            {
                "id": "%s",
                "name": "Clubmeisterschaft2",
                "visible": "true",
                "description": "Anmeldephase offen",
                "beginRegistration": "2024-04-01T23:15:30.000Z",
                "endRegistration": "2024-04-02T23:15:30.000Z",
                "beginGamePhase": "2024-04-03T23:15:30.000Z",
                "endGamePhase": "2024-04-04T23:15:30.000Z"
            }
            """
                .formatted(id))
        .post("/tournament/update")
        .then()
        .assertThat()
        .statusCode(Response.Status.CONFLICT.getStatusCode());
  }

  @Test
  @TestSecurity(
      user = "testUser",
      roles = {"director"})
  public void updateTournamentNotExisting() {
    given()
        .contentType(ContentType.JSON)
        .body(
            """
            {
                "id": "e2db734e-164f-4024-b57f-646fa0282a60",
                "name": "Clubmeisterschaft 2024",
                "visible": "true",
                "description": "Anmeldephase offen",
                "beginRegistration": "2024-04-01T23:15:30.000Z",
                "endRegistration": "2024-04-02T23:15:30.000Z",
                "beginGamePhase": "2024-04-03T23:15:30.000Z",
                "endGamePhase": "2024-04-04T23:15:30.000Z"
            }
            """)
        .post("/tournament/update")
        .then()
        .assertThat()
        .statusCode(Response.Status.NOT_FOUND.getStatusCode());
  }

  @Test
  @TestSecurity(
      user = "testUser",
      roles = {"director"})
  public void updateCourts()
      throws SystemException,
          NotSupportedException,
          HeuristicRollbackException,
          HeuristicMixedException,
          RollbackException {
    given()
        .contentType(ContentType.JSON)
        .body(
            """
            [
              {
                "name": "Court 1"
              },
              {
                "name": "Court 2"
              }
            ]
            """)
        .post("/tournament/Clubmeisterschaft/updateCourts")
        .then()
        .assertThat()
        .statusCode(Response.Status.OK.getStatusCode());

    Panache.getTransactionManager().begin();
    Tournament tournament = tournaments.getByName("Clubmeisterschaft");
    assertEquals(tournament.getCourts().size(), 2);
    assertEquals(
        tournament.getCourts(), Set.of(courts.findByName("Court 1"), courts.findByName("Court 2")));
    Panache.getTransactionManager().commit();
  }

  @Test
  public void updateCourtsUnauthorized() {
    given()
        .contentType(ContentType.JSON)
        .body(
            """
            [
              {
                "name": "Court 1"
              },
              {
                "name": "Court 2"
              }
            ]
            """)
        .post("/tournament/Clubmeisterschaft/updateCourts")
        .then()
        .assertThat()
        .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
  }

  @Test
  @TestSecurity(
      user = "testUser",
      roles = {"director"})
  public void updateCourtsUnkownCourt() {
    given()
        .contentType(ContentType.JSON)
        .body(
            """
            [
              {
                "name": "Court 7"
              },
              {
                "name": "Court 2"
              }
            ]
            """)
        .post("/tournament/Clubmeisterschaft/updateCourts")
        .then()
        .assertThat()
        .statusCode(Response.Status.NOT_FOUND.getStatusCode());
  }

  @Test
  @TestSecurity(
      user = "testUser",
      roles = {"director"})
  public void updateCourtsUnknownTour() {
    given()
        .contentType(ContentType.JSON)
        .body(
            """
            [
              {
                "name": "Court 7"
              },
              {
                "name": "Court 2"
              }
            ]
            """)
        .post("/tournament/Clubmeisterschaft2/updateCourts")
        .then()
        .assertThat()
        .statusCode(Response.Status.NOT_FOUND.getStatusCode());
  }

  @Test
  public void getCourts()
      throws SystemException,
          NotSupportedException,
          HeuristicRollbackException,
          HeuristicMixedException,
          RollbackException {
    Panache.getTransactionManager().begin();
    Tournament tournament = tournaments.getByName("Clubmeisterschaft");
    tournament.setCourts(Set.of(courts.findByName("Court 1"), courts.findByName("Court 2")));
    tournaments.persist(tournament);
    Panache.getTransactionManager().commit();

    given()
        .get("/tournament/Clubmeisterschaft/courts")
        .then()
        .assertThat()
        .statusCode(Response.Status.OK.getStatusCode())
        .body("size()", is(2))
        .body("[0].name", is(oneOf("Court 1", "Court 2")))
        .body("[1].name", is(oneOf("Court 1", "Court 2")));
  }

  @Test
  public void getCourtsUnknownTour() {
    given()
        .get("/tournament/Random Clubmeisterschaft/courts")
        .then()
        .assertThat()
        .statusCode(Response.Status.NOT_FOUND.getStatusCode());
  }
}
