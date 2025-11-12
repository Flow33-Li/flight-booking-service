package uk.ac.newcastle.enterprisemiddleware.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import uk.ac.newcastle.enterprisemiddleware.entity.Customer;

/**
 * DTO for GuestBooking creation request.
 */
public class GuestBookingRequest {

    @NotNull(message = "Customer information is required")
    @Valid
    private Customer customer;

    @NotNull(message = "Commodity ID is required")
    private Long commodityId;

    // Constructors
    public GuestBookingRequest() {
    }

    public GuestBookingRequest(Customer customer, Long commodityId) {
        this.customer = customer;
        this.commodityId = commodityId;
    }

    // Getters and Setters
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Long getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(Long commodityId) {
        this.commodityId = commodityId;
    }

    @Override
    public String toString() {
        return "GuestBookingRequest{" +
                "customer=" + customer +
                ", commodityId=" + commodityId +
                '}';
    }
}

