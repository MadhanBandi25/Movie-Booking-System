package com.booking.movieticket.dto.request;

import com.booking.movieticket.entity.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotNull(message = "Booking number is required")
    private String bookingNumber;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

}
