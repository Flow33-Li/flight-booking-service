package uk.ac.newcastle.enterprisemiddleware.client;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO for Hotel Booking
 */
public class HotelBookingResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("customerId")
    private Long customerId;

    @JsonProperty("hotelId")
    private Long hotelId;

    @JsonProperty("date")
    private String date;

    public HotelBookingResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

