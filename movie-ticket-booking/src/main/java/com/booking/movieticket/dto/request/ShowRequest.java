package com.booking.movieticket.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ShowRequest {

    @NotNull(message = "Movie ID is required")
    private Long movieId;

    @NotNull(message = "Screen ID is required")
    private Long screenId;

    @NotNull(message = "Show Date is required")
    private LocalDate showDate;

    @NotNull(message = "Show time is required")
    private LocalTime showTime;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", message = "Base price must be >=0")
    private BigDecimal basePrice;
}
