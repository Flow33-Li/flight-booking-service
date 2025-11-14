package uk.ac.newcastle.enterprisemiddleware.client;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request DTO for Hotel Booking
 */
public class HotelBookingRequest {

    @JsonProperty("customerId")
    private Long customerId;

    @JsonProperty("hotelId")
    private Long hotelId;

    @JsonProperty("date")
    private String date;

    public HotelBookingRequest() {
    }

    public HotelBookingRequest(Long customerId, Long hotelId, String date) {
        this.customerId = customerId;
        this.hotelId = hotelId;
        this.date = date;
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

