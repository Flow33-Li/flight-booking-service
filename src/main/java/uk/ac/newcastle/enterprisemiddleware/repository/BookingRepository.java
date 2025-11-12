package uk.ac.newcastle.enterprisemiddleware.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import uk.ac.newcastle.enterprisemiddleware.entity.Booking;

import java.util.List;

/**
 * Repository for Booking entity operations.
 */
@ApplicationScoped
public class BookingRepository implements PanacheRepository<Booking> {

    /**
     * Find all bookings for a specific customer.
     *
     * @param customerId the customer ID
     * @return list of bookings
     */
    public List<Booking> findByCustomerId(Long customerId) {
        return list("customer.id", customerId);
    }

    /**
     * Find all bookings for a specific commodity.
     *
     * @param commodityId the commodity ID
     * @return list of bookings
     */
    public List<Booking> findByCommodityId(Long commodityId) {
        return list("commodity.id", commodityId);
    }

    /**
     * Check if a booking exists for a customer and commodity.
     *
     * @param customerId  the customer ID
     * @param commodityId the commodity ID
     * @return true if booking exists
     */
    public boolean existsByCustomerAndCommodity(Long customerId, Long commodityId) {
        return count("customer.id = ?1 and commodity.id = ?2", customerId, commodityId) > 0;
    }
}

