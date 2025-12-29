package com.booking.movieticket.controller;

import com.booking.movieticket.dto.request.BookingRequest;
import com.booking.movieticket.dto.response.BookingResponse;
import com.booking.movieticket.repository.BookingRepository;
import com.booking.movieticket.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api-bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {

        return new ResponseEntity<>(bookingService.createBooking(request), HttpStatus.CREATED);
    }


    @PostMapping("/{bookingNumber}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable String bookingNumber) {

        BookingResponse response = bookingService.cancelBooking(bookingNumber);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{bookingNumber}/failed")
    public ResponseEntity<BookingResponse> failedBooking(
            @PathVariable String bookingNumber) {

        BookingResponse response = bookingService.failedBooking(bookingNumber);
        return ResponseEntity.ok(response);
    }

        @PostMapping("/{bookingNumber}/confirm")
    public ResponseEntity<BookingResponse> confirmBooking(@PathVariable String bookingNumber) {

        BookingResponse response = bookingService.confirmBooking(bookingNumber);
        return ResponseEntity.ok(response);
    }


}
