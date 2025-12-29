package com.booking.movieticket.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SeatLockRequest {

    @NotNull(message = "Show ID is required")
    private Long showId;

    @NotEmpty(message = "Seats IDs are required")
    private List<Long> seatIds;

    @NotNull(message = "User ID is required")
    private Long userId;
}
