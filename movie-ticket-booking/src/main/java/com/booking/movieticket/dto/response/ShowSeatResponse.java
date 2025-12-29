package com.booking.movieticket.dto.response;

import com.booking.movieticket.entity.enums.SeatType;
import com.booking.movieticket.entity.enums.ShowSeatStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShowSeatResponse {

    private Long showSeatId;
    private Long seatId;
    private String seatRow;
    private Integer seatNumber;
    private SeatType seatType;
    private BigDecimal price;
    private ShowSeatStatus status;
}
