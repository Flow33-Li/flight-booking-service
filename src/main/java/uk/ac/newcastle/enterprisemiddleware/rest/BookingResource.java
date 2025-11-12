package uk.ac.newcastle.enterprisemiddleware.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import uk.ac.newcastle.enterprisemiddleware.entity.Booking;
import uk.ac.newcastle.enterprisemiddleware.service.BookingService;

import java.util.List;
import java.util.logging.Logger;

/**
 * REST resource for Booking operations.
 */
@Path("/bookings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Booking", description = "Booking management operations")
public class BookingResource {

    @Inject
    Logger log;

    @Inject
    BookingService bookingService;

    /**
     * Get all bookings.
     *
     * @return list of all bookings
     */
    @GET
    @Operation(summary = "Get all bookings", description = "Returns a list of all bookings")
    @APIResponse(responseCode = "200", description = "Successful retrieval of bookings",
            content = @Content(schema = @Schema(implementation = Booking.class)))
    public Response getAllBookings() {
        log.info("GET /bookings - Getting all bookings");
        List<Booking> bookings = bookingService.findAllBookings();
        return Response.ok(bookings).build();
    }

    /**
     * Get a booking by ID.
     *
     * @param id the booking ID
     * @return the booking
     */
    @GET
    @Path("/{id}")
    @Operation(summary = "Get booking by ID", description = "Returns a single booking by ID")
    @APIResponse(responseCode = "200", description = "Booking found",
            content = @Content(schema = @Schema(implementation = Booking.class)))
    @APIResponse(responseCode = "404", description = "Booking not found")
    public Response getBookingById(@PathParam("id") Long id) {
        log.info("GET /bookings/" + id + " - Getting booking by id");
        Booking booking = bookingService.findBookingById(id);
        return Response.ok(booking).build();
    }

    /**
     * Get all bookings for a specific customer.
     *
     * @param customerId the customer ID
     * @return list of bookings
     */
    @GET
    @Path("/customer/{customerId}")
    @Operation(summary = "Get bookings by customer", description = "Returns all bookings for a specific customer")
    @APIResponse(responseCode = "200", description = "Successful retrieval of customer bookings",
            content = @Content(schema = @Schema(implementation = Booking.class)))
    public Response getBookingsByCustomerId(@PathParam("customerId") Long customerId) {
        log.info("GET /bookings/customer/" + customerId + " - Getting bookings for customer");
        List<Booking> bookings = bookingService.findBookingsByCustomerId(customerId);
        return Response.ok(bookings).build();
    }

    /**
     * Create a new booking.
     *
     * @param customerId  the customer ID
     * @param commodityId the commodity ID
     * @return the created booking
     */
    @POST
    @Operation(summary = "Create a new booking", description = "Creates a new booking for a customer and commodity")
    @APIResponse(responseCode = "201", description = "Booking created successfully",
            content = @Content(schema = @Schema(implementation = Booking.class)))
    @APIResponse(responseCode = "400", description = "Invalid booking data or commodity out of stock")
    @APIResponse(responseCode = "404", description = "Customer or commodity not found")
    @APIResponse(responseCode = "409", description = "Booking already exists")
    public Response createBooking(
            @Parameter(description = "Customer ID", required = true)
            @QueryParam("customerId") Long customerId,
            @Parameter(description = "Commodity ID", required = true)
            @QueryParam("commodityId") Long commodityId) {
        
        log.info("POST /bookings - Creating new booking for customer " + customerId + " and commodity " + commodityId);
        
        if (customerId == null || commodityId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"customerId and commodityId are required\"}")
                    .build();
        }
        
        Booking created = bookingService.createBooking(customerId, commodityId);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    /**
     * Cancel a booking.
     *
     * @param id the booking ID
     * @return no content response
     */
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Cancel a booking", description = "Cancels a booking and returns the commodity to available stock")
    @APIResponse(responseCode = "204", description = "Booking canceled successfully")
    @APIResponse(responseCode = "404", description = "Booking not found")
    public Response cancelBooking(@PathParam("id") Long id) {
        log.info("DELETE /bookings/" + id + " - Canceling booking");
        bookingService.cancelBooking(id);
        return Response.noContent().build();
    }
}

