package template;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class TestExample {

    @Test
    public void unauthorized() {
        given().get("/template/unauthorized").then().assertThat().statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    public void authorizedAsUnauthorized() {
        given()
            .get("/template/authorized")
            .then().assertThat()
            .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    @TestSecurity(user = "testUser")
    public void authorizedAsAuthorized() {
        given()
            .get("/template/authorized")
            .then().assertThat()
            .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    public void userAsUnauthorized() {
        given()
            .get("/template/user")
            .then().assertThat()
            .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"user"})
    public void userAsUser() {
        given()
            .get("/template/user")
            .then().assertThat()
            .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    public void reporterAsUnauthorized() {
        given()
            .get("/template/reporter")
            .then().assertThat()
            .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"reporter"})
    public void reporterAsReporter() {
        given()
            .get("/template/reporter")
            .then().assertThat()
            .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    public void directorAsUnauthorized() {
        given()
            .get("/template/director")
            .then().assertThat()
            .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"director"})
    public void directorAsUser() {
        given()
            .get("/template/director")
            .then().assertThat()
            .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    public void adminAsUnauthorized() {
        given()
            .get("/template/admin")
            .then().assertThat()
            .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"admin"})
    public void adminAsAdmin() {
        given()
            .get("/template/admin")
            .then().assertThat()
            .statusCode(Response.Status.OK.getStatusCode());
    }
}
