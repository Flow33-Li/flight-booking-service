package uk.ac.newcastle.enterprisemiddleware.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import uk.ac.newcastle.enterprisemiddleware.client.HotelBookingRequest;
import uk.ac.newcastle.enterprisemiddleware.client.HotelBookingResponse;
import uk.ac.newcastle.enterprisemiddleware.client.HotelServiceClient;
import uk.ac.newcastle.enterprisemiddleware.client.TaxiBookingRequest;
import uk.ac.newcastle.enterprisemiddleware.client.TaxiBookingResponse;
import uk.ac.newcastle.enterprisemiddleware.client.TaxiServiceClient;
import uk.ac.newcastle.enterprisemiddleware.entity.Booking;
import uk.ac.newcastle.enterprisemiddleware.rest.dto.TravelAgentBookingRequest;
import uk.ac.newcastle.enterprisemiddleware.rest.dto.TravelAgentBookingResponse;
import uk.ac.newcastle.enterprisemiddleware.service.BookingService;

import java.util.logging.Logger;

/**
 * Travel Agent Resource for coordinating distributed bookings
 * Implements Saga pattern with compensation-based rollback
 */
@Path("/travel-agent")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "TravelAgent", description = "Travel Agent orchestration for distributed bookings")
public class TravelAgentResource {

    @Inject
    Logger log;

    @Inject
    @RestClient
    HotelServiceClient hotelServiceClient;

    @Inject
    @RestClient
    TaxiServiceClient taxiServiceClient;

    @Inject
    BookingService bookingService;

    /**
     * Create a travel booking (Hotel + Flight + Taxi) with distributed transaction coordination
     *
     * @param request Travel booking request
     * @return Travel booking response with all booking IDs or error
     */
    @POST
    @Path("/bookings")
    @Operation(summary = "Create travel booking", 
               description = "Creates hotel, flight, and taxi bookings. Implements compensation pattern - " +
                             "if any booking fails, successful bookings are automatically cancelled.")
    @APIResponse(responseCode = "201", description = "Travel booking created successfully",
            content = @Content(schema = @Schema(implementation = TravelAgentBookingResponse.class)))
    @APIResponse(responseCode = "400", description = "Invalid request data")
    @APIResponse(responseCode = "500", description = "Booking failed, compensation executed")
    public Response createTravelBooking(TravelAgentBookingRequest request) {
        log.info("Travel Agent: Creating travel booking for customer " + request.getCustomerId());
        
        Long hotelBookingId = null;
        Long flightBookingId = null;
        Long taxiBookingId = null;
        
        try {
            // Step 1: Book Hotel (external service)
            log.info("Step 1: Booking hotel " + request.getHotelId());
            HotelBookingRequest hotelRequest = new HotelBookingRequest(
                    request.getCustomerId(),
                    request.getHotelId(),
                    request.getDate()
            );
            
            HotelBookingResponse hotelResponse = hotelServiceClient.createBooking(hotelRequest);
            hotelBookingId = hotelResponse.getId();
            log.info("Hotel booking created with ID: " + hotelBookingId);
            
            // Step 2: Book Flight (local service)
            log.info("Step 2: Booking flight commodity " + request.getFlightCommodityId());
            Booking flightBooking = bookingService.createBooking(
                    request.getCustomerId(),
                    request.getFlightCommodityId()
            );
            flightBookingId = flightBooking.getId();
            log.info("Flight booking created with ID: " + flightBookingId);
            
            // Step 3: Book Taxi (external service)
            log.info("Step 3: Booking taxi " + request.getTaxiId());
            TaxiBookingRequest taxiRequest = new TaxiBookingRequest();
            taxiRequest.setCustomerId(request.getCustomerId());
            taxiRequest.setTaxiId(request.getTaxiId());
            taxiRequest.setBookingDate(request.getDate());
            taxiRequest.setDepartureDate(request.getDate());
            taxiRequest.setDepartureLocation(request.getDepartureLocation() != null ? request.getDepartureLocation() : "Airport");
            taxiRequest.setDestination(request.getDestination() != null ? request.getDestination() : "Hotel");
            taxiRequest.setPassengerCount(request.getPassengerCount() != null ? request.getPassengerCount() : 1);
            
            TaxiBookingResponse taxiResponse = taxiServiceClient.createBooking(taxiRequest);
            taxiBookingId = taxiResponse.getId();
            log.info("Taxi booking created with ID: " + taxiBookingId);
            
            // Success - all bookings completed
            TravelAgentBookingResponse response = new TravelAgentBookingResponse(
                    hotelBookingId,
                    flightBookingId,
                    taxiBookingId,
                    "SUCCESS",
                    "Travel booking completed successfully"
            );
            
            log.info("Travel booking completed successfully");
            return Response.status(Response.Status.CREATED).entity(response).build();
            
        } catch (Exception e) {
            log.severe("Travel booking failed: " + e.getMessage());
            log.info("Executing compensation (rollback)...");
            
            // Compensation: Cancel successful bookings in reverse order
            try {
                if (taxiBookingId != null) {
                    log.info("Cancelling taxi booking " + taxiBookingId);
                    taxiServiceClient.cancelBooking(taxiBookingId);
                }
                
                if (flightBookingId != null) {
                    log.info("Cancelling flight booking " + flightBookingId);
                    bookingService.cancelBooking(flightBookingId);
                }
                
                if (hotelBookingId != null) {
                    log.info("Cancelling hotel booking " + hotelBookingId);
                    hotelServiceClient.cancelBooking(hotelBookingId);
                }
                
                log.info("Compensation completed successfully");
            } catch (Exception compensationError) {
                log.severe("Compensation failed: " + compensationError.getMessage());
            }
            
            // Return error response
            TravelAgentBookingResponse errorResponse = new TravelAgentBookingResponse(
                    hotelBookingId,
                    flightBookingId,
                    taxiBookingId,
                    "FAILED",
                    "Travel booking failed: " + e.getMessage() + ". All bookings have been cancelled."
            );
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorResponse)
                    .build();
        }
    }

    /**
     * Health check endpoint for Travel Agent service
     */
    @GET
    @Path("/health")
    @Operation(summary = "Health check", description = "Check if Travel Agent service is operational")
    public Response healthCheck() {
        return Response.ok("{\"status\": \"Travel Agent service is running\"}").build();
    }
}

