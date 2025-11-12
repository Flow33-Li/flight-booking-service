package uk.ac.newcastle.enterprisemiddleware.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * REST Assured tests for Customer endpoints.
 */
@QuarkusTest
public class CustomerResourceTest {

    @Test
    public void testGetAllCustomers() {
        given()
            .when().get("/customers")
            .then()
            .statusCode(200)
            .body("$.size()", greaterThanOrEqualTo(0));
    }

    @Test
    public void testGetCustomerById() {
        given()
            .when().get("/customers/1")
            .then()
            .statusCode(200)
            .body("id", equalTo(1))
            .body("email", notNullValue());
    }

    @Test
    public void testGetCustomerById_NotFound() {
        given()
            .when().get("/customers/99999")
            .then()
            .statusCode(404);
    }

    @Test
    public void testCreateCustomer() {
        String newCustomer = """
            {
                "firstName": "Alice",
                "lastName": "Johnson",
                "email": "alice.johnson@test.com",
                "phoneNumber": "5551234567"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(newCustomer)
            .when().post("/customers")
            .then()
            .statusCode(201)
            .body("firstName", equalTo("Alice"))
            .body("lastName", equalTo("Johnson"))
            .body("email", equalTo("alice.johnson@test.com"))
            .body("id", notNullValue());
    }

    @Test
    public void testCreateCustomer_DuplicateEmail() {
        String customer1 = """
            {
                "firstName": "Bob",
                "lastName": "Brown",
                "email": "bob.brown@test.com",
                "phoneNumber": "5559876543"
            }
            """;

        // Create first customer
        given()
            .contentType(ContentType.JSON)
            .body(customer1)
            .when().post("/customers")
            .then()
            .statusCode(201);

        // Try to create second customer with same email - should fail
        given()
            .contentType(ContentType.JSON)
            .body(customer1)
            .when().post("/customers")
            .then()
            .statusCode(409);
    }

    @Test
    public void testCreateCustomer_InvalidEmail() {
        String invalidCustomer = """
            {
                "firstName": "Charlie",
                "lastName": "Davis",
                "email": "invalid-email",
                "phoneNumber": "5551112222"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(invalidCustomer)
            .when().post("/customers")
            .then()
            .statusCode(400);
    }

    @Test
    public void testCreateCustomer_InvalidPhoneNumber() {
        String invalidCustomer = """
            {
                "firstName": "David",
                "lastName": "Evans",
                "email": "david.evans@test.com",
                "phoneNumber": "123"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(invalidCustomer)
            .when().post("/customers")
            .then()
            .statusCode(400);
    }

    @Test
    public void testUpdateCustomer() {
        String newCustomer = """
            {
                "firstName": "Eve",
                "lastName": "Foster",
                "email": "eve.foster@test.com",
                "phoneNumber": "5553334444"
            }
            """;

        // Create customer
        Integer customerId = given()
            .contentType(ContentType.JSON)
            .body(newCustomer)
            .when().post("/customers")
            .then()
            .statusCode(201)
            .extract().path("id");

        // Update customer
        String updatedCustomer = """
            {
                "firstName": "Eve",
                "lastName": "Foster-Updated",
                "email": "eve.foster@test.com",
                "phoneNumber": "5553334444"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(updatedCustomer)
            .when().put("/customers/" + customerId)
            .then()
            .statusCode(200)
            .body("lastName", equalTo("Foster-Updated"));
    }

    @Test
    public void testDeleteCustomer() {
        String newCustomer = """
            {
                "firstName": "Frank",
                "lastName": "Garcia",
                "email": "frank.garcia@test.com",
                "phoneNumber": "5555556666"
            }
            """;

        // Create customer
        Integer customerId = given()
            .contentType(ContentType.JSON)
            .body(newCustomer)
            .when().post("/customers")
            .then()
            .statusCode(201)
            .extract().path("id");

        // Delete customer
        given()
            .when().delete("/customers/" + customerId)
            .then()
            .statusCode(204);

        // Verify customer is deleted
        given()
            .when().get("/customers/" + customerId)
            .then()
            .statusCode(404);
    }
}

