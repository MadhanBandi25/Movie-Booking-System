🎬 Movie Ticket Booking System – Backend

A scalable Movie Ticket Booking Backend Application built using Java & Spring Boot, designed to handle real-world cinema booking workflows such as movie scheduling, seat locking, booking confirmation, payments, and notifications.

This project follows industry-level backend architecture, proper transaction management, and RESTful API design.

🚀 Features
🎥 Movie Management

Create & manage movies

Movie release date handling

Active / inactive movie control

Movies cannot be scheduled before release date

🏢 Theatre & Screen Management

Multiple theatres per city

Multiple screens per theatre

Seat layout & capacity management per screen

⏰ Show Scheduling

Schedule shows only on or after movie release date

Fetch shows by:

Movie name

Show date

Only active & valid shows are returned

💺 Seat Management (Core Feature)

Show-wise seat mapping

Seat states:

AVAILABLE

LOCKED

BOOKED

Temporary seat locking during booking

Auto unlock on payment failure or cancellation

📖 Booking Flow

Create booking (PENDING)

Confirm booking after successful payment

Cancel booking

Seat unlock on cancel / failure

Unique bookingNumber used instead of ID

💳 Payment Module

Payment initiation & confirmation

Payment status handling:

SUCCESS

FAILED

Transaction-safe booking confirmation

Seat release on payment failure

📧 Email Notifications

Booking confirmation email

Cancellation email

Sent to actual user email

Configured using Spring Mail (Gmail SMTP)

⚠️ Exception Handling

Centralized GlobalExceptionHandler

Custom exceptions:

ResourceNotFoundException

DuplicateResourceException

SeatNotAvailableException

InvalidBookingException

PaymentFailedException

Clean & user-friendly API error responses

🛠️ Tech Stack
Layer	Technology
Language	Java 17+
Framework	Spring Boot
ORM	Spring Data JPA (Hibernate)
Database	MySQL
API Style	RESTful APIs
Validation	Jakarta Validation
Mail	Spring Mail
Build Tool	Maven
📂 Project Structure
com.booking.movieticket
│
├── controller      # REST Controllers
├── service         # Business logic interfaces
├── service.impl    # Service implementations
├── repository      # JPA Repositories
├── entity          # JPA Entities
├── dto
│   ├── request
│   └── response
├── exception       # Custom exceptions & handlers
├── enums           # Status enums
└── config          # Mail & app configurations

🔁 Booking Workflow (High Level)

User selects movie & show

Seats are locked temporarily

Booking created with status PENDING

Payment initiated

On success:

Booking → CONFIRMED

Seats → BOOKED

Confirmation email sent

On failure / cancel:

Booking → CANCELLED

Seats → AVAILABLE

🔐 Transaction Safety

Uses @Transactional

Prevents:

Double booking

Seat race conditions

Partial payment updates

📬 Mail Configuration (application.properties)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=yourgmail@gmail.com
spring.mail.password=your-16-digit-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true


📌 Uses App Password, not Gmail login password

🧪 API Examples
Create Booking
POST /api-bookings

Confirm Booking
POST /api-bookings/{bookingNumber}/confirm

Cancel Booking
POST /api-bookings/{bookingNumber}/cancel

Process Payment
POST /api-payments

Get Shows by Movie & Date
GET /api-shows/movie-name/{movieName}/date/{showDate}

🎯 Key Design Decisions

bookingNumber used instead of DB ID (real-world practice)

Seat locking handled via ShowSeat Service

Release-date validation in Show module

Clean separation of concerns

No logs exposed to end user

Admin & user email handling separated

🧑‍💻 Author

Madhan B
Backend Software Developer
Java | Spring Boot | REST APIs | MySQL
