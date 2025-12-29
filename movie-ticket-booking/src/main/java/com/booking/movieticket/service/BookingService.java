package com.booking.movieticket.service;

import com.booking.movieticket.dto.request.BookingRequest;
import com.booking.movieticket.dto.response.BookingResponse;

public interface BookingService {

    BookingResponse createBooking(BookingRequest request);
    BookingResponse cancelBooking(String bookingNumber);
    BookingResponse failedBooking(String bookingNumber);

    BookingResponse confirmBooking(String bookingNumber);

}
