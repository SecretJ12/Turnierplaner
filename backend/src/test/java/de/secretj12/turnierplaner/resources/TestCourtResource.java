package de.secretj12.turnierplaner.resources;

import de.secretj12.turnierplaner.db.entities.Court;
import de.secretj12.turnierplaner.db.entities.CourtType;
import de.secretj12.turnierplaner.db.repositories.CourtRepositiory;
import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.*;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;


@QuarkusTest
public class TestCourtResource {

    @Inject
    CourtRepositiory courts;

    @BeforeEach
    @Transactional
    public void addCourts() {
        for (int i = 0; i < 5; i++) {
            Court court = new Court();
            court.setCourtType(CourtType.CLAY);
            court.setName("Court " + i);
            courts.persist(court);
        }
    }

    @AfterEach
    @Transactional
    public void delete() {
        courts.deleteAll();
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"director"})
    public void listCourts() {
        given()
            .get("/court")
            .then().assertThat()
            .statusCode(Response.Status.OK.getStatusCode())
            .body("size()", equalTo(5))
            .body("[2].name", equalTo("Court 2"));
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"director"})
    public void addCourtExists() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "name": "Court 4",
                    "courtType": "HARD"
                }""")
            .put("/court")
            .then().assertThat()
            .statusCode(Response.Status.CONFLICT.getStatusCode());
    }


    @Test
    @TestSecurity(user = "testUser", roles = {"director"})
    public void addCourt() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "name": "Court 5",
                    "courtType": "HARD"
                }""")
            .put("/court")
            .then().assertThat()
            .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"director"})
    public void updateCourt() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "name": "Court 4",
                    "courtType": "HARD"
                }""")
            .post("/court")
            .then().assertThat()
            .statusCode(Response.Status.OK.getStatusCode());
        Panache.getTransactionManager().begin();
        Court court = courts.findByName("Court 4");
        assertNotNull(court);
        assertEquals(court.getCourtType(), CourtType.HARD);
        Panache.getTransactionManager().commit();
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"director"})
    public void updateCourtNotFound() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "name": "Court 9",
                    "courtType": "HARD"
                }""")
            .post("/court")
            .then().assertThat()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"director"})
    public void deleteCourt() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        given()
            .delete("/court/Court 0")
            .then().assertThat()
            .statusCode(Response.Status.OK.getStatusCode());

        Panache.getTransactionManager().begin();
        assertNull(courts.findByName("Court 0"));
        Panache.getTransactionManager().commit();
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"director"})
    public void delCourtNotFound() {
        given()
            .delete("/court/Court 9")
            .then().assertThat()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void unauthorized() {
        given()
            .get("/court")
            .then().assertThat()
            .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());

        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "name": "Court 5",
                    "courtType": "HARD"
                }""")
            .put("/court")
            .then().assertThat()
            .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());

        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "name": "Court 4",
                    "courtType": "HARD"
                }""")
            .post("/court")
            .then().assertThat()
            .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());


        given()
            .delete("/court/Court 0")
            .then().assertThat()
            .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
    }
}
