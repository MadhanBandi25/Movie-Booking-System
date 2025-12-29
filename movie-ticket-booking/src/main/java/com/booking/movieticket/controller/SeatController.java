package com.booking.movieticket.controller;

import com.booking.movieticket.dto.request.SeatBulkRequest;
import com.booking.movieticket.dto.request.SeatBulkUpdateRequest;
import com.booking.movieticket.dto.request.SeatRequest;
import com.booking.movieticket.dto.response.SeatResponse;
import com.booking.movieticket.entity.enums.SeatType;
import com.booking.movieticket.service.SeatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api-seats")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @PostMapping
    public ResponseEntity<SeatResponse> createSeat(@Valid @RequestBody SeatRequest request){
        SeatResponse response=seatService.createSeat(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<SeatResponse>> createBulkSeat(@Valid @RequestBody SeatBulkRequest request) {
       List<SeatResponse> response=seatService.createBulkSeat(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeatResponse> getSeatById(@PathVariable Long id) {
        return ResponseEntity.ok(seatService.getSeatById(id));
    }

    @GetMapping
    public ResponseEntity<List<SeatResponse>> getAllSeats() {

        return ResponseEntity.ok(seatService.getAllSeats());
    }

    @GetMapping("/screen/{screenId}")
    public ResponseEntity<List<SeatResponse>> getSeatsByScreen(@PathVariable Long screenId) {
        return ResponseEntity.ok(seatService.getSeatsByScreen(screenId));
    }

    @GetMapping("/screen/{screenId}/active")
    public ResponseEntity<List<SeatResponse>> getActiveSeatsByScreen(@PathVariable Long screenId) {
        return ResponseEntity.ok(seatService.getActiveSeatsByScreen(screenId));
    }
    @GetMapping("/type/{seatType}")
    public ResponseEntity<List<SeatResponse>> getSeatsByType(@PathVariable SeatType seatType) {
        return ResponseEntity.ok(seatService.getSeatsByType(seatType));
    }

    @GetMapping("/screen/{screenId}/row/{seatRow}")
    public ResponseEntity<List<SeatResponse>> getSeatsByRow(
            @PathVariable Long screenId,
            @PathVariable String seatRow) {
        return ResponseEntity.ok(seatService.getSeatsByScreenAndRow(screenId, seatRow));
    }
    @PutMapping("/{id}")
    public ResponseEntity<SeatResponse> updateSeat(
            @PathVariable Long id,
            @Valid @RequestBody SeatRequest request) {

        return ResponseEntity.ok(seatService.updateSeat(id, request));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeat(@PathVariable Long id) {
        seatService.deleteSeat(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/active")
    public ResponseEntity<List<SeatResponse>> getActiveSeats() {
        return ResponseEntity.ok(seatService.getActiveSeats());
    }
    @GetMapping("/inactive")
    public ResponseEntity<List<SeatResponse>> getInactiveSeats() {
        return ResponseEntity.ok(seatService.getInactiveSeats());
    }
    @PatchMapping("/{id}/activate")
    public ResponseEntity<SeatResponse> activateSeat(@PathVariable Long id) {
        return ResponseEntity.ok(seatService.activateSeat(id));
    }
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<SeatResponse> deactivateSeat(@PathVariable Long id) {
        return ResponseEntity.ok(seatService.deactivateSeat(id));
    }

    @PutMapping("/bulk-seats")
    public ResponseEntity<String> bulkUpdateSeats(@Valid @RequestBody SeatBulkUpdateRequest request) {

        int updatedCount = seatService.bulkUpdateSeats(request);
        return ResponseEntity.ok(updatedCount + " seats updated successfully");
    }


}
