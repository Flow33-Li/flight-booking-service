package uk.ac.newcastle.enterprisemiddleware.rest.dto;

import uk.ac.newcastle.enterprisemiddleware.entity.Booking;
import uk.ac.newcastle.enterprisemiddleware.entity.Customer;

/**
 * DTO for GuestBooking creation response.
 */
public class GuestBookingResponse {

    private Customer customer;
    private Booking booking;
    private String message;

    // Constructors
    public GuestBookingResponse() {
    }

    public GuestBookingResponse(Customer customer, Booking booking) {
        this.customer = customer;
        this.booking = booking;
        this.message = "Guest booking created successfully in a single transaction";
    }

    // Getters and Setters
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "GuestBookingResponse{" +
                "customer=" + customer +
                ", booking=" + booking +
                ", message='" + message + '\'' +
                '}';
    }
}

