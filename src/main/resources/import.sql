-- Sample data for testing
-- This file will be executed on application startup

-- Insert sample customers
INSERT INTO Customer (id, firstName, lastName, email, phoneNumber) VALUES (1, 'John', 'Doe', 'john.doe@example.com', '1234567890');
INSERT INTO Customer (id, firstName, lastName, email, phoneNumber) VALUES (2, 'Jane', 'Smith', 'jane.smith@example.com', '0987654321');

-- Insert sample commodities (flights)
INSERT INTO Commodity (id, name, description, price, quantity) VALUES (1, 'Flight to London', 'Direct flight from NYC to London', 599.99, 50);
INSERT INTO Commodity (id, name, description, price, quantity) VALUES (2, 'Flight to Paris', 'Direct flight from NYC to Paris', 699.99, 30);
INSERT INTO Commodity (id, name, description, price, quantity) VALUES (3, 'Flight to Tokyo', 'Direct flight from NYC to Tokyo', 1299.99, 20);

-- Insert sample bookings
INSERT INTO Booking (id, bookingDate, customer_id, commodity_id) VALUES (1, '2025-11-12', 1, 1);
INSERT INTO Booking (id, bookingDate, customer_id, commodity_id) VALUES (2, '2025-11-12', 2, 2);

