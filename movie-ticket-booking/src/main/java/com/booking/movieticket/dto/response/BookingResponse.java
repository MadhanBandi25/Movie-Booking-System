package com.booking.movieticket.dto.response;

import com.booking.movieticket.entity.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {

     private Long bookingId;
     private String bookingNumber;
     private BookingStatus status;
     private Integer totalSeats;
     private BigDecimal totalAmount;
     private List<String> seats;
     private LocalDateTime createdAt;
}
