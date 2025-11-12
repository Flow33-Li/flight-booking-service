package uk.ac.newcastle.enterprisemiddleware.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import uk.ac.newcastle.enterprisemiddleware.entity.Commodity;

import java.util.List;

/**
 * Repository for Commodity entity operations.
 */
@ApplicationScoped
public class CommodityRepository implements PanacheRepository<Commodity> {

    /**
     * Find all commodities with available quantity greater than zero.
     *
     * @return list of available commodities
     */
    public List<Commodity> findAvailableCommodities() {
        return list("quantity > 0");
    }

    /**
     * Find commodities by name (case-insensitive).
     *
     * @param name the commodity name
     * @return list of matching commodities
     */
    public List<Commodity> findByName(String name) {
        return list("LOWER(name) LIKE LOWER(?1)", "%" + name + "%");
    }
}

