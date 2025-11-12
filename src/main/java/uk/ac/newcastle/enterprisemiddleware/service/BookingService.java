package uk.ac.newcastle.enterprisemiddleware.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import uk.ac.newcastle.enterprisemiddleware.entity.Booking;
import uk.ac.newcastle.enterprisemiddleware.entity.Commodity;
import uk.ac.newcastle.enterprisemiddleware.entity.Customer;
import uk.ac.newcastle.enterprisemiddleware.repository.BookingRepository;

import java.util.List;
import java.util.logging.Logger;

/**
 * Service class for Booking business logic.
 */
@ApplicationScoped
public class BookingService {

    @Inject
    Logger log;

    @Inject
    BookingRepository bookingRepository;

    @Inject
    CustomerService customerService;

    @Inject
    CommodityService commodityService;

    /**
     * Get all bookings.
     *
     * @return list of all bookings
     */
    public List<Booking> findAllBookings() {
        log.info("Finding all bookings");
        return bookingRepository.listAll();
    }

    /**
     * Get a booking by ID.
     *
     * @param id the booking ID
     * @return the booking
     * @throws WebApplicationException if booking not found
     */
    public Booking findBookingById(Long id) {
        log.info("Finding booking by id: " + id);
        Booking booking = bookingRepository.findById(id);
        if (booking == null) {
            throw new WebApplicationException("Booking with id " + id + " not found", Response.Status.NOT_FOUND);
        }
        return booking;
    }

    /**
     * Get all bookings for a customer.
     *
     * @param customerId the customer ID
     * @return list of bookings
     */
    public List<Booking> findBookingsByCustomerId(Long customerId) {
        log.info("Finding bookings for customer id: " + customerId);
        return bookingRepository.findByCustomerId(customerId);
    }

    /**
     * Create a new booking.
     *
     * @param customerId  the customer ID
     * @param commodityId the commodity ID
     * @return the created booking
     * @throws WebApplicationException if customer/commodity not found or duplicate booking
     */
    @Transactional
    public Booking createBooking(Long customerId, Long commodityId) {
        log.info("Creating booking for customer " + customerId + " and commodity " + commodityId);
        
        // Validate customer and commodity exist
        Customer customer = customerService.findCustomerById(customerId);
        Commodity commodity = commodityService.findCommodityById(commodityId);
        
        // Check if booking already exists
        if (bookingRepository.existsByCustomerAndCommodity(customerId, commodityId)) {
            throw new WebApplicationException("Booking already exists for this customer and commodity", 
                    Response.Status.CONFLICT);
        }
        
        // Check commodity availability
        if (commodity.getQuantity() <= 0) {
            throw new WebApplicationException("Commodity is out of stock", Response.Status.BAD_REQUEST);
        }
        
        // Create booking
        Booking booking = new Booking(customer, commodity);
        bookingRepository.persist(booking);
        
        // Decrease commodity quantity
        commodityService.decreaseQuantity(commodityId);
        
        log.info("Booking created successfully");
        return booking;
    }

    /**
     * Cancel a booking by ID.
     *
     * @param id the booking ID
     * @throws WebApplicationException if booking not found
     */
    @Transactional
    public void cancelBooking(Long id) {
        log.info("Canceling booking with id: " + id);
        
        Booking booking = findBookingById(id);
        
        // Increase commodity quantity back
        commodityService.increaseQuantity(booking.getCommodity().getId());
        
        // Delete booking
        bookingRepository.delete(booking);
        
        log.info("Booking canceled successfully");
    }
}

