package uk.ac.newcastle.enterprisemiddleware.client;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * REST Client for external Taxi Service
 */
@RegisterRestClient(configKey = "taxi-api")
@Path("/bookings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface TaxiServiceClient {

    /**
     * Create a taxi booking
     * @param request Taxi booking request
     * @return Taxi booking response
     */
    @POST
    TaxiBookingResponse createBooking(TaxiBookingRequest request);

    /**
     * Cancel a taxi booking
     * @param id Booking ID
     * @return Response
     */
    @DELETE
    @Path("/{id}")
    Response cancelBooking(@PathParam("id") Long id);
}

