package com.booking.movieticket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CityResponse {

    private Long id;
    private String name;
    private String state;
    private String country;
    private String pinCode;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
