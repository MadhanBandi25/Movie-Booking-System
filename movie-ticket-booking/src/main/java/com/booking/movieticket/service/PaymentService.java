package com.booking.movieticket.service;

import com.booking.movieticket.dto.request.PaymentRequest;
import com.booking.movieticket.dto.response.PaymentResponse;

public interface PaymentService {

    PaymentResponse processPayment(PaymentRequest request);
    void cancelBooking(String bookingNumber);


}
