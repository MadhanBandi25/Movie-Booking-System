package com.booking.movieticket.repository;

import com.booking.movieticket.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,Long> {

    Optional<Payment> findByTransactionId(String transactionId);

    Optional<Payment> findByBooking_BookingNumber(String bookingNumber);

    boolean existsByBooking_Id(Long bookingId);


}
