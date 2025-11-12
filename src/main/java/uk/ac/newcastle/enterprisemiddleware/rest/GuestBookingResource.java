package uk.ac.newcastle.enterprisemiddleware.rest;

import jakarta.inject.Inject;
import jakarta.transaction.UserTransaction;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import uk.ac.newcastle.enterprisemiddleware.entity.Booking;
import uk.ac.newcastle.enterprisemiddleware.entity.Customer;
import uk.ac.newcastle.enterprisemiddleware.rest.dto.GuestBookingRequest;
import uk.ac.newcastle.enterprisemiddleware.rest.dto.GuestBookingResponse;
import uk.ac.newcastle.enterprisemiddleware.service.BookingService;
import uk.ac.newcastle.enterprisemiddleware.service.CommodityService;
import uk.ac.newcastle.enterprisemiddleware.service.CustomerService;

import java.util.logging.Logger;

/**
 * REST resource for GuestBooking operations.
 * This endpoint creates a customer and booking in a single JTA-managed transaction.
 */
@Path("/guest-bookings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "GuestBooking", description = "Guest booking operations with manual JTA transaction management")
public class GuestBookingResource {

    @Inject
    Logger log;

    @Inject
    UserTransaction userTransaction;

    @Inject
    CustomerService customerService;

    @Inject
    CommodityService commodityService;

    @Inject
    BookingService bookingService;

    /**
     * Create a guest booking (customer + booking) in a single transaction.
     * This uses manual JTA transaction management (UserTransaction API).
     *
     * @param request the guest booking request containing customer and commodity info
     * @return the created customer and booking
     */
    @POST
    @Operation(summary = "Create a guest booking", 
               description = "Creates a new customer and booking in a single JTA-managed transaction. " +
                             "If any operation fails, the entire transaction is rolled back.")
    @APIResponse(responseCode = "201", description = "Guest booking created successfully",
            content = @Content(schema = @Schema(implementation = GuestBookingResponse.class)))
    @APIResponse(responseCode = "400", description = "Invalid request data or commodity out of stock")
    @APIResponse(responseCode = "404", description = "Commodity not found")
    @APIResponse(responseCode = "409", description = "Customer email already exists")
    @APIResponse(responseCode = "500", description = "Transaction error")
    public Response createGuestBooking(GuestBookingRequest request) {
        log.info("POST /guest-bookings - Creating guest booking with manual JTA transaction");
        
        Customer createdCustomer = null;
        Booking createdBooking = null;
        
        try {
            // Begin transaction manually using JTA UserTransaction API
            log.info("Beginning JTA transaction");
            userTransaction.begin();
            
            // Step 1: Create the customer
            log.info("Step 1: Creating customer: " + request.getCustomer().getEmail());
            createdCustomer = customerService.createCustomer(request.getCustomer());
            log.info("Customer created with ID: " + createdCustomer.getId());
            
            // Step 2: Validate commodity exists and has stock
            log.info("Step 2: Validating commodity ID: " + request.getCommodityId());
            commodityService.findCommodityById(request.getCommodityId());
            
            // Step 3: Create the booking
            log.info("Step 3: Creating booking for customer " + createdCustomer.getId() + 
                     " and commodity " + request.getCommodityId());
            createdBooking = bookingService.createBooking(createdCustomer.getId(), request.getCommodityId());
            log.info("Booking created with ID: " + createdBooking.getId());
            
            // Commit the transaction
            log.info("Committing JTA transaction");
            userTransaction.commit();
            log.info("Transaction committed successfully");
            
            // Prepare response
            GuestBookingResponse response = new GuestBookingResponse(createdCustomer, createdBooking);
            return Response.status(Response.Status.CREATED).entity(response).build();
            
        } catch (Exception e) {
            // Rollback transaction on any error
            log.severe("Error occurred during guest booking creation: " + e.getMessage());
            try {
                if (userTransaction.getStatus() != jakarta.transaction.Status.STATUS_NO_TRANSACTION) {
                    log.info("Rolling back JTA transaction due to error");
                    userTransaction.rollback();
                    log.info("Transaction rolled back successfully");
                }
            } catch (Exception rollbackEx) {
                log.severe("Error during transaction rollback: " + rollbackEx.getMessage());
            }
            
            // Determine appropriate error response
            if (e instanceof WebApplicationException) {
                throw (WebApplicationException) e;
            } else {
                throw new WebApplicationException(
                    "Failed to create guest booking: " + e.getMessage(), 
                    Response.Status.INTERNAL_SERVER_ERROR
                );
            }
        }
    }
}

