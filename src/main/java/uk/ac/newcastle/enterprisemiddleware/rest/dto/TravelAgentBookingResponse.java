package uk.ac.newcastle.enterprisemiddleware.rest.dto;

/**
 * DTO for Travel Agent booking response
 */
public class TravelAgentBookingResponse {

    private Long hotelBookingId;
    private Long flightBookingId;
    private Long taxiBookingId;
    private String status;
    private String message;

    public TravelAgentBookingResponse() {
    }

    public TravelAgentBookingResponse(Long hotelBookingId, Long flightBookingId, Long taxiBookingId, String status, String message) {
        this.hotelBookingId = hotelBookingId;
        this.flightBookingId = flightBookingId;
        this.taxiBookingId = taxiBookingId;
        this.status = status;
        this.message = message;
    }

    public Long getHotelBookingId() {
        return hotelBookingId;
    }

    public void setHotelBookingId(Long hotelBookingId) {
        this.hotelBookingId = hotelBookingId;
    }

    public Long getFlightBookingId() {
        return flightBookingId;
    }

    public void setFlightBookingId(Long flightBookingId) {
        this.flightBookingId = flightBookingId;
    }

    public Long getTaxiBookingId() {
        return taxiBookingId;
    }

    public void setTaxiBookingId(Long taxiBookingId) {
        this.taxiBookingId = taxiBookingId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

