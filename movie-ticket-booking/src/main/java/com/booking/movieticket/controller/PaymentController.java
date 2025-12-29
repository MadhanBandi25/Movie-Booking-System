package com.booking.movieticket.controller;


import com.booking.movieticket.dto.request.PaymentRequest;
import com.booking.movieticket.dto.response.PaymentResponse;
import com.booking.movieticket.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api-payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest request){
        PaymentResponse response= paymentService.processPayment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{bookingNumber}/cancel")
    public ResponseEntity<String> cancelBooking(@PathVariable String bookingNumber) {

        paymentService.cancelBooking(bookingNumber);
        return ResponseEntity.ok("Booking cancelled successfully");
    }
}
