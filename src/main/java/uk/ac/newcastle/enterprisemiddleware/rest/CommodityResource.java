package uk.ac.newcastle.enterprisemiddleware.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import uk.ac.newcastle.enterprisemiddleware.entity.Commodity;
import uk.ac.newcastle.enterprisemiddleware.service.CommodityService;

import java.util.List;
import java.util.logging.Logger;

/**
 * REST resource for Commodity operations.
 */
@Path("/commodities")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Commodity", description = "Commodity (Flight) management operations")
public class CommodityResource {

    @Inject
    Logger log;

    @Inject
    CommodityService commodityService;

    /**
     * Get all commodities.
     *
     * @return list of all commodities
     */
    @GET
    @Operation(summary = "Get all commodities", description = "Returns a list of all commodities (flights)")
    @APIResponse(responseCode = "200", description = "Successful retrieval of commodities",
            content = @Content(schema = @Schema(implementation = Commodity.class)))
    public Response getAllCommodities() {
        log.info("GET /commodities - Getting all commodities");
        List<Commodity> commodities = commodityService.findAllCommodities();
        return Response.ok(commodities).build();
    }

    /**
     * Get available commodities only.
     *
     * @return list of available commodities
     */
    @GET
    @Path("/available")
    @Operation(summary = "Get available commodities", description = "Returns only commodities with quantity > 0")
    @APIResponse(responseCode = "200", description = "Successful retrieval of available commodities",
            content = @Content(schema = @Schema(implementation = Commodity.class)))
    public Response getAvailableCommodities() {
        log.info("GET /commodities/available - Getting available commodities");
        List<Commodity> commodities = commodityService.findAvailableCommodities();
        return Response.ok(commodities).build();
    }

    /**
     * Get a commodity by ID.
     *
     * @param id the commodity ID
     * @return the commodity
     */
    @GET
    @Path("/{id}")
    @Operation(summary = "Get commodity by ID", description = "Returns a single commodity by ID")
    @APIResponse(responseCode = "200", description = "Commodity found",
            content = @Content(schema = @Schema(implementation = Commodity.class)))
    @APIResponse(responseCode = "404", description = "Commodity not found")
    public Response getCommodityById(@PathParam("id") Long id) {
        log.info("GET /commodities/" + id + " - Getting commodity by id");
        Commodity commodity = commodityService.findCommodityById(id);
        return Response.ok(commodity).build();
    }

    /**
     * Create a new commodity.
     *
     * @param commodity the commodity to create
     * @return the created commodity
     */
    @POST
    @Operation(summary = "Create a new commodity", description = "Creates a new commodity (flight)")
    @APIResponse(responseCode = "201", description = "Commodity created successfully",
            content = @Content(schema = @Schema(implementation = Commodity.class)))
    @APIResponse(responseCode = "400", description = "Invalid commodity data")
    public Response createCommodity(@Valid Commodity commodity) {
        log.info("POST /commodities - Creating new commodity");
        Commodity created = commodityService.createCommodity(commodity);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    /**
     * Update an existing commodity.
     *
     * @param id        the commodity ID
     * @param commodity the updated commodity data
     * @return the updated commodity
     */
    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a commodity", description = "Updates an existing commodity")
    @APIResponse(responseCode = "200", description = "Commodity updated successfully",
            content = @Content(schema = @Schema(implementation = Commodity.class)))
    @APIResponse(responseCode = "404", description = "Commodity not found")
    public Response updateCommodity(@PathParam("id") Long id, @Valid Commodity commodity) {
        log.info("PUT /commodities/" + id + " - Updating commodity");
        Commodity updated = commodityService.updateCommodity(id, commodity);
        return Response.ok(updated).build();
    }

    /**
     * Delete a commodity by ID.
     * This will cascade delete all bookings associated with this commodity.
     *
     * @param id the commodity ID
     * @return no content response
     */
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a commodity", description = "Deletes a commodity and all associated bookings")
    @APIResponse(responseCode = "204", description = "Commodity deleted successfully")
    @APIResponse(responseCode = "404", description = "Commodity not found")
    public Response deleteCommodity(@PathParam("id") Long id) {
        log.info("DELETE /commodities/" + id + " - Deleting commodity");
        commodityService.deleteCommodity(id);
        return Response.noContent().build();
    }
}

