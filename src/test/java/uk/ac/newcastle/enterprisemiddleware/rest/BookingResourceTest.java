package uk.ac.newcastle.enterprisemiddleware.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * REST Assured tests for Booking endpoints.
 */
@QuarkusTest
public class BookingResourceTest {

    @Test
    public void testGetAllBookings() {
        given()
            .when().get("/bookings")
            .then()
            .statusCode(200)
            .body("$.size()", greaterThanOrEqualTo(0));
    }

    @Test
    public void testGetBookingById() {
        given()
            .when().get("/bookings/1")
            .then()
            .statusCode(200)
            .body("id", equalTo(1))
            .body("customer", notNullValue())
            .body("commodity", notNullValue());
    }

    @Test
    public void testGetBookingById_NotFound() {
        given()
            .when().get("/bookings/99999")
            .then()
            .statusCode(404);
    }

    @Test
    public void testCreateBooking() {
        // First create a customer
        String newCustomer = """
            {
                "firstName": "Test",
                "lastName": "User",
                "email": "test.booking@test.com",
                "phoneNumber": "1234567890"
            }
            """;

        Integer customerId = given()
            .contentType(ContentType.JSON)
            .body(newCustomer)
            .when().post("/customers")
            .then()
            .statusCode(201)
            .extract().path("id");

        // Create booking with existing commodity
        given()
            .queryParam("customerId", customerId)
            .queryParam("commodityId", 1)
            .when().post("/bookings")
            .then()
            .statusCode(201)
            .body("customer.id", equalTo(customerId))
            .body("commodity.id", equalTo(1))
            .body("id", notNullValue());
    }

    @Test
    public void testCreateBooking_MissingParameters() {
        given()
            .when().post("/bookings")
            .then()
            .statusCode(400);
    }

    @Test
    public void testCreateBooking_CustomerNotFound() {
        given()
            .queryParam("customerId", 99999)
            .queryParam("commodityId", 1)
            .when().post("/bookings")
            .then()
            .statusCode(404);
    }

    @Test
    public void testCreateBooking_CommodityNotFound() {
        given()
            .queryParam("customerId", 1)
            .queryParam("commodityId", 99999)
            .when().post("/bookings")
            .then()
            .statusCode(404);
    }

    @Test
    public void testCreateBooking_Duplicate() {
        // Create first booking
        String newCustomer = """
            {
                "firstName": "Duplicate",
                "lastName": "Test",
                "email": "duplicate.test@test.com",
                "phoneNumber": "9876543210"
            }
            """;

        Integer customerId = given()
            .contentType(ContentType.JSON)
            .body(newCustomer)
            .when().post("/customers")
            .then()
            .statusCode(201)
            .extract().path("id");

        given()
            .queryParam("customerId", customerId)
            .queryParam("commodityId", 2)
            .when().post("/bookings")
            .then()
            .statusCode(201);

        // Try to create duplicate booking - should fail
        given()
            .queryParam("customerId", customerId)
            .queryParam("commodityId", 2)
            .when().post("/bookings")
            .then()
            .statusCode(409);
    }

    @Test
    public void testCancelBooking() {
        // Create customer and booking first
        String newCustomer = """
            {
                "firstName": "Cancel",
                "lastName": "Test",
                "email": "cancel.test@test.com",
                "phoneNumber": "1112223333"
            }
            """;

        Integer customerId = given()
            .contentType(ContentType.JSON)
            .body(newCustomer)
            .when().post("/customers")
            .then()
            .statusCode(201)
            .extract().path("id");

        Integer bookingId = given()
            .queryParam("customerId", customerId)
            .queryParam("commodityId", 3)
            .when().post("/bookings")
            .then()
            .statusCode(201)
            .extract().path("id");

        // Cancel booking
        given()
            .when().delete("/bookings/" + bookingId)
            .then()
            .statusCode(204);

        // Verify booking is deleted
        given()
            .when().get("/bookings/" + bookingId)
            .then()
            .statusCode(404);
    }

    @Test
    public void testGetBookingsByCustomerId() {
        given()
            .when().get("/bookings/customer/1")
            .then()
            .statusCode(200)
            .body("$.size()", greaterThanOrEqualTo(0));
    }
}

