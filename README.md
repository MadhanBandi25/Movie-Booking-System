 # 🎬 Movie Ticket Booking System · Backend

> A **Spring Boot REST API** for online movie ticket booking — manage cities, theatres, screens, seats, movies, shows, seat locking, bookings, payments, ratings, and email notifications.

---

## 📋 Table of Contents

* [Tech Stack](#-tech-stack)
* [Features](#-features)
* [System Design](#-system-design)
* [Project Structure](#-project-structure)
* [Entity Relationship Overview](#-entity-relationship-overview)
* [API Endpoints](#-api-endpoints)
* [Setup & Installation](#-setup--installation)
* [Environment Configuration](#-environment-configuration)
* [Running the Application](#-running-the-application)
* [Error Handling](#-error-handling)
* [Security Note](#-security-note)

---

## 🛠 Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Database | MySQL 8.x |
| ORM | Spring Data JPA / Hibernate |
| Mapping | ModelMapper |
| Email | Spring Mail (Gmail SMTP) |
| Async | Spring `@Async` (AsyncConfig) |
| Scheduling | Spring `@Scheduled` (seat lock & show status schedulers) |
| Validation | Jakarta Bean Validation |
| Build Tool | Maven |
| Dev Tools | Lombok, Spring DevTools |

> ⚠️ No Spring Security / JWT module is present — all endpoints are currently **unauthenticated**. See [Security Note](#-security-note).

---

## ✨ Features

* **Users** – Create/manage users with roles (`CUSTOMER`, `ADMIN`, `THEATRE_OWNER`), activate/deactivate, filter by role/status
* **Cities** – Manage cities (state/country), activate/deactivate, search by name/state/country
* **Theatres** – Manage theatres per city, search, activate/deactivate, paginated active-theatre listing by city
* **Screens** – Manage screens per theatre, search by city/theatre, activate/deactivate
* **Seats** – Add seats individually or in bulk, bulk-update, filter by screen/type/row/status, activate/deactivate
* **Movies** – Manage movie catalog with statuses (`COMING_SOON`, `NOW_SHOWING`, `ARCHIVED`), search, filter by language
* **Movie Ratings** – Add and view user ratings/reviews per movie (with duplicate-rating prevention)
* **Shows** – Schedule shows (movie + screen + date/time + base price), filter by upcoming/now-showing/date, cancel/complete
* **Show Seats** – Initialize seat layout for a show, view seat layout & availability, lock/unlock seats, book seats
* **Bookings** – Create booking, confirm/cancel/fail with seat-status transitions
* **Payments** – Make payment for booking, cancel payment (with refund status)
* **Notifications / Email** – Automated email notifications (e.g., booking confirmation) via Gmail SMTP
* **Schedulers** – Auto-expire seat locks (`SeatLockScheduler`) and auto-update show status (`ShowStatusScheduler`)

---

## 🏗 System Design

```
┌─────────────────────────────────────────────────────────────────┐
│                CLIENT (Browser / Postman)                       │
└───────────────────────────┬─────────────────────────────────────┘
                            │  HTTP/REST (JSON)
                            ▼
┌───────────────────────────────────────────────────────────────────┐
│                   SPRING BOOT APPLICATION                         │
│                   localhost:9988                                  │
│                                                                   │
│  ┌────────────────────┐        ┌────────────────────┐             │
│  │   Controllers      │ ─────▶ │      Services      │             │
│  │ /api-users         │        │   Business Logic   │             │
│  │ /api-cities        │        │   (Impl package)   │             │
│  │ /api-theatres      │        └────────┬───────────┘             │
│  │ /api-screens       │                 │                         │
│  │ /api-seats         │        ┌────────▼───────────┐             │
│  │ /api-movies        │        │    Repositories    │             │
│  │ /api-shows         │        │   (Spring Data)    │             │
│  │ /api/show-seats    │        └────────┬───────────┘             │
│  │ /api-bookings      │                 │                         │
│  │ /api-payments      │                 │                         │
│  └────────────────────┘                 │                         │
│                                         │                         │
│  ┌─────────────────────┐                │                         │
│  │ Schedulers          │ ───────────────┘                         │
│  │ SeatLockScheduler   │  (expire locked seats)                   │
│  │ ShowStatusScheduler │  (auto update show status)               │
│  └─────────────────────┘                                          │
│                                                                   │
└──────────────────────────────────────────────┬────────────────────┘
                                               │
                            ┌──────────────────▼──────────────────────┐
                            │           MySQL Database                │
                            │                                         │
                            │ ┌──────┐ ┌──────────┐ ┌──────┐ ┌─────-┐ │
                            │ │users │ │ theatres │ │movies│ │cities│ │
                            │ └──────┘ └──────────┘ └──────┘ └──────┘ │
                            │ ┌────────┐ ┌──────┐ ┌───────┐ ┌──────┐  │
                            │ │screens │ │ seats│ │ shows │ │ratngs│  │
                            │ └────────┘ └──────┘ └───────┘ └──────┘  │
                            │ ┌────────────┐ ┌──────────┐ ┌────────┐  │
                            │ │ show_seats │ │ bookings │ │payments│  │
                            │ └────────────┘ └──────────┘ └────────┘  │
                            └─────────────────────────────────────────┘
                                               │
                            ┌──────────────────▼──────────────────────┐
                            │         Gmail SMTP Server               │
                            │      (Booking confirmation emails)      │
                            └─────────────────────────────────────────┘
```

### Seat Lock → Booking → Payment Flow

```
INITIALIZE SHOW SEATS         LOCK SEATS              CREATE BOOKING
─────────────────────       ──────────────          ─────────────────
POST /api/show-seats/    →   POST /api/show-seats/   →   POST /api-bookings
   initialize/{showId}           lock                         │
      │                          │                            ▼
      ▼                          ▼                     Booking status: PENDING
ShowSeat rows created      Seat status: LOCKED          Linked seats: BookingSeat
for each seat (AVAILABLE)   (expires via                       │
                             SeatLockScheduler)                ▼
                                                          PAY OR CANCEL
                                                  ┌──────────────┴───────────────┐
                                                  ▼                              ▼
                                       POST /api-payments              POST /api-bookings/
                                       Seat status: BOOKED                {bookingNumber}/cancel
                                       Booking status: CONFIRMED        Seat status: AVAILABLE
                                       (via /confirm)                   Booking status: CANCELLED
```

### Seat Lock Expiry & Show Status Scheduler

```
SeatLockScheduler (runs periodically)
  → Finds seats LOCKED beyond app.seat-lock-duration (minutes)
  → Reverts ShowSeat status: LOCKED → AVAILABLE
  → Marks related pending Booking as FAILED (if unpaid)

ShowStatusScheduler (runs periodically)
  → ACTIVE shows past showDate/showTime → RUNNING
  → RUNNING shows past duration → COMPLETED
```

---

## 📁 Project Structure

```
movie-ticket-booking/
│
├── src/main/java/com/booking/movieticket/
│   │
│   ├── config/
│   │   ├── AsyncConfig.java             # @Async executor config (for emails)
│   │   ├── CorsConfig.java              # CORS configuration
│   │   └── ModelMapperConfig.java       # ModelMapper bean
│   │
│   ├── controller/                      # REST API Controllers
│   │   ├── UserController.java          # /api-users/**
│   │   ├── CityController.java          # /api-cities/**
│   │   ├── TheatreController.java       # /api-theatres/**
│   │   ├── ScreenController.java        # /api-screens/**
│   │   ├── SeatController.java          # /api-seats/**
│   │   ├── MovieController.java         # /api-movies/**
│   │   ├── MovieRatingController.java   # /api-movies/{movieId}/ratings
│   │   ├── ShowController.java          # /api-shows/**
│   │   ├── ShowSeatController.java      # /api/show-seats/**
│   │   ├── BookingController.java       # /api-bookings/**
│   │   └── PaymentController.java       # /api-payments/**
│   │
│   ├── service/ & service/Impl/         # Business logic interfaces & implementations
│   │   ├── UserService(Impl)
│   │   ├── CityService(Impl)
│   │   ├── TheatreService(Impl)
│   │   ├── ScreenService(Impl)
│   │   ├── SeatService(Impl)
│   │   ├── MovieService(Impl)
│   │   ├── MovieRatingService(Impl)
│   │   ├── ShowService(Impl)
│   │   ├── ShowSeatService(Impl)
│   │   ├── BookingService(Impl)
│   │   ├── PaymentService(Impl)
│   │   ├── NotificationService(Impl)
│   │   └── EmailService (Impl)
│   │
│   ├── entity/                          # JPA Entities
│   │   ├── User.java
│   │   ├── City.java
│   │   ├── Theatre.java
│   │   ├── Screen.java
│   │   ├── Seat.java
│   │   ├── Movie.java
│   │   ├── MovieRating.java
│   │   ├── Show.java
│   │   ├── ShowSeat.java
│   │   ├── Booking.java
│   │   ├── BookingSeat.java
│   │   └── Payment.java
│   │
│   ├── entity/enums/                    # Enumerations
│   │   ├── UserRole.java                # CUSTOMER, ADMIN, THEATRE_OWNER
│   │   ├── MovieStatus.java             # COMING_SOON, NOW_SHOWING, ARCHIVED
│   │   ├── ShowStatus.java              # ACTIVE, RUNNING, CANCELLED, COMPLETED
│   │   ├── SeatType.java                # NORMAL, PREMIUM, VIP
│   │   ├── ShowSeatStatus.java          # AVAILABLE, LOCKED, BOOKED
│   │   ├── BookingStatus.java           # PENDING, CONFIRMED, CANCELLED, FAILED
│   │   ├── PaymentMethod.java           # CARD, UPI, WALLET, NET_BANKING
│   │   ├── PaymentStatus.java           # PENDING, SUCCESS, FAILED
│   │   └── RefundStatus.java            # NOT_APPLICABLE, REFUNDED
│   │
│   ├── repository/                      # Spring Data JPA Repositories
│   │   ├── UserRepository.java
│   │   ├── CityRepository.java
│   │   ├── TheatreRepository.java
│   │   ├── ScreenRepository.java
│   │   ├── SeatRepository.java
│   │   ├── MovieRepository.java
│   │   ├── MovieRatingRepository.java
│   │   ├── ShowRepository.java
│   │   ├── ShowSeatRepository.java
│   │   ├── BookingRepository.java
│   │   ├── BookingSeatRepository.java
│   │   └── PaymentRepository.java
│   │
│   ├── dto/
│   │   ├── request/                     # Incoming request bodies
│   │   │   ├── UserRequest.java
│   │   │   ├── CityRequest.java
│   │   │   ├── TheatreRequest.java
│   │   │   ├── ScreenRequest.java
│   │   │   ├── SeatRequest.java
│   │   │   ├── SeatBulkRequest.java
│   │   │   ├── SeatBulkUpdateRequest.java
│   │   │   ├── MovieRequest.java
│   │   │   ├── MovieRatingRequest.java
│   │   │   ├── ShowRequest.java
│   │   │   ├── SeatLockRequest.java
│   │   │   ├── BookingRequest.java
│   │   │   └── PaymentRequest.java
│   │   │
│   │   └── response/                    # Outgoing response bodies
│   │       ├── ApiResponse.java         # Generic wrapper response
│   │       ├── UserResponse.java
│   │       ├── CityResponse.java
│   │       ├── TheatreResponse.java
│   │       ├── ScreenResponse.java
│   │       ├── SeatResponse.java
│   │       ├── MovieResponse.java
│   │       ├── MovieRatingResponse.java
│   │       ├── ShowResponse.java
│   │       ├── ShowSeatResponse.java
│   │       ├── BookingResponse.java
│   │       └── PaymentResponse.java
│   │
│   ├── exception/                       # Custom Exceptions
│   │   ├── GlobalExceptionHandler.java  # @RestControllerAdvice
│   │   ├── ErrorResponse.java
│   │   ├── BusinessException.java
│   │   ├── ResourceNotFoundException.java
│   │   ├── DuplicateResourceException.java
│   │   ├── DuplicateRatingException.java
│   │   ├── InvalidRequestException.java
│   │   ├── InvalidBookingException.java
│   │   ├── SeatAlreadyBookedException.java
│   │   ├── SeatNotAvailableException.java
│   │   ├── ShowSeatAlreadyInitializedException.java
│   │   ├── PaymentFailedException.java
│   │   └── UnauthorizedActionException.java
│   │
│   ├── scheduler/
│   │   ├── SeatLockScheduler.java       # Expires stale seat locks
│   │   └── ShowStatusScheduler.java     # Auto-updates show status
│   │
│   └── MovieTicketBookingApplication.java  # Main entry point
│
├── src/main/resources/
│   └── application.properties           # DB, mail, server port, seat-lock config
│
├── src/test/java/
│   └── (test classes here)
│
└── pom.xml                              # Maven dependencies
```

---

## 🗃 Entity Relationship Overview

```
┌──────────┐        ┌────────────┐        ┌───────────────┐
│   CITY   │ 1   N  │  THEATRE   │ 1   N  │   SCREEN      │
├──────────┤────────├────────────┤────────├───────────────┤
│ name     │        │ name       │        │ name          │
│ state    │        │ city_id FK │        │ theatre_id FK │
│ country  │        │ active     │        │ active        │
│ active   │        └────────────┘        └─────────┬─────┘
└──────────┘                                        │ 1
                                                    │ N
┌────────────────┐                           ┌──────▼─────────┐
│    MOVIE       │ 1        N                │     SEAT       │
├────────────────┤────┐                      ├────────────────┤
│ title          │    │                      │ screen_id FK   │
│ language       │    │                      │ seatRow/number │
│ status ENUM    │    │                      │ seatType ENUM  │◄── NORMAL|PREMIUM|VIP
│ (COMING_SOON/  │    │                      │ active         │
│ NOW_SHOWING/   │    │                      └────────┬───────┘
│ ARCHIVED)      │    │                               │
└──────┬─────────┘    │                               │
       │ 1            │ N (ratings)                   │
       │ N            ▼                               │
┌──────▼──────────┐  ┌───────────────┐                │
│     SHOW        │  │ MOVIE_RATING  │                │
├─────────────────┤  ├───────────────┤                │
│ movie_id FK     │  │ movie_id FK   │                │
│ screen_id FK    │  │ user_id FK    │                │
│ showDate/Time   │  │ rating, review│                │
│ basePrice       │  └───────────────┘                │
│ status ENUM     │◄── ACTIVE|RUNNING|CANCELLED|COMPLETED
└──────┬──────────┘                                   │
       │ 1                                            │
       │ N                                            │
┌──────▼───────────┐   1:1 per seat   ┌───────────────▼──┐
│   SHOW_SEAT      │◄─────────────────│     SEAT (above) │
├──────────────────┤                  └──────────────────┘
│ show_id FK       │
│ seat_id FK       │
│ status ENUM      │◄── AVAILABLE|LOCKED|BOOKED
│ price            │
└──────┬───────────┘
       │ N
       │ 1
┌──────▼──────────┐        ┌───────────────┐
│  BOOKING_SEAT   │ N   1  │   BOOKING     │
├─────────────────┤────────├───────────────┤
│ show_seat_id FK │        │ bookingNumber │
│ booking_id FK   │        │ user_id FK    │
└─────────────────┘        │ show_id FK    │
                           │ status ENUM   │◄── PENDING|CONFIRMED|CANCELLED|FAILED
                           └──────┬────────┘
                                  │ 1
                                  │ 1
                              ┌───▼────────────┐
                              │    PAYMENT     │
                              ├────────────────┤
                              │ booking_id FK  │
                              │ paymentMethod  │◄── CARD|UPI|WALLET|NET_BANKING
                              │ status ENUM    │◄── PENDING|SUCCESS|FAILED
                              │ refundStatus   │◄── NOT_APPLICABLE|REFUNDED
                              └────────────────┘

┌───────────┐
│   USER    │
├───────────┤
│ name      │
│ email     │
│ phone     │
│ role ENUM │◄── CUSTOMER|ADMIN|THEATRE_OWNER
│ active    │
└───────────┘
```

---

## 🔌 API Endpoints

> Base URL: `http://localhost:9988`

### 👤 Users (`/api-users`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api-users` | Create a new user |
| GET | `/api-users/{id}` | Get user by ID |
| GET | `/api-users` | Get all users |
| GET | `/api-users/role/{role}` | Get users by role |
| GET | `/api-users/active` | Get active users |
| GET | `/api-users/inactive` | Get inactive users |
| PUT | `/api-users/{id}` | Update user |
| PUT | `/api-users/{id}/activate` | Activate user |
| PUT | `/api-users/{id}/deactivate` | Deactivate user |
| DELETE | `/api-users/{id}` | Delete user |

**Example – Create User**
```http
POST /api-users
Content-Type: application/json

{
  "name": "Madhan Kumar",
  "email": "madhan@example.com",
  "phone": "9876543210",
  "role": "CUSTOMER"
}
```

---

### 🌆 Cities (`/api-cities`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api-cities` | Add a city |
| GET | `/api-cities/{id}` | Get city by ID |
| GET | `/api-cities` | Get all cities |
| GET | `/api-cities/state/{state}` | Get cities by state |
| GET | `/api-cities/country/{country}` | Get cities by country |
| GET | `/api-cities/city/{cityName}` | Get city by name |
| GET | `/api-cities/active` | Get active cities |
| GET | `/api-cities/inactive` | Get inactive cities |
| PUT | `/api-cities/{id}` | Update city |
| PATCH | `/api-cities/{id}/activate` | Activate city |
| PATCH | `/api-cities/{id}/deactivate` | Deactivate city |

**Example – Add City**
```http
POST /api-cities
Content-Type: application/json

{
  "name": "Bengaluru",
  "state": "Karnataka",
  "country": "India"
}
```

---

### 🏛 Theatres (`/api-theatres`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api-theatres` | Add a theatre |
| GET | `/api-theatres/{id}` | Get theatre by ID |
| GET | `/api-theatres` | Get all theatres |
| GET | `/api-theatres/search?name=` | Search theatres by name |
| GET | `/api-theatres/city?cityId=` | Get theatres by city |
| GET | `/api-theatres/active` | Get active theatres |
| GET | `/api-theatres/inactive` | Get inactive theatres |
| GET | `/api-theatres/city/{cityId}/active/paged` | Paginated active theatres in a city |
| PUT | `/api-theatres/{id}` | Update theatre |
| PATCH | `/api-theatres/{id}/activate` | Activate theatre |
| PATCH | `/api-theatres/{id}/deactivate` | Deactivate theatre |
| DELETE | `/api-theatres/{id}` | Delete theatre |

**Example – Add Theatre**
```http
POST /api-theatres
Content-Type: application/json

{
  "name": "PVR Cinemas",
  "cityId": 1,
  "address": "Forum Mall, Koramangala"
}
```

---

### 🎞 Screens (`/api-screens`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api-screens` | Add a screen |
| GET | `/api-screens/{id}` | Get screen by ID |
| GET | `/api-screens` | Get all screens |
| GET | `/api-screens/search?name=` | Search screens by name |
| GET | `/api-screens/search-by-city?city=` | Search screens by city |
| GET | `/api-screens/theatre/{id}` | Get screens by theatre |
| GET | `/api-screens/theatre/{id}/active` | Get active screens by theatre |
| GET | `/api-screens/active` | Get active screens |
| GET | `/api-screens/inactive` | Get inactive screens |
| PUT | `/api-screens/{id}` | Update screen |
| PATCH | `/api-screens/{id}/activate` | Activate screen |
| PATCH | `/api-screens/{id}/deactivate` | Deactivate screen |
| DELETE | `/api-screens/{id}` | Delete screen |

**Example – Add Screen**
```http
POST /api-screens
Content-Type: application/json

{
  "name": "Screen 1",
  "theatreId": 1,
  "totalSeats": 100
}
```

---

### 💺 Seats (`/api-seats`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api-seats` | Add a single seat |
| POST | `/api-seats/bulk` | Add seats in bulk |
| GET | `/api-seats/{id}` | Get seat by ID |
| GET | `/api-seats` | Get all seats |
| GET | `/api-seats/screen/{screenId}` | Get seats by screen |
| GET | `/api-seats/screen/{screenId}/active` | Get active seats by screen |
| GET | `/api-seats/type/{seatType}` | Get seats by type |
| GET | `/api-seats/screen/{screenId}/row/{seatRow}` | Get seats by screen & row |
| GET | `/api-seats/active` | Get active seats |
| GET | `/api-seats/inactive` | Get inactive seats |
| PUT | `/api-seats/{id}` | Update seat |
| PUT | `/api-seats/bulk-seats` | Bulk update seats |
| PATCH | `/api-seats/{id}/activate` | Activate seat |
| PATCH | `/api-seats/{id}/deactivate` | Deactivate seat |
| DELETE | `/api-seats/{id}` | Delete seat |

**Example – Add Seats in Bulk**
```http
POST /api-seats/bulk
Content-Type: application/json

{
  "screenId": 1,
  "seatRow": "A",
  "seatNumbers": [1, 2, 3, 4, 5],
  "seatType": "PREMIUM"
}
```

---

### 🎥 Movies (`/api-movies`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api-movies` | Add a movie |
| GET | `/api-movies/{id}` | Get movie by ID |
| GET | `/api-movies/now-showing` | Get now-showing movies |
| GET | `/api-movies/coming-soon` | Get coming-soon movies |
| GET | `/api-movies/archived` | Get archived movies |
| GET | `/api-movies` | Get all movies |
| GET | `/api-movies/search?title=` | Search movies by title |
| GET | `/api-movies/language/{language}` | Get movies by language |
| PUT | `/api-movies/{id}` | Update movie |
| PUT | `/api-movies/{id}/archive` | Archive a movie |

**Example – Add Movie**
```http
POST /api-movies
Content-Type: application/json

{
  "title": "Interstellar",
  "language": "English",
  "genre": "Sci-Fi",
  "durationMinutes": 169,
  "status": "NOW_SHOWING"
}
```

---

### ⭐ Movie Ratings (`/api-movies/{movieId}/ratings`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api-movies/{movieId}/ratings` | Add a rating/review for a movie |
| GET | `/api-movies/{movieId}/ratings` | Get ratings for a movie |

**Example – Add Rating**
```http
POST /api-movies/5/ratings
Content-Type: application/json

{
  "userId": 1,
  "rating": 5,
  "review": "Amazing visuals and story!"
}
```

---

### 🎬 Shows (`/api-shows`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api-shows` | Schedule a show |
| GET | `/api-shows/{id}` | Get show by ID |
| GET | `/api-shows/now-upcoming` | Get upcoming shows |
| GET | `/api-shows/now-showing` | Get currently running shows |
| GET | `/api-shows` | Get all shows |
| GET | `/api-shows/movie-name/{movieName}/date/{showDate}` | Get shows by movie name & date |
| GET | `/api-shows/movie/{movieId}/date/{showDate}` | Get shows by movie ID & date |
| PATCH | `/api-shows/{id}/cancel` | Cancel a show |
| PATCH | `/api-shows/{id}/complete` | Mark a show as completed |

**Example – Schedule Show**
```http
POST /api-shows
Content-Type: application/json

{
  "movieId": 5,
  "screenId": 1,
  "showDate": "2026-06-15",
  "showTime": "18:30:00",
  "basePrice": 250
}
```

---

### 🪑 Show Seats (`/api/show-seats`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/show-seats/initialize/{showId}` | Initialize seat layout for a show |
| GET | `/api/show-seats/layout/{showId}` | Get full seat layout for a show |
| GET | `/api/show-seats/available/{showId}` | Get available seats for a show |
| POST | `/api/show-seats/lock` | Lock selected seats (temporary hold) |
| POST | `/api/show-seats/unlock` | Unlock previously locked seats |
| POST | `/api/show-seats/book` | Mark locked seats as booked |

**Example – Initialize Show Seats**
```http
POST /api/show-seats/initialize/10
```

**Example – Lock Seats**
```http
POST /api/show-seats/lock
Content-Type: application/json

{
  "showId": 10,
  "seatIds": [101, 102, 103],
  "userId": 1
}
```
> Locked seats auto-expire after `app.seat-lock-duration` minutes (configurable), reverted by `SeatLockScheduler`.

---

### 📅 Bookings (`/api-bookings`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api-bookings` | Create a booking (status: PENDING) |
| POST | `/api-bookings/{bookingNumber}/cancel` | Cancel a booking |
| POST | `/api-bookings/{bookingNumber}/failed` | Mark a booking as failed |
| POST | `/api-bookings/{bookingNumber}/confirm` | Confirm a booking (after payment) |

**Example – Create Booking**
```http
POST /api-bookings
Content-Type: application/json

{
  "showId": 10,
  "userId": 1,
  "seatIds": [101, 102, 103]
}
```
Response:
```json
{
  "bookingNumber": "BKG-20260613-0001",
  "status": "PENDING",
  "totalAmount": 750.0,
  "seats": ["A1", "A2", "A3"]
}
```

---

### 💳 Payments (`/api-payments`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api-payments` | Make payment for a booking (confirms booking on success) |
| POST | `/api-payments/{bookingNumber}/cancel` | Cancel payment & process refund |

**Example – Make Payment**
```http
POST /api-payments
Content-Type: application/json

{
  "bookingNumber": "BKG-20260613-0001",
  "paymentMethod": "UPI"
}
```

---

## ⚙️ Setup & Installation

### Prerequisites

```
✅ Java 17+
✅ Maven 3.8+
✅ MySQL 8.0+
✅ Gmail account (for email notifications)
```

### Step 1: Clone the Repository

```bash
git clone https://github.com/your-username/Movie-Booking-System.git
cd Movie-Booking-System/movie-ticket-booking
```

### Step 2: Create MySQL Database

```sql
CREATE DATABASE movie_ticket_booking;
```

### Step 3: Configure `application.properties`

```properties
spring.application.name=movie-ticket-booking

spring.datasource.url=jdbc:mysql://localhost:3306/movie_ticket_booking
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update

server.port=9988

# Seat lock duration in minutes
app.seat-lock-duration=5

# Gmail SMTP
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-16-char-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

> **Gmail App Password**: Go to Google Account → Security → 2-Step Verification → App Passwords → Generate a 16-character password.

---

## 🌐 Environment Configuration

| Property | Description | Must Change Before Pushing |
|----------|-------------|------------------------------|
| `spring.datasource.username` / `password` | MySQL credentials | ✅ |
| `spring.mail.username` / `password` | Gmail SMTP credentials (App Password) | ✅ |
| `app.seat-lock-duration` | Minutes before a locked seat auto-releases | Optional |
| `server.port` | Application port (default `9988`) | Optional |

---

## ▶ Running the Application

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run
```

Server starts at: `http://localhost:9988`

---

## ❌ Error Handling

All errors follow this standard format (via `GlobalExceptionHandler`):

```json
{
  "timestamp": "2026-06-13T12:00:00",
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Show not found with id: 10",
  "path": "/api-shows/10"
}
```

| Exception | HTTP Status | Error Code |
|-----------|-------------|------------|
| ResourceNotFoundException | 404 | NOT_FOUND |
| DuplicateResourceException | 409 | DUPLICATE_RESOURCE |
| DuplicateRatingException | 409 | DUPLICATE_RATING |
| InvalidRequestException | 400 | INVALID_REQUEST |
| InvalidBookingException | 400 | INVALID_BOOKING |
| SeatAlreadyBookedException | 409 | SEAT_ALREADY_BOOKED |
| SeatNotAvailableException | 400 | SEAT_NOT_AVAILABLE |
| ShowSeatAlreadyInitializedException | 409 | SHOW_SEAT_ALREADY_INITIALIZED |
| PaymentFailedException | 402 | PAYMENT_FAILED |
| UnauthorizedActionException | 403 | UNAUTHORIZED |
| BusinessException | 400 | BUSINESS_ERROR |
| MethodArgumentNotValidException | 400 | VALIDATION_ERROR |
| Exception (generic) | 500 | INTERNAL_SERVER_ERROR |

---

## ⚠️ Security Note

This project currently has **no authentication/authorization layer** (no Spring Security/JWT), and `application.properties` contains a **real Gmail SMTP password**. Before deploying or pushing publicly:

1. Add Spring Security with JWT-based authentication and role-based access control using the existing `UserRole` enum (`CUSTOMER`, `ADMIN`, `THEATRE_OWNER`) — e.g., restrict movie/show/theatre management to `ADMIN`/`THEATRE_OWNER`, booking/payment actions to `CUSTOMER`.
2. Move `spring.datasource.password` and `spring.mail.password` to environment variables.
3. Rotate the Gmail App Password if it has already been committed/pushed.
4. Add `.gitignore` for `application.properties` and provide an `application.properties.example` template.

---
---
 
