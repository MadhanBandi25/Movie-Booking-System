package com.booking.movieticket.dto.request;

import com.booking.movieticket.entity.enums.SeatType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeatBulkUpdateRequest {

    @NotNull(message = "Screen ID is required")
    private Long screenId;

    private String seatRow;

    private SeatType seatType;
    private BigDecimal basePrice;
    private Boolean active;

}
