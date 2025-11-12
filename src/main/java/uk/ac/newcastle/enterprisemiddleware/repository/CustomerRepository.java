package uk.ac.newcastle.enterprisemiddleware.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import uk.ac.newcastle.enterprisemiddleware.entity.Customer;

import java.util.Optional;

/**
 * Repository for Customer entity operations.
 */
@ApplicationScoped
public class CustomerRepository implements PanacheRepository<Customer> {

    /**
     * Find a customer by email address.
     *
     * @param email the email address
     * @return Optional containing the customer if found
     */
    public Optional<Customer> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    /**
     * Check if a customer exists by email.
     *
     * @param email the email address
     * @return true if customer exists
     */
    public boolean existsByEmail(String email) {
        return count("email", email) > 0;
    }
}

