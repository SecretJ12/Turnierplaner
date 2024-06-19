package de.secretj12.turnierplaner.resources;

import de.secretj12.turnierplaner.db.entities.Player;
import de.secretj12.turnierplaner.db.entities.SexType;
import de.secretj12.turnierplaner.db.entities.Tournament;
import de.secretj12.turnierplaner.db.entities.competition.*;
import de.secretj12.turnierplaner.db.repositories.CompetitionRepository;
import de.secretj12.turnierplaner.db.repositories.PlayerRepository;
import de.secretj12.turnierplaner.db.repositories.TeamRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static de.secretj12.turnierplaner.db.entities.competition.CreationProgress.PLAYER;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class TestCompetitionResource {

    @Inject
    TournamentRepository tournaments;
    @Inject
    CompetitionRepository competitions;
    @Inject
    PlayerRepository players;
    @Inject
    TeamRepository teams;

    @BeforeEach
    @Transactional
    public void addData() {
        Tournament tour20 = new Tournament();
        tour20.setName("Clubmeisterschaft 2020");
        tour20.setDescription("Anmeldung ausstehend");
        tour20.setBeginRegistration(LocalDateTime.now().minusDays(20));
        tour20.setEndRegistration(LocalDateTime.now().minusDays(18));
        tour20.setBeginGamePhase(LocalDateTime.now().minusDays(17));
        tour20.setEndGamePhase(LocalDateTime.now().minusDays(15));
        tour20.setVisible(true);
        tournaments.persist(tour20);
        Tournament tour21 = new Tournament();
        tour21.setName("Clubmeisterschaft 2021");
        tour21.setDescription("Anmeldung ausstehend");
        tour21.setBeginRegistration(LocalDateTime.now().minusDays(10));
        tour21.setEndRegistration(LocalDateTime.now().plusDays(11));
        tour21.setBeginGamePhase(LocalDateTime.now().plusDays(12));
        tour21.setEndGamePhase(LocalDateTime.now().plusDays(13));
        tour21.setVisible(true);
        tournaments.persist(tour21);
        Tournament tour22 = new Tournament();
        tour22.setName("Clubmeisterschaft 2022");
        tour22.setDescription("Anmeldung ausstehend");
        tour22.setBeginRegistration(LocalDateTime.now().plusDays(10));
        tour22.setEndRegistration(LocalDateTime.now().plusDays(11));
        tour22.setBeginGamePhase(LocalDateTime.now().plusDays(12));
        tour22.setEndGamePhase(LocalDateTime.now().plusDays(13));
        tour22.setVisible(false);
        tournaments.persist(tour22);

        Competition comp20 = new Competition();
        comp20.setName("Herren");
        comp20.setDescription("Offen für alle Herren");
        comp20.setMode(CompetitionMode.SINGLES);
        comp20.setType(CompetitionType.KNOCKOUT);
        comp20.setPlayerASex(Sex.MALE);
        comp20.setcProgress(PLAYER);
        comp20.setSignup(CompetitionSignUp.INDIVIDUAL);
        comp20.setPlayerASex(Sex.ANY);
        comp20.setTournament(tour20);
        competitions.persist(comp20);
        Competition comp21 = new Competition();
        comp21.setName("Herren");
        comp21.setDescription("Offen für alle Herren");
        comp21.setMode(CompetitionMode.SINGLES);
        comp21.setType(CompetitionType.KNOCKOUT);
        comp21.setPlayerASex(Sex.MALE);
        comp21.setcProgress(PLAYER);
        comp21.setSignup(CompetitionSignUp.INDIVIDUAL);
        comp21.setPlayerASex(Sex.ANY);
        comp21.setTournament(tour21);
        competitions.persist(comp21);
        Competition comp22 = new Competition();
        comp22.setName("Damen");
        comp22.setDescription("Offen für alle Damen");
        comp22.setMode(CompetitionMode.SINGLES);
        comp22.setType(CompetitionType.KNOCKOUT);
        comp22.setPlayerASex(Sex.FEMALE);
        comp22.setcProgress(PLAYER);
        comp22.setSignup(CompetitionSignUp.INDIVIDUAL);
        comp22.setPlayerASex(Sex.ANY);
        comp22.setTournament(tour22);
        competitions.persist(comp22);

        Player playerM = new Player();
        playerM.setFirstName("Rainer");
        playerM.setLastName("Zufall");
        playerM.setSex(SexType.MALE);
        playerM.setBirthday(LocalDate.of(2000, 1, 1));
        players.persist(playerM);
        Player playerF = new Player();
        playerF.setFirstName("Anna");
        playerF.setLastName("Gramm");
        playerF.setSex(SexType.FEMALE);
        playerF.setBirthday(LocalDate.of(2000, 1, 1));
        players.persist(playerF);

        Team team20 = new Team();
        team20.setPlayerA(playerM);
        team20.setCompetition(comp20);
        teams.persist(team20);
        Team team21 = new Team();
        team21.setPlayerA(playerM);
        team21.setCompetition(comp21);
        teams.persist(team21);
        Team team22 = new Team();
        team22.setPlayerA(playerF);
        team22.setCompetition(comp22);
        teams.persist(team22);
    }

    @AfterEach
    @Transactional
    public void clearData() {
        teams.deleteAll();
        players.deleteAll();
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

    @Test
    public void getSignedUpPlayers() {
        given()
            .get("/tournament/Clubmeisterschaft 2021/competition/Herren/signedUpTeams")
            .then().assertThat()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body("$.size()", is(1),
                  "[0].playerA.firstName", is("Rainer"));
    }

    @Test
    public void getSignedUpPlayersOutdated() {
        given()
            .get("/tournament/Clubmeisterschaft 2020/competition/Herren/signedUpTeams")
            .then().assertThat()
            .statusCode(401);
        given()
            .get("/tournament/Clubmeisterschaft 2022/competition/Herren/signedUpTeams")
            .then().assertThat()
            .statusCode(401);
    }

    @Test
    @TestSecurity(user = "testuser", roles = "director")
    public void getSignedUpPlayersOutdatedAuthorized() {
        given()
            .get("/tournament/Clubmeisterschaft 2020/competition/Herren/signedUpTeams")
            .then().assertThat()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body("$.size()", is(1),
                  "[0].playerA.firstName", is("Rainer"));
        given()
            .get("/tournament/Clubmeisterschaft 2021/competition/Herren/signedUpTeams")
            .then().assertThat()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body("$.size()", is(1),
                  "[0].playerA.firstName", is("Rainer"));
        given()
            .get("/tournament/Clubmeisterschaft 2022/competition/Damen/signedUpTeams")
            .then().assertThat()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body("$.size()", is(1),
                  "[0].playerA.firstName", is("Anna"));
    }

    @Transactional
    public void addComps() {
        Tournament tour21 = tournaments.getByName("Clubmeisterschaft 2021");

        Competition compSingle = new Competition();
        compSingle.setName("Single");
        compSingle.setDescription("Offen für alle Herren");
        compSingle.setMode(CompetitionMode.SINGLES);
        compSingle.setType(CompetitionType.KNOCKOUT);
        compSingle.setPlayerASex(Sex.MALE);
        compSingle.setcProgress(PLAYER);
        compSingle.setSignup(CompetitionSignUp.INDIVIDUAL);
        compSingle.setPlayerASex(Sex.ANY);
        compSingle.setTournament(tour21);
        competitions.persist(compSingle);
        Competition compDoubleIndSame = new Competition();
        compDoubleIndSame.setName("DoubleIndSame");
        compDoubleIndSame.setDescription("Offen für alle Herren");
        compDoubleIndSame.setMode(CompetitionMode.DOUBLES);
        compDoubleIndSame.setType(CompetitionType.KNOCKOUT);
        compDoubleIndSame.setPlayerASex(Sex.MALE);
        compDoubleIndSame.setcProgress(PLAYER);
        compDoubleIndSame.setSignup(CompetitionSignUp.INDIVIDUAL);
        compDoubleIndSame.setPlayerASex(Sex.ANY);
        compDoubleIndSame.setPlayerBdifferent(false);
        compDoubleIndSame.setTournament(tour21);
        competitions.persist(compDoubleIndSame);
        Competition compDoubleIndDif = new Competition();
        compDoubleIndDif.setName("DoubleIndDif");
        compDoubleIndDif.setDescription("Offen für alle Herren");
        compDoubleIndDif.setMode(CompetitionMode.DOUBLES);
        compDoubleIndDif.setType(CompetitionType.KNOCKOUT);
        compDoubleIndDif.setPlayerASex(Sex.MALE);
        compDoubleIndDif.setcProgress(PLAYER);
        compDoubleIndDif.setSignup(CompetitionSignUp.INDIVIDUAL);
        compDoubleIndDif.setPlayerASex(Sex.MALE);
        compDoubleIndDif.setPlayerBdifferent(true);
        compDoubleIndDif.setPlayerBSex(Sex.FEMALE);
        compDoubleIndDif.setTournament(tour21);
        competitions.persist(compDoubleIndDif);
        Competition compDoubleTog = new Competition();
        compDoubleTog.setName("DoubleTog");
        compDoubleTog.setDescription("Offen für alle Herren");
        compDoubleTog.setMode(CompetitionMode.DOUBLES);
        compDoubleTog.setType(CompetitionType.KNOCKOUT);
        compDoubleTog.setPlayerASex(Sex.MALE);
        compDoubleTog.setcProgress(PLAYER);
        compDoubleTog.setSignup(CompetitionSignUp.TOGETHER);
        compDoubleTog.setPlayerASex(Sex.ANY);
        compDoubleTog.setPlayerBSex(Sex.ANY);
        compDoubleTog.setTournament(tour21);
        competitions.persist(compDoubleTog);

        Tournament tour22 = tournaments.getByName("Clubmeisterschaft 2022");
        Competition compSingle2 = new Competition();
        compSingle2.setName("Single");
        compSingle2.setDescription("Offen für alle Herren");
        compSingle2.setMode(CompetitionMode.SINGLES);
        compSingle2.setType(CompetitionType.KNOCKOUT);
        compSingle2.setPlayerASex(Sex.MALE);
        compSingle2.setcProgress(PLAYER);
        compSingle2.setSignup(CompetitionSignUp.INDIVIDUAL);
        compSingle2.setPlayerASex(Sex.ANY);
        compSingle2.setTournament(tour22);
        competitions.persist(compSingle2);
    }


    @Test
    public void testSignUpLate() {
        addComps();
        Player playerM = players.getByName("Rainer", "Zufall");

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(String.format("""
                                    {
                                        "playerA": {
                                          "id": "%s",
                                          "firstName": "Rainer",
                                          "lastName": "Zufall"
                                        }
                                    }
                                    """, playerM.getId().toString()))
            .post("/tournament/Clubmeisterschaft 2020/competition/Herren/signUp")
            .then().assertThat()
            .statusCode(401);
    }

    @Test
    public void testSignUpEarly() {
        addComps();
        Player playerM = players.getByName("Rainer", "Zufall");

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(String.format("""
                                    {
                                        "playerA": {
                                          "id": "%s",
                                          "firstName": "Rainer",
                                          "lastName": "Zufall"
                                        }
                                    }
                                    """, playerM.getId().toString()))
            .post("/tournament/Clubmeisterschaft 2022/competition/Herren/signUp")
            .then().assertThat()
            .statusCode(401);
    }

    @Test
    @TestSecurity(user = "testuser", roles = "director")
    public void testSignUpEarlyAuthorized() {
        addComps();
        Player playerM = players.getByName("Rainer", "Zufall");

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(String.format("""
                                    {
                                        "playerA": {
                                          "id": "%s",
                                          "firstName": "Rainer",
                                          "lastName": "Zufall"
                                        }
                                    }
                                    """, playerM.getId().toString()))
            .post("/tournament/Clubmeisterschaft 2022/competition/Single/signUp")
            .then().assertThat()
            .statusCode(200);
    }

    @Test
    public void testSignUpSingle() {
        addComps();
        Player playerM = players.getByName("Rainer", "Zufall");

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(String.format("""
                                    {
                                        "playerA": {
                                          "id": "%s",
                                          "firstName": "Rainer",
                                          "lastName": "Zufall"
                                        }
                                    }
                                    """, playerM.getId().toString()))
            .post("/tournament/Clubmeisterschaft 2021/competition/Single/signUp")
            .then().assertThat()
            .statusCode(200);
    }

    @Test
    public void testSignUpSingleEmpty() {
        addComps();
        Player playerM = players.getByName("Rainer", "Zufall");

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body("{}")
            .post("/tournament/Clubmeisterschaft 2021/competition/Single/signUp")
            .then().assertThat()
            .statusCode(400);
    }

    @Test
    public void testSignUpSingleConflict() {
        addComps();
        Player playerM = players.getByName("Rainer", "Zufall");

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(String.format("""
                                    {
                                        "playerA": {
                                          "id": "%s",
                                          "firstName": "Rainer",
                                          "lastName": "Zufall"
                                        }
                                    }
                                    """, playerM.getId().toString()))
            .post("/tournament/Clubmeisterschaft 2021/competition/Single/signUp")
            .then().assertThat()
            .statusCode(200);
        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(String.format("""
                                    {
                                        "playerA": {
                                          "id": "%s",
                                          "firstName": "Rainer",
                                          "lastName": "Zufall"
                                        }
                                    }
                                    """, playerM.getId().toString()))
            .post("/tournament/Clubmeisterschaft 2021/competition/Single/signUp")
            .then().assertThat()
            .statusCode(409);
    }

    @Test
    public void testSignUpInvalidComp() {
        addComps();
        Player playerM = players.getByName("Rainer", "Zufall");

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(String.format("""
                                    {
                                        "playerA": {
                                          "id": "%s",
                                          "firstName": "Rainer",
                                          "lastName": "Zufall"
                                        }
                                    }
                                    """, playerM.getId().toString()))
            .post("/tournament/Clubmeisterschaft 2021/competition/Single2/signUp")
            .then().assertThat()
            .statusCode(400);
    }

    @Test
    public void testSignUpDoubleIndSame() {
        addComps();
        Player playerM = players.getByName("Rainer", "Zufall");

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(String.format("""
                                    {
                                        "playerA": {
                                          "id": "%s",
                                          "firstName": "Rainer",
                                          "lastName": "Zufall"
                                        }
                                    }
                                    """, playerM.getId().toString()))
            .post("/tournament/Clubmeisterschaft 2021/competition/DoubleIndSame/signUp")
            .then().assertThat()
            .statusCode(200);
    }

    @Test
    public void testSignUpDoubleIndDif() {
        addComps();
        Player playerM = players.getByName("Rainer", "Zufall");
        Player playerF = players.getByName("Anna", "Gramm");

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(String.format("""
                                    {
                                        "playerA": {
                                          "id": "%s",
                                          "firstName": "Rainer",
                                          "lastName": "Zufall"
                                        }
                                    }
                                    """, playerM.getId().toString()))
            .post("/tournament/Clubmeisterschaft 2021/competition/DoubleIndDif/signUp")
            .then().assertThat()
            .statusCode(200);


        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(String.format("""
                                    {
                                        "playerB": {
                                          "id": "%s",
                                          "firstName": "Anny",
                                          "lastName": "Gramm"
                                        }
                                    }
                                    """, playerF.getId().toString()))
            .post("/tournament/Clubmeisterschaft 2021/competition/DoubleIndDif/signUp")
            .then().assertThat()
            .statusCode(200);
    }

    @Test
    public void testSignUpDoubleIndDifBoth() {
        addComps();
        Player playerM = players.getByName("Rainer", "Zufall");
        Player playerF = players.getByName("Anna", "Gramm");

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(String.format("""
                                    {
                                        "playerA": {
                                          "id": "%s",
                                          "firstName": "Rainer",
                                          "lastName": "Zufall"
                                        },
                                        "playerB": {
                                          "id": "%s",
                                          "firstName": "Anny",
                                          "lastName": "Gramm"
                                        }
                                    }
                                    """, playerM.getId().toString(), playerF.getId().toString()))
            .post("/tournament/Clubmeisterschaft 2021/competition/DoubleIndDif/signUp")
            .then().assertThat()
            .statusCode(400);

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body("{}")
            .post("/tournament/Clubmeisterschaft 2021/competition/DoubleIndDif/signUp")
            .then().assertThat()
            .statusCode(400);
    }

    @Test
    public void testSignUpDoubleTog() {
        addComps();
        Player playerM = players.getByName("Rainer", "Zufall");
        Player playerF = players.getByName("Anna", "Gramm");

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(String.format("""
                                    {
                                        "playerA": {
                                          "id": "%s",
                                          "firstName": "Rainer",
                                          "lastName": "Zufall"
                                        },
                                        "playerB": {
                                          "id": "%s",
                                          "firstName": "Anna",
                                          "lastName": "Gramm"
                                        }
                                    }
                                    """, playerM.getId().toString(), playerF.getId().toString()))
            .post("/tournament/Clubmeisterschaft 2021/competition/DoubleTog/signUp")
            .then().assertThat()
            .statusCode(200);
    }

    @Test
    public void testSignUpDoubleTogMissing() {
        addComps();
        Player playerM = players.getByName("Rainer", "Zufall");
        Player playerF = players.getByName("Anna", "Gramm");

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(String.format("""
                                    {
                                        "playerB": {
                                          "id": "%s",
                                          "firstName": "Anna",
                                          "lastName": "Gramm"
                                        }
                                    }
                                    """, playerF.getId().toString()))
            .post("/tournament/Clubmeisterschaft 2021/competition/DoubleTog/signUp")
            .then().assertThat()
            .statusCode(400);

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(String.format("""
                                    {
                                        "playerA": {
                                          "id": "%s",
                                          "firstName": "Rainer",
                                          "lastName": "Zufall"
                                        }
                                    }
                                    """, playerM.getId().toString()))
            .post("/tournament/Clubmeisterschaft 2021/competition/DoubleTog/signUp")
            .then().assertThat()
            .statusCode(400);
    }

    @Test
    @TestSecurity(user = "testuser", roles = "director")
    public void updateTeamsChangeTeams() {
        Player playerF = players.getByName("Anna", "Gramm");
        UUID teamIdBefore = competitions.getByName("Clubmeisterschaft 2021", "Herren")
                                        .getTeams().getFirst().getId();

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(String.format("""
                                    [
                                        {
                                            "playerA": {
                                              "id": "%s",
                                              "firstName": "Anna",
                                              "lastName": "Gramm"
                                            }
                                        }
                                    ]
                                    """, playerF.getId().toString()))
            .post("/tournament/Clubmeisterschaft 2021/competition/Herren/updateTeams")
            .then().assertThat()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body("$.size()", is(1),
                  "[0].id", not(teamIdBefore.toString()),
                  "[0].playerA.id", is(playerF.getId().toString()));

        given()
            .get("/tournament/Clubmeisterschaft 2021/competition/Herren/signedUpTeams")
            .then().assertThat()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body("$.size()", is(1),
                  "[0].id", not(teamIdBefore.toString()),
                  "[0].playerA.firstName", is("Anna"));
    }

    @Test
    @TestSecurity(user = "testuser", roles = "director")
    public void updateTeamsChangePlayerInTeam() {
        Player playerF = players.getByName("Anna", "Gramm");
        UUID teamId = competitions.getByName("Clubmeisterschaft 2021", "Herren")
                                  .getTeams().getFirst().getId();

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(String.format("""
                                    [
                                        {
                                            "id": "%s",
                                            "playerA": {
                                              "id": "%s",
                                              "firstName": "Anna",
                                              "lastName": "Gramm"
                                            }
                                        }
                                    ]
                                    """, teamId, playerF.getId().toString()))
            .post("/tournament/Clubmeisterschaft 2021/competition/Herren/updateTeams")
            .then().assertThat()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body("$.size()", is(1),
                  "[0].id", is(teamId.toString()),
                  "[0].playerA.id", is(playerF.getId().toString()));

        given()
            .get("/tournament/Clubmeisterschaft 2021/competition/Herren/signedUpTeams")
            .then().assertThat()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body("$.size()", is(1),
                  "[0].id", is(teamId.toString()),
                  "[0].playerA.firstName", is("Anna"));
    }

    // TODO Tests
}
