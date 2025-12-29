package com.booking.movieticket.dto.response;

import com.booking.movieticket.entity.enums.ShowStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowResponse {

    private Long id;

    private Long movieId;
    private String movieTitle;
    private String screenName;

    private String theatreName;

    private LocalDate showDate;
    private LocalTime showTime;

    private BigDecimal basePrice;
    private ShowStatus status;

    private Boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
