package de.secretj12.turnierplaner.resources;

import de.secretj12.turnierplaner.db.entities.Player;
import de.secretj12.turnierplaner.db.entities.SexType;
import de.secretj12.turnierplaner.db.entities.VerificationCode;
import de.secretj12.turnierplaner.db.repositories.PlayerRepository;
import de.secretj12.turnierplaner.db.repositories.VerificationCodeRepository;
import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.*;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;


@QuarkusTest
public class TestPlayerResource {

    @Inject
    PlayerRepository players;
    @Inject
    VerificationCodeRepository verificationCodes;
    @Inject
    MockMailbox mailbox;

    @BeforeEach
    @Transactional
    public void addPlayer() {
        for (int i = 0; i < 5; i++) {
            players.persist(new Player("M" + i, "F" + i + "V", SexType.MALE,
                    LocalDate.parse("2023-04-02"), "a@example.org", "+12345", true, true));
        }
        for (int i = 5; i < 10; i++) {
            players.persist(new Player("M" + i, "F" + i, SexType.MALE,
                    LocalDate.parse("2023-04-05"), "a@example.org", "+12345", false, true));
        }
        for (int i = 10; i < 20; i++) {
            players.persist(new Player("M" + i, "F" + i + "V", SexType.MALE,
                    LocalDate.parse("2023-04-05"), "a@example.org", "+12345", true, true));
        }
        for (int i = 0; i < 3; i++) {
            players.persist(new Player("F" + i, "M" + i, SexType.FEMALE,
                    LocalDate.parse("2023-04-02"), "a@example.org", "+12345", true, true));
        }

        players.persist(new Player("first", "last", SexType.MALE,
                LocalDate.parse("2023-04-02"), "a@example.org", "+12345", true, true));

        mailbox.clear();
    }

    @AfterEach
    @Transactional
    public void delete() {
        players.deleteAll();
    }

    @Test
    public void listPlayerEmpty() {
        given()
                .param("search", "")
                .get("/player/find")
                .then().assertThat()
                .statusCode(Response.Status.OK.getStatusCode())
                .and().body("size()", equalTo(0));
    }

    @Test
    public void listPlayerLessThan10() {
        given()
                .param("search", "M")
                .get("/player/find")
                .then().assertThat()
                .statusCode(Response.Status.OK.getStatusCode())
                .and().body("size()", equalTo(10));
    }

    @Test
    public void listPlayerMale() {
        given()
                .param("search", "M")
                .param("sex", "MALE")
                .get("/player/find")
                .then().assertThat()
                .statusCode(Response.Status.OK.getStatusCode())
                .and().body("findAll { it.firstName.startsWith('M') }.size()", equalTo(10));
    }

    @Test
    public void listPlayerFemale() {
        given()
                .param("search", "F")
                .param("sex", "FEMALE")
                .get("/player/find")
                .then().assertThat()
                .statusCode(Response.Status.OK.getStatusCode())
                .and().body("findAll { it.firstName.startsWith('F') }.size()", equalTo(3));
    }

    @Test
    public void listPlayerCheckVerified() {
        given()
                .param("search", "M")
                .get("/player/find")
                .then().assertThat()
                .statusCode(Response.Status.OK.getStatusCode())
                .and().body("findAll { it.lastName.endsWith('V') }.size()", equalTo(10));
    }

    @Test
    public void listPlayerMaxAge() {
        given()
                .param("search", "M")
                .param("maxAge", "2023-04-03")
                .get("/player/find")
                .then().assertThat()
                .statusCode(Response.Status.OK.getStatusCode())
                .and().body("size()", equalTo(10));
    }

    @Test
    public void listPlayerMinAge() {
        given()
                .param("search", "M")
                .param("minAge", "2023-04-03")
                .get("/player/find")
                .then().assertThat()
                .statusCode(Response.Status.OK.getStatusCode())
                .and().body("size()", equalTo(8));
    }

    @Test
    public void registerPlayerConflict() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                      {
                          "firstName": "first",
                          "lastName": "last",
                          "sex": "MALE",
                          "birthday": "2022-03-10",
                          "email": "ab@example.org",
                          "phone": "+034759834"
                      }""")
                .post("/player/registration")
                .then().assertThat()
                .statusCode(Response.Status.CONFLICT.getStatusCode());
    }

    @Test
    public void registerPlayerStandardFlow() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        String recipient = "ab@example.org";
        String tel = "+034759834";
        given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                      {
                          "firstName": "firstName",
                          "lastName": "lastName",
                          "sex": "MALE",
                          "birthday": "1977-03-10",
                          "email": "%s",
                          "phone": "%s"
                      }""", recipient, tel))
                .post("/player/registration")
                .then().assertThat()
                .statusCode(Response.Status.OK.getStatusCode());

        Panache.getTransactionManager().begin();
        assertNotNull(players.getByName("firstName", "lastName"));
        assertFalse(players.getByName("firstName", "lastName").isMailVerified());
        // TODO admin verified?
        assertEquals(tel, players.getByName("firstName", "lastName").getPhone());

        assertEquals(1, mailbox.getTotalMessagesSent());
        assertDoesNotThrow(() -> mailbox.getMessagesSentTo(recipient).get(0).getHtml());
        String text = mailbox.getMessagesSentTo("ab@example.org").get(0).getHtml();
        assertTrue(text.contains("Please verify"));
        String code = text.split("code=")[1].split("#")[0];
        VerificationCode verificationCode = verificationCodes.findByUUID(UUID.fromString(code));
        Player player = players.getByName("firstName", "lastName");
        assertEquals(verificationCode.getPlayer().getId(), player.getId());
        Panache.getTransactionManager().commit();

        given()
                .param("code", code)
                .get("/player/verification")
                .then().assertThat()
                .statusCode(Response.Status.ACCEPTED.getStatusCode());
        assertTrue(players.getByName("firstName", "lastName").isMailVerified());
        // TODO admin verified?
        assertEquals(tel, players.getByName("firstName", "lastName").getPhone());
    }

    @Test
    public void playerRegistrationFemale() {
        String recipient = "ab@example.org";
        String tel = "+034759834";
        given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                      {
                          "firstName": "firstName",
                          "lastName": "lastName",
                          "sex": "FEMALE",
                          "birthday": "1977-03-10",
                          "email": "%s",
                          "phone": "%s"
                      }""", recipient, tel))
                .post("/player/registration")
                .then().assertThat()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    public void playerRegistrationNoSex() {
        String recipient = "ab@example.org";
        String tel = "+034759834";
        given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                      {
                          "firstName": "firstName",
                          "lastName": "lastName",
                          "birthday": "1977-03-10",
                          "email": "%s",
                          "phone": "%s"
                      }""", recipient, tel))
                .post("/player/registration")
                .then().assertThat()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }
}
