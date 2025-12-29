package com.booking.movieticket.controller;

import com.booking.movieticket.dto.request.SeatLockRequest;
import com.booking.movieticket.dto.response.ApiResponse;
import com.booking.movieticket.dto.response.ShowSeatResponse;
import com.booking.movieticket.service.ShowSeatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/show-seats")
public class ShowSeatController {

    @Autowired
    private ShowSeatService showSeatService;

    @Value("${app.seat-lock-duration}")
    private int lockDurationMinutes;


    // initialize show seats
    @PostMapping("/initialize/{showId}")
    public ResponseEntity<ApiResponse<Void>> initializeShowSeats(@PathVariable Long showId){
        showSeatService.initializeShowSeat(showId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Show seats initialized successfully"));
    }
   // get seat layout
    @GetMapping("/layout/{showId}")
    public ResponseEntity<ApiResponse<List<ShowSeatResponse>>> getShowSeatLayout(@PathVariable Long showId) {

        List<ShowSeatResponse> layout = showSeatService.getShowSeatLayout(showId);

        return ResponseEntity.ok(ApiResponse.success("Seat layout retrieved successfully", layout));
    }

    // lock seats
    @PostMapping("/lock")
    public ResponseEntity<ApiResponse<Void>> lockSeats(@Valid @RequestBody SeatLockRequest request) {
        showSeatService.lockSeats(
                request.getShowId(),
                request.getSeatIds(),
                request.getUserId());

        return ResponseEntity.ok(ApiResponse.success( "Seats locked successfully (valid for " + lockDurationMinutes + " minutes)"));
    }

    // book seats
    @PostMapping("/book")
    public ResponseEntity<ApiResponse<Void>> bookSeats(
            @RequestParam Long showId,
            @RequestParam Long userId,
            @RequestBody List<Long> seatIds) {

        showSeatService.bookSeats(showId, seatIds, userId);

        return ResponseEntity.ok(ApiResponse.success("Seats booked successfully"));
    }

    // available seats
    @GetMapping("/available/{showId}")
    public ResponseEntity<ApiResponse<Long>> getAvailableSeatCount(@PathVariable Long showId) {

        Long count = showSeatService.getAvailableSeatCount(showId);

        return ResponseEntity.ok(ApiResponse.success("Available seats count retrieved", count));
    }

    @PostMapping("/unlock")
    public void unlockSeats(
            @RequestParam Long showId,
            @RequestParam Long userId,
            @RequestBody List<Long> seatIds) {

        showSeatService.unlockSeats(showId, seatIds, userId);
    }


}
