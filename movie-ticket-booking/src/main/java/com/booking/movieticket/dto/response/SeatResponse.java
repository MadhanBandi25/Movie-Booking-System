package com.booking.movieticket.dto.response;

import com.booking.movieticket.entity.enums.SeatType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatResponse {

    private Long id;
    private Long screenId;
    private String screenName;
    private String seatRow;
    private Integer seatNumber;
    private SeatType seatType;
    private BigDecimal basePrice;
    private Boolean active;
    private LocalDateTime createdAt;
}
