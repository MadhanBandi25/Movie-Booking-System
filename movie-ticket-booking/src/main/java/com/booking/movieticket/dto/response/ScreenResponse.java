package com.booking.movieticket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScreenResponse {

    private Long id;
    private String name;

    private Integer totalRows;
    private Integer totalColumns;
    private Integer capacity;

    private Long theatreId;
    private String theatreName;
    private String theatreAddress;

    private Boolean active;
    private LocalDateTime createdAt;
}
