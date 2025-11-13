# Flight Booking Service

A RESTful API service for flight booking system built with Quarkus.

## Features

### Core Functionality
- **Customer Management**: Create, read, update, and delete customers
- **Commodity Management**: Create, read, update, and delete commodities (flights)
- **Booking Management**: Create, read, and cancel bookings

### Advanced Features
- **Cascade Delete**: Automatically delete related bookings when a customer or commodity is deleted
- **GuestBooking Endpoint**: Create customer and booking in a single transaction using manual JTA transaction management
- **Swagger UI**: Complete API documentation with interactive testing interface
- **REST Assured Tests**: Comprehensive unit and integration tests

## Tech Stack

- **Framework**: Quarkus 3.15.1
- **Java Version**: Java 17
- **Database**: H2 (in-memory database)
- **ORM**: Hibernate with Panache
- **API Documentation**: OpenAPI/Swagger
- **Testing**: JUnit 5 + REST Assured
- **Transaction Management**: JTA (Java Transaction API)

## Running Locally

### Prerequisites
- Java 17
- Maven 3.9+

### Steps

1. **Clone the repository**
```bash
git clone <your-repo-url>
cd flight-booking-service
```

2. **Start the application (dev mode)**
```bash
./mvnw quarkus:dev
```

3. **Access the application**
- API endpoints: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui
- OpenAPI spec: http://localhost:8080/openapi

### Run Tests
```bash
./mvnw test
```

## API Endpoints

### Customers
- `GET /customers` - Get all customers
- `GET /customers/{id}` - Get customer by ID
- `POST /customers` - Create new customer
- `PUT /customers/{id}` - Update customer
- `DELETE /customers/{id}` - Delete customer (cascade delete related bookings)

### Commodities
- `GET /commodities` - Get all commodities
- `GET /commodities/available` - Get available commodities
- `GET /commodities/{id}` - Get commodity by ID
- `POST /commodities` - Create new commodity
- `PUT /commodities/{id}` - Update commodity
- `DELETE /commodities/{id}` - Delete commodity (cascade delete related bookings)

### Bookings
- `GET /bookings` - Get all bookings
- `GET /bookings/{id}` - Get booking by ID
- `GET /bookings/customer/{customerId}` - Get all bookings for a customer
- `POST /bookings?customerId={id}&commodityId={id}` - Create new booking
- `DELETE /bookings/{id}` - Cancel booking

### Guest Bookings
- `POST /guest-bookings` - Create customer and booking in a single transaction (manual JTA transaction management)

## API Usage Examples

### Create Customer
```bash
curl -X POST http://localhost:8080/customers \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phoneNumber": "1234567890"
  }'
```

### Create Booking
```bash
curl -X POST "http://localhost:8080/bookings?customerId=1&commodityId=1"
```

### Create Guest Booking (Transactional)
```bash
curl -X POST http://localhost:8080/guest-bookings \
  -H "Content-Type: application/json" \
  -d '{
    "customer": {
      "firstName": "Guest",
      "lastName": "User",
      "email": "guest@example.com",
      "phoneNumber": "9876543210"
    },
    "commodityId": 1
  }'
```

## Data Models

### Customer
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "1234567890"
}
```

### Commodity
```json
{
  "id": 1,
  "name": "Flight to London",
  "description": "Direct flight from NYC to London",
  "price": 599.99,
  "quantity": 50
}
```

### Booking
```json
{
  "id": 1,
  "bookingDate": "2025-11-12",
  "customer": { ... },
  "commodity": { ... }
}
```

## Deploying to OpenShift

### Prerequisites
1. Red Hat OpenShift account
2. OpenShift project configured
3. Code pushed to GitHub

### Deployment Steps

Reference documentation:
- [OpenShift Setup Guide](https://github.com/NewcastleComputingScience/CSC8104-Quarkus-Specification/blob/main/tutorial.asciidoc)
- [Redeployment Guide](https://github.com/NewcastleComputingScience/CSC8104-Quarkus-Specification/blob/main/serverless-redeploy.md)

### Quick Deployment Process

1. **Push code to GitHub**
```bash
git add .
git commit -m "Initial commit"
git push origin main
```

2. **Create application in OpenShift**
- Log in to OpenShift console
- Select "Import from Git"
- Enter GitHub repository URL
- Select "Knative Service"
- Click "Create"

3. **Redeploy after code updates**
- Push code to GitHub
- Trigger new build in OpenShift (Builds → BuildConfigs → Start build)
- Update Knative Service `buildVersion` annotation to trigger redeployment

## Project Structure

```
flight-booking-service/
├── src/
│   ├── main/
│   │   ├── java/uk/ac/newcastle/enterprisemiddleware/
│   │   │   ├── entity/          # Entity classes
│   │   │   ├── repository/      # Data access layer
│   │   │   ├── service/         # Business logic layer
│   │   │   └── rest/            # REST endpoints
│   │   │       └── dto/         # Data transfer objects
│   │   └── resources/
│   │       ├── application.properties
│   │       └── import.sql       # Initial data
│   └── test/
│       └── java/                # REST Assured tests
├── .s2i/                        # OpenShift S2I configuration
├── .mvn/                        # Maven wrapper
├── pom.xml
└── README.md
```

## Implementation Details

### Cascade Delete
Uses JPA `@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)` annotation.
When a Customer or Commodity is deleted, all related Bookings are automatically deleted.

### JTA Transaction Management
`GuestBookingResource` uses `UserTransaction` API for manual transaction management:
- Manually begin transaction (`userTransaction.begin()`)
- Execute business operations
- Commit on success (`userTransaction.commit()`)
- Rollback on failure (`userTransaction.rollback()`)
