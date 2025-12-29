package com.booking.movieticket.repository;

import com.booking.movieticket.entity.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingSeatRepository extends JpaRepository<BookingSeat,Long> {

    List<BookingSeat> findByBooking_Id(Long bookingId);
}
