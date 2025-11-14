package uk.ac.newcastle.enterprisemiddleware.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * REST Client for Hotel Booking Service
 */
@Path("/bookings")
@RegisterRestClient(configKey = "hotel-api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface HotelServiceClient {

    /**
     * Create a hotel booking
     */
    @POST
    HotelBookingResponse createBooking(HotelBookingRequest request);

    /**
     * Cancel a hotel booking
     */
    @DELETE
    @Path("/{id}")
    void cancelBooking(@PathParam("id") Long id);
}

