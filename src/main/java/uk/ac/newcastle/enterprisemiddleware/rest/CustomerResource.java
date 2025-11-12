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
import uk.ac.newcastle.enterprisemiddleware.entity.Customer;
import uk.ac.newcastle.enterprisemiddleware.service.CustomerService;

import java.util.List;
import java.util.logging.Logger;

/**
 * REST resource for Customer operations.
 */
@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Customer", description = "Customer management operations")
public class CustomerResource {

    @Inject
    Logger log;

    @Inject
    CustomerService customerService;

    /**
     * Get all customers.
     *
     * @return list of all customers
     */
    @GET
    @Operation(summary = "Get all customers", description = "Returns a list of all customers")
    @APIResponse(responseCode = "200", description = "Successful retrieval of customers",
            content = @Content(schema = @Schema(implementation = Customer.class)))
    public Response getAllCustomers() {
        log.info("GET /customers - Getting all customers");
        List<Customer> customers = customerService.findAllCustomers();
        return Response.ok(customers).build();
    }

    /**
     * Get a customer by ID.
     *
     * @param id the customer ID
     * @return the customer
     */
    @GET
    @Path("/{id}")
    @Operation(summary = "Get customer by ID", description = "Returns a single customer by ID")
    @APIResponse(responseCode = "200", description = "Customer found",
            content = @Content(schema = @Schema(implementation = Customer.class)))
    @APIResponse(responseCode = "404", description = "Customer not found")
    public Response getCustomerById(@PathParam("id") Long id) {
        log.info("GET /customers/" + id + " - Getting customer by id");
        Customer customer = customerService.findCustomerById(id);
        return Response.ok(customer).build();
    }

    /**
     * Create a new customer.
     *
     * @param customer the customer to create
     * @return the created customer
     */
    @POST
    @Operation(summary = "Create a new customer", description = "Creates a new customer")
    @APIResponse(responseCode = "201", description = "Customer created successfully",
            content = @Content(schema = @Schema(implementation = Customer.class)))
    @APIResponse(responseCode = "400", description = "Invalid customer data")
    @APIResponse(responseCode = "409", description = "Customer with this email already exists")
    public Response createCustomer(@Valid Customer customer) {
        log.info("POST /customers - Creating new customer");
        Customer created = customerService.createCustomer(customer);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    /**
     * Update an existing customer.
     *
     * @param id       the customer ID
     * @param customer the updated customer data
     * @return the updated customer
     */
    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a customer", description = "Updates an existing customer")
    @APIResponse(responseCode = "200", description = "Customer updated successfully",
            content = @Content(schema = @Schema(implementation = Customer.class)))
    @APIResponse(responseCode = "404", description = "Customer not found")
    @APIResponse(responseCode = "409", description = "Email already exists")
    public Response updateCustomer(@PathParam("id") Long id, @Valid Customer customer) {
        log.info("PUT /customers/" + id + " - Updating customer");
        Customer updated = customerService.updateCustomer(id, customer);
        return Response.ok(updated).build();
    }

    /**
     * Delete a customer by ID.
     * This will cascade delete all bookings associated with this customer.
     *
     * @param id the customer ID
     * @return no content response
     */
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a customer", description = "Deletes a customer and all associated bookings")
    @APIResponse(responseCode = "204", description = "Customer deleted successfully")
    @APIResponse(responseCode = "404", description = "Customer not found")
    public Response deleteCustomer(@PathParam("id") Long id) {
        log.info("DELETE /customers/" + id + " - Deleting customer");
        customerService.deleteCustomer(id);
        return Response.noContent().build();
    }
}

