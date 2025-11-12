package uk.ac.newcastle.enterprisemiddleware.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import uk.ac.newcastle.enterprisemiddleware.entity.Customer;
import uk.ac.newcastle.enterprisemiddleware.repository.CustomerRepository;

import java.util.List;
import java.util.logging.Logger;

/**
 * Service class for Customer business logic.
 */
@ApplicationScoped
public class CustomerService {

    @Inject
    Logger log;

    @Inject
    CustomerRepository customerRepository;

    /**
     * Get all customers.
     *
     * @return list of all customers
     */
    public List<Customer> findAllCustomers() {
        log.info("Finding all customers");
        return customerRepository.listAll();
    }

    /**
     * Get a customer by ID.
     *
     * @param id the customer ID
     * @return the customer
     * @throws WebApplicationException if customer not found
     */
    public Customer findCustomerById(Long id) {
        log.info("Finding customer by id: " + id);
        Customer customer = customerRepository.findById(id);
        if (customer == null) {
            throw new WebApplicationException("Customer with id " + id + " not found", Response.Status.NOT_FOUND);
        }
        return customer;
    }

    /**
     * Create a new customer.
     *
     * @param customer the customer to create
     * @return the created customer
     * @throws WebApplicationException if email already exists
     */
    @Transactional
    public Customer createCustomer(@Valid Customer customer) {
        log.info("Creating customer: " + customer.getEmail());
        
        // Check if email already exists
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new WebApplicationException("Customer with email " + customer.getEmail() + " already exists", 
                    Response.Status.CONFLICT);
        }
        
        customerRepository.persist(customer);
        return customer;
    }

    /**
     * Update an existing customer.
     *
     * @param id       the customer ID
     * @param customer the updated customer data
     * @return the updated customer
     * @throws WebApplicationException if customer not found or email conflict
     */
    @Transactional
    public Customer updateCustomer(Long id, @Valid Customer customer) {
        log.info("Updating customer with id: " + id);
        
        Customer existingCustomer = findCustomerById(id);
        
        // Check if email is being changed and if new email already exists
        if (!existingCustomer.getEmail().equals(customer.getEmail()) 
                && customerRepository.existsByEmail(customer.getEmail())) {
            throw new WebApplicationException("Customer with email " + customer.getEmail() + " already exists", 
                    Response.Status.CONFLICT);
        }
        
        existingCustomer.setFirstName(customer.getFirstName());
        existingCustomer.setLastName(customer.getLastName());
        existingCustomer.setEmail(customer.getEmail());
        existingCustomer.setPhoneNumber(customer.getPhoneNumber());
        
        return existingCustomer;
    }

    /**
     * Delete a customer by ID.
     * This will cascade delete all bookings associated with this customer.
     *
     * @param id the customer ID
     * @throws WebApplicationException if customer not found
     */
    @Transactional
    public void deleteCustomer(Long id) {
        log.info("Deleting customer with id: " + id);
        
        Customer customer = findCustomerById(id);
        customerRepository.delete(customer);
        
        log.info("Customer deleted successfully. Associated bookings were also deleted due to cascade.");
    }
}

