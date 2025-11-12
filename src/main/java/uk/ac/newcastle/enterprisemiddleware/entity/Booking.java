package uk.ac.newcastle.enterprisemiddleware.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Booking entity representing a booking in the flight booking system.
 */
@Entity
@Table(name = "Booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "bookingDate")
    private LocalDate bookingDate;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "commodity_id")
    private Commodity commodity;

    // Constructors
    public Booking() {
        this.bookingDate = LocalDate.now();
    }

    public Booking(Customer customer, Commodity commodity) {
        this.bookingDate = LocalDate.now();
        this.customer = customer;
        this.commodity = commodity;
    }

    public Booking(LocalDate bookingDate, Customer customer, Commodity commodity) {
        this.bookingDate = bookingDate;
        this.customer = customer;
        this.commodity = commodity;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Commodity getCommodity() {
        return commodity;
    }

    public void setCommodity(Commodity commodity) {
        this.commodity = commodity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", bookingDate=" + bookingDate +
                ", customerId=" + (customer != null ? customer.getId() : null) +
                ", commodityId=" + (commodity != null ? commodity.getId() : null) +
                '}';
    }
}

