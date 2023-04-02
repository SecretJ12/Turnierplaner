package de.secretj12.turnierplaner.resources;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class TestTournamentResource {

    @Test
    public void testCanCreateUnauthorized() {
        given()
                .get("/tournament/canCreate")
                .then().assertThat()
                .statusCode(Response.Status.OK.getStatusCode())
                .body(is("false"));
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"user", "reporter", "director"})
    public void testCanCreateAuthorized() {
        given()
                .get("/tournament/canCreate")
                .then().assertThat()
                .statusCode(Response.Status.OK.getStatusCode())
                .body(is("true"));
    }

    // TODO Tests
}
