package uk.ac.newcastle.enterprisemiddleware.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * REST Assured tests for GuestBooking endpoint with JTA transaction management.
 */
@QuarkusTest
public class GuestBookingResourceTest {

    @Test
    public void testCreateGuestBooking_Success() {
        String guestBookingRequest = """
            {
                "customer": {
                    "firstName": "Guest",
                    "lastName": "User",
                    "email": "guest.user@test.com",
                    "phoneNumber": "1231231234"
                },
                "commodityId": 1
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(guestBookingRequest)
            .when().post("/guest-bookings")
            .then()
            .statusCode(201)
            .body("customer.firstName", equalTo("Guest"))
            .body("customer.lastName", equalTo("User"))
            .body("customer.email", equalTo("guest.user@test.com"))
            .body("booking.customer.id", notNullValue())
            .body("booking.commodity.id", equalTo(1))
            .body("message", containsString("transaction"));
    }

    @Test
    public void testCreateGuestBooking_DuplicateEmail_Rollback() {
        // Create first guest booking
        String guestBooking1 = """
            {
                "customer": {
                    "firstName": "John",
                    "lastName": "Rollback",
                    "email": "john.rollback@test.com",
                    "phoneNumber": "4564564567"
                },
                "commodityId": 2
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(guestBooking1)
            .when().post("/guest-bookings")
            .then()
            .statusCode(201);

        // Try to create another guest booking with same email - should rollback
        String guestBooking2 = """
            {
                "customer": {
                    "firstName": "Jane",
                    "lastName": "Rollback",
                    "email": "john.rollback@test.com",
                    "phoneNumber": "7897897890"
                },
                "commodityId": 3
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(guestBooking2)
            .when().post("/guest-bookings")
            .then()
            .statusCode(409);
    }

    @Test
    public void testCreateGuestBooking_CommodityNotFound_Rollback() {
        String guestBookingRequest = """
            {
                "customer": {
                    "firstName": "Invalid",
                    "lastName": "Commodity",
                    "email": "invalid.commodity@test.com",
                    "phoneNumber": "3213213210"
                },
                "commodityId": 99999
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(guestBookingRequest)
            .when().post("/guest-bookings")
            .then()
            .statusCode(404);

        // Verify customer was not created (transaction rolled back)
        given()
            .when().get("/customers")
            .then()
            .statusCode(200)
            .body("findAll { it.email == 'invalid.commodity@test.com' }", empty());
    }

    @Test
    public void testCreateGuestBooking_InvalidCustomerData() {
        String invalidRequest = """
            {
                "customer": {
                    "firstName": "Invalid",
                    "lastName": "Email",
                    "email": "not-an-email",
                    "phoneNumber": "6546546540"
                },
                "commodityId": 1
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(invalidRequest)
            .when().post("/guest-bookings")
            .then()
            .statusCode(400);
    }

    @Test
    public void testCreateGuestBooking_MissingCustomer() {
        String invalidRequest = """
            {
                "commodityId": 1
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(invalidRequest)
            .when().post("/guest-bookings")
            .then()
            .statusCode(400);
    }

    @Test
    public void testCreateGuestBooking_MissingCommodityId() {
        String invalidRequest = """
            {
                "customer": {
                    "firstName": "Missing",
                    "lastName": "Commodity",
                    "email": "missing.commodity@test.com",
                    "phoneNumber": "9879879870"
                }
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(invalidRequest)
            .when().post("/guest-bookings")
            .then()
            .statusCode(400);
    }
}

