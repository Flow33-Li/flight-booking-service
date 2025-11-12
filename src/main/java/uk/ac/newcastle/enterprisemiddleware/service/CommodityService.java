package uk.ac.newcastle.enterprisemiddleware.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import uk.ac.newcastle.enterprisemiddleware.entity.Commodity;
import uk.ac.newcastle.enterprisemiddleware.repository.CommodityRepository;

import java.util.List;
import java.util.logging.Logger;

/**
 * Service class for Commodity business logic.
 */
@ApplicationScoped
public class CommodityService {

    @Inject
    Logger log;

    @Inject
    CommodityRepository commodityRepository;

    /**
     * Get all commodities.
     *
     * @return list of all commodities
     */
    public List<Commodity> findAllCommodities() {
        log.info("Finding all commodities");
        return commodityRepository.listAll();
    }

    /**
     * Get a commodity by ID.
     *
     * @param id the commodity ID
     * @return the commodity
     * @throws WebApplicationException if commodity not found
     */
    public Commodity findCommodityById(Long id) {
        log.info("Finding commodity by id: " + id);
        Commodity commodity = commodityRepository.findById(id);
        if (commodity == null) {
            throw new WebApplicationException("Commodity with id " + id + " not found", Response.Status.NOT_FOUND);
        }
        return commodity;
    }

    /**
     * Get all available commodities (quantity > 0).
     *
     * @return list of available commodities
     */
    public List<Commodity> findAvailableCommodities() {
        log.info("Finding available commodities");
        return commodityRepository.findAvailableCommodities();
    }

    /**
     * Create a new commodity.
     *
     * @param commodity the commodity to create
     * @return the created commodity
     */
    @Transactional
    public Commodity createCommodity(@Valid Commodity commodity) {
        log.info("Creating commodity: " + commodity.getName());
        commodityRepository.persist(commodity);
        return commodity;
    }

    /**
     * Update an existing commodity.
     *
     * @param id        the commodity ID
     * @param commodity the updated commodity data
     * @return the updated commodity
     * @throws WebApplicationException if commodity not found
     */
    @Transactional
    public Commodity updateCommodity(Long id, @Valid Commodity commodity) {
        log.info("Updating commodity with id: " + id);
        
        Commodity existingCommodity = findCommodityById(id);
        
        existingCommodity.setName(commodity.getName());
        existingCommodity.setDescription(commodity.getDescription());
        existingCommodity.setPrice(commodity.getPrice());
        existingCommodity.setQuantity(commodity.getQuantity());
        
        return existingCommodity;
    }

    /**
     * Delete a commodity by ID.
     * This will cascade delete all bookings associated with this commodity.
     *
     * @param id the commodity ID
     * @throws WebApplicationException if commodity not found
     */
    @Transactional
    public void deleteCommodity(Long id) {
        log.info("Deleting commodity with id: " + id);
        
        Commodity commodity = findCommodityById(id);
        commodityRepository.delete(commodity);
        
        log.info("Commodity deleted successfully. Associated bookings were also deleted due to cascade.");
    }

    /**
     * Decrease commodity quantity (used when creating a booking).
     *
     * @param id the commodity ID
     * @throws WebApplicationException if not enough quantity available
     */
    @Transactional
    public void decreaseQuantity(Long id) {
        Commodity commodity = findCommodityById(id);
        if (commodity.getQuantity() <= 0) {
            throw new WebApplicationException("Commodity is out of stock", Response.Status.BAD_REQUEST);
        }
        commodity.setQuantity(commodity.getQuantity() - 1);
    }

    /**
     * Increase commodity quantity (used when canceling a booking).
     *
     * @param id the commodity ID
     */
    @Transactional
    public void increaseQuantity(Long id) {
        Commodity commodity = findCommodityById(id);
        commodity.setQuantity(commodity.getQuantity() + 1);
    }
}

