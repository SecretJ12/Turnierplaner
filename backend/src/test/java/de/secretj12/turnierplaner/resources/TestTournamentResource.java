package de.secretj12.turnierplaner.resources;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class TestTournamentResource {

    @Test
    public void testCanCreateUnauthorized() {
        given()
                .get("/tournament/canCreate")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"user", "reporter", "director"})
    public void testCanCreateAuthorized() {
        given()
                .get("/tournament/canCreate")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

}
