package uk.ac.newcastle.enterprisemiddleware.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for Travel Agent booking request
 */
public class TravelAgentBookingRequest {

    @NotNull(message = "Customer ID is required")
    @JsonProperty("customerId")
    private Long customerId;

    @NotNull(message = "Hotel ID is required")
    @JsonProperty("hotelId")
    private Long hotelId;

    @NotNull(message = "Flight Commodity ID is required")
    @JsonProperty("flightCommodityId")
    private Long flightCommodityId;

    @NotNull(message = "Date is required")
    @JsonProperty("date")
    private String date;

    public TravelAgentBookingRequest() {
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

    public Long getFlightCommodityId() {
        return flightCommodityId;
    }

    public void setFlightCommodityId(Long flightCommodityId) {
        this.flightCommodityId = flightCommodityId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

