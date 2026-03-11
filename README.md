# 🎬 Movie Ticket Booking System - Backend API

> **⚠️ IMPORTANT NOTICE - API ENDPOINT VERIFICATION**
> 
> This README follows standardized REST conventions. Please verify endpoint paths match your implementation before testing:
> - Check actual Controller mappings in your code
> - Verify DTO field names and enum types
> - Test endpoints using Postman before production
> - Cross-reference with application logs during startup

A comprehensive **Spring Boot REST API** for a movie ticket booking system with complete functionality including user management, movie catalog, theatre management, show scheduling, seat booking, payment processing, and ticket verification.

---

## 📋 Table of Contents

- [Features](#-features)
- [Technologies Used](#technologies-used)
- [System Architecture](#system-architecture)
- [Configuration](#configuration)
- [API Documentation](#-api-documentation)
- [Testing Guide](#-testing-guide)
- [Module Details](#-module-details)
- [Business Rules](#-business-rules)
- [Scheduled Jobs](#-scheduled-jobs)
- [Error Handling](#-error-handling)

---

## 🚀 Features

### Core Functionality
- ✅ **User Management** - Registration, authentication, role-based access
- ✅ **City/Location Management** - Multi-city support
- ✅ **Movie Catalog** - Complete movie database with search and filters
- ✅ **Theatre Management** - Multiple theatres with screen management
- ✅ **Show Scheduling** - Flexible show time management
- ✅ **Seat Management** - Dynamic seat layouts with bulk creation
- ✅ **Smart Booking System** - Temporary seat locking (10 minutes)
- ✅ **Payment Processing** - Multiple payment methods support

### Advanced Features
- 🎯 **Dynamic Pricing** - Based on seat type, day, and show time
- 🔒 **Seat Locking Mechanism** - Prevents double booking
- ⏰ **Auto-unlock Expired Locks** - Scheduled cleanup jobs
- 🔄 **Show Status Management** - Auto-completion of shows
- 📊 **Real-time Availability** - Live seat availability tracking

---

## 🛠️ Technologies Used

### Backend Framework
- **Java 17** - Programming language
- **Spring Boot 3.2.0** - Application framework
- **Spring Data JPA** - Data persistence
- **Hibernate** - ORM framework
- **Maven** - Dependency management

### Database
- **MySQL 8.0** - Relational database

### Libraries & Tools
- **Lombok** - Reduce boilerplate code
- **ModelMapper** - Object mapping
- **Spring Validation** - Request validation
- **Spring Scheduling** - Background jobs

---

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Client Layer                            │
│  (Web Browser / Mobile App / Third-party Integration)       │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                  REST API Layer                             │
│  (Controllers - Request/Response Handling)                  │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                  Service Layer                              │
│  (Business Logic - Validation, Processing)                  │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                  Repository Layer                           │
│  (Data Access - JPA Repositories)                           │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                  Database Layer                             │
│  (MySQL - 12 Tables)                                        │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│              Background Services (Parallel)                 │
│  - Seat Lock Cleanup (Every 5 min)                          │                          │
│  - Show Status Update (Every hour)                          │
└─────────────────────────────────────────────────────────────┘
```

---


### Installation Steps

1. **Clone the Repository**
```bash
git clone https://github.com/yourusername/movie-ticket-booking.git
cd movie-ticket-booking
```

2. **Create MySQL Database**
```sql
CREATE DATABASE movie_booking_db;
```

3. **Configure Database Connection**

Edit `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/movie_booking_db
    username: your_mysql_username
    password: your_mysql_password
```

4. **Build the Project**
```bash
mvn clean install
```

5. **Run the Application**
```bash
mvn spring-boot:run
```

6. **Verify Installation**

The application will start on: `http://localhost:8080/api`


---

## 💾 Database Setup

### Database Schema

The system uses **12 interconnected tables**:

```sql
-- Core Tables
users                  -- User accounts
cities                 -- City/location data
movies                 -- Movie catalog
theatres               -- Theatre information
screens                -- Screen details per theatre
seats                  -- Seat layout per screen

-- Operational Tables
shows                  -- Show scheduling
show_seats             -- Show-specific seat availability (CRITICAL)
bookings               -- Booking records
booking_seats          -- Booking-seat mapping (CRITICAL)
payments               -- Payment transactions
```

### Entity Relationships

```
User ─────┐
          │
          ├──> Booking ──> Payment
          │         │
          │         └──> BookingSeat
          │                   │
City ──> Theatre ──> Screen ─┴──> Seat
                       │           │
                       └──> Show ──┴──> ShowSeat
                              │
Movie ────────────────────────┘
```

### Sample Data Script

```sql
-- Insert Sample City
INSERT INTO cities (name, state, country, zip_code, active) 
VALUES ('Mumbai', 'Maharashtra', 'India', '400001', true);

-- Insert Sample Movie
INSERT INTO movies (title, description, duration_minutes, genre, language, 
                   release_date, director, rating, status, active) 
VALUES ('Inception', 'A thief who steals corporate secrets', 148, 
        'Sci-Fi', 'English', '2024-12-25', 'Christopher Nolan', 
        8.8, 'NOW_SHOWING', true);

-- Insert Sample User
INSERT INTO users (name, email, phone, password, role, active) 
VALUES ('John Doe', 'john@example.com', '9876543210', 
        'password123', 'CUSTOMER', true);
```

---

## ⚙️ Configuration

### Application Properties

Located in `src/main/resources/application.yml`:

```yaml
# Server Configuration
server:
  port: 8080
  servlet:
    context-path: /api

# Database Configuration
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/movie_booking_db?createDatabaseIfNotExist=true
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  # JPA Configuration
  jpa:
    hibernate:
      ddl-auto: update  # Change to 'validate' in production
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        format_sql: true

# Business Rules Configuration
app:
  seat-lock-duration: 10          # Minutes
  qr-expiry-duration: 30          # Minutes
  qr-code-directory: src/main/resources/static/qr-codes/

# Scheduler Configuration
scheduler:
  seat-lock-cleanup-cron: "0 */5 * * * ?"     # Every 5 minutes
  qr-expiry-cleanup-cron: "0 */10 * * * ?"    # Every 10 minutes
  show-status-update-cron: "0 0 * * * ?"      # Every hour
```

---

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api
```

### API Endpoints Overview

#### 👤 User Management
```http
POST   /users                    # Create user
GET    /users                    # Get all users
GET    /users/{id}               # Get user by ID
GET    /users/email/{email}      # Get user by email
GET    /users/role/{role}        # Get users by role
PUT    /users/{id}               # Update user
DELETE /users/{id}               # Delete user
PATCH  /users/{id}/activate      # Activate user
PATCH  /users/{id}/deactivate    # Deactivate user
```

#### 🏙️ City Management
```http
POST   /cities                   # Create city
GET    /cities                   # Get all cities
GET    /cities/{id}              # Get city by ID
GET    /cities/search?name=      # Search cities
GET    /cities/state/{state}     # Get cities by state
PUT    /cities/{id}              # Update city
DELETE /cities/{id}              # Delete city
```

#### 🎬 Movie Management
```http
POST   /movies                   # Create movie
GET    /movies                   # Get all movies
GET    /movies/{id}              # Get movie by ID
GET    /movies/now-showing        # Get currently showing movies
GET    /movies/search?title=     # Search movies
GET    /movies/language/{lang}   # Get movies by language
GET    /movies/genre/{genre}     # Get movies by genre
PUT    /movies/{id}              # Update movie
DELETE /movies/{id}              # Delete movie
```

#### 🏛️ Theatre Management
```http
POST   /theatres                 # Create theatre
GET    /theatres                 # Get all theatres
GET    /theatres/{id}            # Get theatre by ID
GET    /theatres/city/{cityId}   # Get theatres by city
GET    /theatres/search?name=    # Search theatres
PUT    /theatres/{id}            # Update theatre
DELETE /theatres/{id}            # Delete theatre
```

#### 🎭 Screen Management
```http
POST   /screens                  # Create screen
GET    /screens                  # Get all screens
GET    /screens/{id}             # Get screen by ID
GET    /screens/theatre/{theatreId}  # Get screens by theatre
PUT    /screens/{id}             # Update screen
DELETE /screens/{id}             # Delete screen
```

#### 💺 Seat Management
```http
POST   /seats                    # Create single seat
POST   /seats/bulk               # Create bulk seats (row-wise)
GET    /seats                    # Get all seats
GET    /seats/{id}               # Get seat by ID
GET    /seats/screen/{screenId}  # Get seats by screen
GET    /seats/screen/{screenId}/row/{row}  # Get seats by row
PUT    /seats/{id}               # Update seat
DELETE /seats/{id}               # Delete seat
```

#### 🎫 Show Management
```http
POST   /shows                    # Create show
GET    /shows                    # Get all shows
GET    /shows/{id}               # Get show by ID
GET    /shows/{id}/details       # Get show with complete details
GET    /shows/movie/{movieId}    # Get shows by movie
GET    /shows/movie/{movieId}/city/{cityId}  # Get shows by movie & city
GET    /shows/date/{date}        # Get shows by date
PUT    /shows/{id}               # Update show
DELETE /shows/{id}               # Delete show
PATCH  /shows/{id}/cancel        # Cancel show
```

#### 🪑 ShowSeat Management
```http
POST   /show-seats/initialize/{showId}      # Initialize show seats
GET    /show-seats/layout/{showId}          # Get seat layout
POST   /show-seats/lock                     # Lock seats
POST   /show-seats/unlock                   # Unlock seats
GET    /show-seats/available-count/{showId} # Get available seats count
```

#### 📋 Booking Management
```http
POST   /bookings                 # Create booking
GET    /bookings                 # Get all bookings
GET    /bookings/{id}            # Get booking by ID
GET    /bookings/{id}/details    # Get booking with full details
GET    /bookings/number/{bookingNumber}         # Get booking by number
GET    /bookings/number/{bookingNumber}/details # Get full details by number
GET    /bookings/user/{userId}   # Get bookings by user
PATCH  /bookings/{id}/confirm    # Confirm booking
PATCH  /bookings/{id}/cancel     # Cancel booking
```

#### 💳 Payment Management
```http
POST   /payments/process         # Process payment
GET    /payments/{id}            # Get payment by ID
GET    /payments/transaction/{txnId}  # Get payment by transaction ID
GET    /payments/booking/{bookingId}  # Get payment by booking
GET    /payments/user/{userId}   # Get payments by user
PATCH  /payments/{id}/status     # Update payment status
```

---

## 🧪 Testing Guide

### Complete Booking Workflow (Postman)

#### **Step 1: Create User**
```http
POST http://localhost:8080/api/users
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "9876543210",
  "role": "CUSTOMER"
}
```

#### **Step 2: Create City**
```http
POST http://localhost:8080/api/cities
Content-Type: application/json

{
  "name": "Mumbai",
  "state": "Maharashtra",
  "country": "India",
  "zipCode": "400001"
}
```

#### **Step 3: Create Movie**
```http
POST http://localhost:8080/api/movies
Content-Type: application/json

{
  "title": "Inception",
  "description": "A thief who steals corporate secrets through dream-sharing technology",
  "durationMinutes": 148,
  "genre": "Sci-Fi",
  "language": "English",
  "releaseDate": "2024-12-25",
  "director": "Christopher Nolan",
  "cast": "Leonardo DiCaprio, Tom Hardy",
  "rating": 8.8,
  "status": "NOW_SHOWING"
}
```

#### **Step 4: Create Theatre**
```http
POST http://localhost:8080/api/theatres
Content-Type: application/json

{
  "name": "PVR Cinemas Phoenix",
  "address": "Phoenix Marketcity, Lower Parel, Mumbai",
  "cityId": 1,
  "phone": "9876543210",
  "email": "pvr.phoenix@example.com",
  "facilities": "Parking, Food Court, Wheelchair Access, 3D Screen"
}
```

#### **Step 5: Create Screen**
```http
POST http://localhost:8080/api/screens
Content-Type: application/json

{
  "name": "Screen 1 - Audi 1",
  "theatreId": 1,
  "totalSeats": 100
}
```

#### **Step 6: Create Bulk Seats**
```http
POST http://localhost:8080/api/seats/bulk
Content-Type: application/json

{
  "screenId": 1,
  "rowNumber": "A",
  "startSeatNumber": 1,
  "endSeatNumber": 10,
  "seatType": "NORMAL",
  "basePrice": 200.0
}
```

Repeat for rows B, C, D with different seat types:
- Row A: NORMAL (₹200)
- Row B: NORMAL (₹200)
- Row C: PREMIUM (₹250)
- Row D: VIP (₹350)

#### **Step 7: Create Show**
```http
POST http://localhost:8080/api/shows
Content-Type: application/json

{
  "movieId": 1,
  "screenId": 1,
  "showDate": "2024-12-25",
  "showTime": "18:00:00",
  "basePrice": 250.0,
  "status": "ACTIVE"
}
```

#### **Step 8: Initialize Show Seats**
```http
POST http://localhost:8080/api/show-seats/initialize/1
```

#### **Step 9: View Seat Layout**
```http
GET http://localhost:8080/api/show-seats/layout/1
```

#### **Step 10: Lock Seats (Temporary Reservation)**
```http
POST http://localhost:8080/api/show-seats/lock
Content-Type: application/json

{
  "showId": 1,
  "seatIds": [1, 2, 3],
  "userId": 1
}
```

#### **Step 11: Create Booking**
```http
POST http://localhost:8080/api/bookings
Content-Type: application/json

{
  "userId": 1,
  "showId": 1,
  "seatIds": [1, 2, 3]
}
```

**Response:**
```json
{
  "success": true,
  "message": "Booking created successfully",
  "data": {
    "id": 1,
    "bookingNumber": "BK12345678",
    "userId": 1,
    "userName": "John Doe",
    "showId": 1,
    "movieTitle": "Inception",
    "theatreName": "PVR Cinemas Phoenix",
    "totalSeats": 3,
    "totalAmount": 750.0,
    "status": "PENDING",
    "createdAt": "2024-12-25T15:30:00"
  }
}
```

#### **Step 12: Process Payment**
```http
POST http://localhost:8080/api/payments/process
Content-Type: application/json

{
  "bookingId": 1,
  "amount": 750.0,
  "paymentMethod": "CARD"
}
```

---

## 📦 Module Details

### Module Structure

```
movie-ticket-booking/
│
├── src/main/java/com/example/movieticketbooking/
│   │
│   ├── controller/          # REST Controllers (11 controllers)
│   ├── service/             # Service Interfaces (12 services)
│   ├── service/impl/        # Service Implementations
│   ├── repository/          # JPA Repositories (12 repositories)
│   ├── entity/              # JPA Entities (12 entities)
│   ├── dto/
│   │   ├── request/         # Request DTOs
│   │   └── response/        # Response DTOs
│   ├── enums/               # Enumerations (8 enums)
│   ├── exception/           # Custom Exceptions (8 exceptions)
│   ├── scheduler/           # Scheduled Jobs (3 jobs)
│   ├── config/              # Configuration Classes (4 configs)
│   ├── util/                # Utility Classes (4 utilities)
│   └── constants/           # Constants (3 constant classes)
│
└── src/main/resources/
    ├── application.yml      # Application configuration
    └── static/
        └── qr-codes/        # Generated QR codes storage
```

### Key Modules

#### 1. **User Module**
- User registration and management
- Role-based access (CUSTOMER, ADMIN, THEATRE_OWNER)
- Active/inactive status management

#### 2. **Movie Module**
- Movie catalog management
- Search and filter functionality
- Status tracking (NOW_SHOWING, COMING_SOON, ARCHIVED)

#### 3. **ShowSeat Module** (Critical)
- Show-specific seat availability
- Seat locking mechanism
- Prevents double booking
- Auto-release expired locks

#### 4. **Booking Module** (Critical)
- Complete booking workflow
- Booking-seat mapping
- Confirm/cancel functionality

#### 5. **Payment Module**
- Payment processing simulation
- Multiple payment methods
- Transaction tracking

---

## 📏 Business Rules

### 1. **Seat Locking Rules**
- Seats are **locked for 10 minutes** when user starts booking
- User must **complete payment within 10 minutes**
- After 10 minutes, seats are **automatically released**
- **Scheduler runs every 5 minutes** to clean up expired locks

### 2. **Dynamic Pricing Rules**

```java
Base Price Calculation:
─────────────────────
Base Price = Show Base Price + Seat Type Surcharge + Time Surcharge

Seat Type Surcharge:
- NORMAL: ₹0
- PREMIUM: ₹50
- VIP: ₹100

Time Surcharge:
- Weekend (Sat/Sun): +₹50
- Evening Show (after 5 PM): +₹30

Example:
Show Base Price: ₹200
VIP Seat: ₹200 + ₹100 = ₹300
Weekend Evening VIP: ₹200 + ₹100 + ₹50 + ₹30 = ₹380
```

### 3. **Booking Workflow**

```
User Flow:
──────────
1. Browse Movies → Select Movie
2. Select City → View Available Shows
3. Select Show → View Seat Layout
4. Select Seats → Seats LOCKED (10 min timer starts)
5. Proceed to Payment → Make Payment
6. Payment Success → Booking CONFIRMED
7. Seats marked as BOOKED

Failure Scenarios:
─────────────────
- Payment timeout → Seats UNLOCKED automatically
- Payment failure → Seats UNLOCKED, booking CANCELLED
- Show cancellation → All bookings CANCELLED, refund initiated
```


### 4. **Show Status Rules**
```
Status Transitions:
───────────────────
ACTIVE → Show is bookable
CANCELLED → Refunds processed automatically
COMPLETED → Auto-updated 3 hours after show time
```

---

## ⏰ Scheduled Jobs

### 1. **Seat Lock Cleanup Job**
```java
Schedule: Every 5 minutes
Cron: "0 */5 * * * ?"
Purpose: Release expired seat locks
Logic: 
  - Find seats locked > 10 minutes ago
  - Set status to AVAILABLE
  - Clear lockedAt and lockedByUserId
```


### 2. **Show Status Update Job**
```java
Schedule: Every hour
Cron: "0 0 * * * ?"
Purpose: Mark completed shows
Logic:
  - Find shows with date/time in past
  - Update status to COMPLETED
```

---

## 🚨 Error Handling

### Standard Error Response Format

```json
{
  "success": false,
  "message": "Error description",
  "errors": ["Detailed error 1", "Detailed error 2"],
  "timestamp": "2024-12-25T10:30:00"
}
```

### HTTP Status Codes

| Code | Meaning | Example |
|------|---------|---------|
| 200 | Success | Resource retrieved successfully |
| 201 | Created | Resource created successfully |
| 400 | Bad Request | Validation error, invalid input |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Duplicate resource, seat already booked |
| 500 | Server Error | Unexpected server error |

### Custom Exceptions

```java
ResourceNotFoundException        // 404
DuplicateResourceException       // 409
SeatAlreadyBookedException       // 409
SeatNotAvailableException        // 400
InvalidBookingException          // 400
PaymentFailedException           // 400
ShowFullException               // 400
InvalidShowTimeException        // 400
```

---

## 📊 Performance Optimization

### Database Indexes
```sql
-- Recommended indexes for better performance
CREATE INDEX idx_show_date ON shows(show_date);
CREATE INDEX idx_show_status ON shows(status);
CREATE INDEX idx_showseat_status ON show_seats(status);
CREATE INDEX idx_booking_user ON bookings(user_id);
CREATE INDEX idx_booking_show ON bookings(show_id);
CREATE INDEX idx_payment_booking ON payments(booking_id);
```

### Caching Strategy
- Consider adding Redis for:
  - Movie catalog (rarely changes)
  - Theatre/screen information
  - Seat layouts
  - Show listings

---

## 🐛 Troubleshooting

### Common Issues

#### 1. **Database Connection Failed**
```
Error: Communications link failure

Solution:
- Check MySQL is running
- Verify database credentials in application.yml
- Ensure database 'movie_booking_db' exists
```

#### 2. **Port Already in Use**
```
Error: Port 8080 is already in use

Solution:
- Change port in application.yml
  server:
    port: 8081
```

#### 3. **Seat Lock Not Released**
```
Issue: Seats remain locked after 10 minutes

Solution:
- Check scheduler is enabled (@EnableScheduling)
- Verify scheduler cron expression
- Check application logs for errors
```

---

## 📈 Future Enhancements

### Planned Features

- [ ] JWT Authentication
- [ ] Role-Based Access Control (RBAC)
- [ ] Email Notifications (Booking confirmation)
- [ ] SMS Notifications (OTP, Alerts)
- [ ] Payment Gateway Integration (Razorpay, Stripe)
- [ ] Movie Recommendations (ML-based)
- [ ] Discount Coupons & Offers
- [ ] Loyalty Program
- [ ] Review & Rating System
- [ ] Social Media Integration
- [ ] Push Notifications
- [ ] Admin Dashboard
- [ ] Analytics & Reports
- [ ] Multi-language Support
- [ ] Dark Mode UI

---
    
## 👨‍💻 Author

**Madhan B**  
Backend Software Developer  
🔧 Java | Spring Boot | REST APIs | MySQL  

[![GitHub](https://img.shields.io/badge/GitHub-Profile-black?style=flat&logo=github)](https://github.com/MadhanBandi25)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-blue?style=flat&logo=linkedin)](https://www.linkedin.com/in/madhanbandi25)
[![Email](https://img.shields.io/badge/Email-Contact-red?style=flat&logo=gmail)](mailto:madhannn25@gmail.com)

---
