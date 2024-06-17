package de.secretj12.turnierplaner.resources;

import de.secretj12.turnierplaner.db.entities.Tournament;
import de.secretj12.turnierplaner.db.entities.competition.*;
import de.secretj12.turnierplaner.db.repositories.CompetitionRepository;
import de.secretj12.turnierplaner.db.repositories.TournamentRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static de.secretj12.turnierplaner.db.entities.competition.CreationProgress.PLAYER;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class TestCompetitionResource {

    @Inject
    TournamentRepository tournaments;
    @Inject
    CompetitionRepository competitions;

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
        Tournament tour2 = new Tournament();
        tour2.setName("Clubmeisterschaft 2022");
        tour2.setDescription("Anmeldung ausstehend");
        tour2.setBeginRegistration(LocalDateTime.now().plusDays(10));
        tour2.setEndRegistration(LocalDateTime.now().plusDays(11));
        tour2.setBeginGamePhase(LocalDateTime.now().plusDays(12));
        tour2.setEndGamePhase(LocalDateTime.now().plusDays(13));
        tour2.setVisible(false);
        tournaments.persist(tour2);

        Competition comp = new Competition();
        comp.setName("Herren");
        comp.setDescription("Offen für alle Herren");
        comp.setMode(CompetitionMode.SINGLES);
        comp.setType(CompetitionType.KNOCKOUT);
        comp.setPlayerASex(Sex.MALE);
        comp.setcProgress(PLAYER);
        comp.setSignup(CompetitionSignUp.INDIVIDUAL);
        comp.setPlayerASex(Sex.ANY);
        comp.setTournament(tour);
        competitions.persist(comp);
        Competition comp2 = new Competition();
        comp2.setName("Damen");
        comp2.setDescription("Offen für alle Damen");
        comp2.setMode(CompetitionMode.SINGLES);
        comp2.setType(CompetitionType.KNOCKOUT);
        comp2.setPlayerASex(Sex.FEMALE);
        comp2.setcProgress(PLAYER);
        comp2.setSignup(CompetitionSignUp.INDIVIDUAL);
        comp2.setPlayerASex(Sex.ANY);
        comp2.setTournament(tour2);
        competitions.persist(comp2);
    }

    @AfterEach
    @Transactional
    public void clearData() {
        competitions.deleteAll();
        tournaments.deleteAll();
    }

    @Test
    public void getAllCompetitonsUnauthorized() {
        given()
            .get("/tournament/Clubmeisterschaft 2021/competition/list")
            .then()
            .assertThat()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body("$.size()", is(1),
                  "[0].name", is("Herren"));
    }

    @Test
    public void getAllCompetitonsUnauthorizedInvisible() {
        given()
            .get("/tournament/Clubmeisterschaft 2022/competition/list")
            .then()
            .assertThat()
            .statusCode(401)
            .body(not(contains("Damen")));
    }

    @Test
    @TestSecurity(user = "testuser", roles = "director")
    public void getAllCompetitonsAuthorized() {
        given()
            .get("/tournament/Clubmeisterschaft 2022/competition/list")
            .then()
            .assertThat()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body("$.size()", is(1),
                  "[0].name", is("Damen"));
    }

    @Test
    public void getPrepareCompetitionsUnauthorized() {
        given()
            .get("/tournament/Clubmeisterschaft 2021/competition/prepare")
            .then()
            .assertThat()
            .statusCode(401);
    }

    @Test
    @TestSecurity(user = "testuser", roles = "director")
    public void getPrepareCompetitions() {
        given()
            .get("/tournament/Clubmeisterschaft 2021/competition/prepare")
            .then()
            .assertThat()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body("$.size()", is(1),
                  "[0].name", is("Herren"),
                  "[0].id", is(competitions.getByName("Clubmeisterschaft 2021", "Herren").getId().toString()));
    }

    @Test
    @TestSecurity(user = "testuser", roles = "director")
    public void getCompetition() {
        given()
            .get("/tournament/Clubmeisterschaft 2021/competition/Herren/details")
            .then()
            .assertThat()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body("name", is("Herren"),
                  "id", notNullValue());
    }

    @Test
    public void getCompetitionUnauthorized() {
        given()
            .get("/tournament/Clubmeisterschaft 2021/competition/Herren/details")
            .then()
            .assertThat()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body("name", is("Herren"),
                  "id", nullValue());
    }

    @Test
    public void testCanEditUnauthorized() {
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

    @Test
    public void addTournamentUnauthorized() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                      {
                          "name": "Damen",
                          "description": "Für alle Damen",
                          "type": "KNOCKOUT",
                          "mode": "SINGLE",
                          "signUp": "INDIVIDUAL",
                          "playerA": {
                            "sex": "FEMALE",
                            "hasMinAge": false,
                            "hasMaxAge": false
                          },
                          "playerB": {
                            "different": false,
                            "sex": "ANY",
                            "hasMinAge": false,
                            "hasMaxAge": false
                          }
                      }
                      """)
            .post("/tournament/Clubmeisterschaft 2021/competition/add")
            .then().assertThat()
            .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    @TestSecurity(user = "testuser", roles = "director")
    public void addTournament() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                      {
                          "name": "Damen",
                          "description": "Für alle Damen",
                          "type": "KNOCKOUT",
                          "mode": "SINGLE",
                          "signUp": "INDIVIDUAL",
                          "playerA": {
                            "sex": "FEMALE",
                            "hasMinAge": false,
                            "hasMaxAge": false
                          },
                          "playerB": {
                            "different": false,
                            "sex": "ANY",
                            "hasMinAge": false,
                            "hasMaxAge": false
                          }
                      }
                      """)
            .post("/tournament/Clubmeisterschaft 2021/competition/add")
            .then().assertThat()
            .statusCode(200);
    }

    @Test
    @TestSecurity(user = "testuser", roles = "director")
    public void addTournamentBadName() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                      {
                          "description": "Für alle Damen",
                          "type": "KNOCKOUT",
                          "mode": "SINGLE",
                          "signUp": "INDIVIDUAL",
                          "playerA": {
                            "sex": "FEMALE",
                            "hasMinAge": false,
                            "hasMaxAge": false
                          },
                          "playerB": {
                            "different": false,
                            "sex": "ANY",
                            "hasMinAge": false,
                            "hasMaxAge": false
                          }
                      }
                      """)
            .post("/tournament/Clubmeisterschaft 2021/competition/add")
            .then().assertThat()
            .statusCode(400);
    }

    @Test
    @TestSecurity(user = "testuser", roles = "director")
    public void addTournamentConflict() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                      {
                          "name": "Herren",
                          "description": "Für alle Damen",
                          "type": "KNOCKOUT",
                          "mode": "SINGLE",
                          "signUp": "INDIVIDUAL",
                          "playerA": {
                            "sex": "FEMALE",
                            "hasMinAge": false,
                            "hasMaxAge": false
                          },
                          "playerB": {
                            "different": false,
                            "sex": "ANY",
                            "hasMinAge": false,
                            "hasMaxAge": false
                          }
                      }
                      """)
            .post("/tournament/Clubmeisterschaft 2021/competition/add")
            .then().assertThat()
            .statusCode(400);
    }

    @Test
    @TestSecurity(user = "testuser", roles = "director")
    public void addTournamentInvalidAge() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                      {
                          "name": "Damen",
                          "description": "Für alle Damen",
                          "type": "KNOCKOUT",
                          "mode": "SINGLE",
                          "signUp": "INDIVIDUAL",
                          "playerA": {
                            "sex": "FEMALE",
                            "hasMinAge": true,
                            "hasMaxAge": false
                          },
                          "playerB": {
                            "different": false,
                            "sex": "ANY",
                            "hasMinAge": false,
                            "hasMaxAge": false
                          }
                      }
                      """)
            .post("/tournament/Clubmeisterschaft 2021/competition/add")
            .then().assertThat()
            .statusCode(400);
        given()
            .contentType(ContentType.JSON)
            .body("""
                      {
                          "name": "Damen",
                          "description": "Für alle Damen",
                          "type": "KNOCKOUT",
                          "mode": "SINGLE",
                          "signUp": "INDIVIDUAL",
                          "playerA": {
                            "sex": "FEMALE",
                            "hasMinAge": false,
                            "hasMaxAge": true
                          },
                          "playerB": {
                            "different": false,
                            "sex": "ANY",
                            "hasMinAge": false,
                            "hasMaxAge": false
                          }
                      }
                      """)
            .post("/tournament/Clubmeisterschaft 2021/competition/add")
            .then().assertThat()
            .statusCode(400);
        given()
            .contentType(ContentType.JSON)
            .body("""
                      {
                          "name": "Damen",
                          "description": "Für alle Damen",
                          "type": "KNOCKOUT",
                          "mode": "SINGLE",
                          "signUp": "INDIVIDUAL",
                          "playerA": {
                            "sex": "FEMALE",
                            "hasMinAge": false,
                            "hasMaxAge": false
                          },
                          "playerB": {
                            "different": false,
                            "sex": "ANY",
                            "hasMinAge": true,
                            "hasMaxAge": false
                          }
                      }
                      """)
            .post("/tournament/Clubmeisterschaft 2021/competition/add")
            .then().assertThat()
            .statusCode(400);
        given()
            .contentType(ContentType.JSON)
            .body("""
                      {
                          "name": "Damen",
                          "description": "Für alle Damen",
                          "type": "KNOCKOUT",
                          "mode": "SINGLE",
                          "signUp": "INDIVIDUAL",
                          "playerA": {
                            "sex": "FEMALE",
                            "hasMinAge": false,
                            "hasMaxAge": false
                          },
                          "playerB": {
                            "different": false,
                            "sex": "ANY",
                            "hasMinAge": false,
                            "hasMaxAge": true
                          }
                      }
                      """)
            .post("/tournament/Clubmeisterschaft 2021/competition/add")
            .then().assertThat()
            .statusCode(400);
    }

    @Test
    @TestSecurity(user = "testuser", roles = "director")
    public void updateTournament() {
        given()
            .contentType(ContentType.JSON)
            .body(String.format("""
                                    {
                                        "id": "%s",
                                        "name": "Damen2",
                                        "description": "Für alle Damen",
                                        "type": "KNOCKOUT",
                                        "mode": "SINGLE",
                                        "signUp": "INDIVIDUAL",
                                        "playerA": {
                                          "sex": "FEMALE",
                                          "hasMinAge": false,
                                          "hasMaxAge": false
                                        },
                                        "playerB": {
                                          "different": false,
                                          "sex": "ANY",
                                          "hasMinAge": false,
                                          "hasMaxAge": false
                                        }
                                    }
                                    """, competitions
                                    .getByName("Clubmeisterschaft 2021", "Herren")
                                    .getId().toString()))
            .post("/tournament/Clubmeisterschaft 2021/competition/update")
            .then().assertThat()
            .statusCode(200);
    }

    @Test
    public void updateTournamentUnauthorized() {
        given()
            .contentType(ContentType.JSON)
            .body(String.format("""
                                    {
                                        "id": "%s",
                                        "name": "Damen2",
                                        "description": "Für alle Damen",
                                        "type": "KNOCKOUT",
                                        "mode": "SINGLE",
                                        "signUp": "INDIVIDUAL",
                                        "playerA": {
                                          "sex": "FEMALE",
                                          "hasMinAge": false,
                                          "hasMaxAge": false
                                        },
                                        "playerB": {
                                          "different": false,
                                          "sex": "ANY",
                                          "hasMinAge": false,
                                          "hasMaxAge": false
                                        }
                                    }
                                    """, competitions
                .getByName("Clubmeisterschaft 2021", "Herren")
                .getId().toString()))
            .post("/tournament/Clubmeisterschaft 2021/competition/update")
            .then().assertThat()
            .statusCode(401);
    }

    @Test
    @TestSecurity(user = "testuser", roles = "director")
    public void updateTournamentWrongId() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                      {
                          "id": "dummyid",
                          "name": "Damen2",
                          "description": "Für alle Damen",
                          "type": "KNOCKOUT",
                          "mode": "SINGLE",
                          "signUp": "INDIVIDUAL",
                          "playerA": {
                            "sex": "FEMALE",
                            "hasMinAge": false,
                            "hasMaxAge": false
                          },
                          "playerB": {
                            "different": false,
                            "sex": "ANY",
                            "hasMinAge": false,
                            "hasMaxAge": false
                          }
                      }
                      """)
            .post("/tournament/Clubmeisterschaft 2021/competition/update")
            .then().assertThat()
            .statusCode(400);
    }

    @Test
    @TestSecurity(user = "testuser", roles = "director")
    public void updateTournamentWrongTournament() {
        given()
            .contentType(ContentType.JSON)
            .body(String.format("""
                                    {
                                        "id": "%s",
                                        "name": "Damen2",
                                        "description": "Für alle Damen",
                                        "type": "KNOCKOUT",
                                        "mode": "SINGLE",
                                        "signUp": "INDIVIDUAL",
                                        "playerA": {
                                          "sex": "FEMALE",
                                          "hasMinAge": false,
                                          "hasMaxAge": false
                                        },
                                        "playerB": {
                                          "different": false,
                                          "sex": "ANY",
                                          "hasMinAge": false,
                                          "hasMaxAge": false
                                        }
                                    }
                                    """, competitions
                .getByName("Clubmeisterschaft 2021", "Herren")
                .getId().toString()))
            .post("/tournament/Clubmeisterschaft 2022/competition/update")
            .then().assertThat()
            .statusCode(400);
    }



    // TODO Tests
}
