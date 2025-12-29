package com.booking.movieticket.service;

import com.booking.movieticket.entity.Booking;

import java.util.List;

public interface NotificationService {

    void sendBookingConfirmation(Booking booking, List<String> seats);

    void sendBookingCancellation(Booking booking, List<String> seats);

    void sendPaymentFailure(Booking booking);
}
