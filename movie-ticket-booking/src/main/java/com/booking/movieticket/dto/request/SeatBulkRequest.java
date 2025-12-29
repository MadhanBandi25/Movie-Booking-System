package com.booking.movieticket.dto.request;

import com.booking.movieticket.entity.enums.SeatType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeatBulkRequest {

    @NotNull(message = "Screen ID is required")
    private Long screenId;

    @NotBlank(message = "Seat Row is required")
    private String seatRow;

    @NotNull(message = "Start seat number is required")
    @Min(value = 1, message = "Start seat number must be at least 1")
    private Integer startSeat;

    @NotNull(message = "End seat is required")
    @Min(value = 1, message = "End seat number must be at least 1")
    private Integer endSeat;

    private SeatType seatType;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0",message = "Base price must be at least 0")
    private BigDecimal basePrice;
}
