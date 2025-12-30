package com.booking.movieticket.dto.response;

import com.booking.movieticket.entity.enums.BookingStatus;
import com.booking.movieticket.entity.enums.PaymentMethod;
import com.booking.movieticket.entity.enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class PaymentResponse {

    private String bookingNumber;
    private String transactionId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;

    private String theatreName;
    private String theatreAddress;
    private String screenName;
    private List<String> seats;
    private LocalDate showDate;
    private LocalTime showTime;

    private LocalDateTime createdAt;
}
