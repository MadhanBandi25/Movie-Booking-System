package com.booking.movieticket.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ScreenRequest {

    @NotBlank(message = "Screen name is required")
    @Size(min = 2, max = 100, message = "Screen name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Theatre  ID is required")
    private Long theatreId;

    @NotNull
    @Min(value = 1)
    private Integer totalRows;

    @NotNull
    @Min(value = 1)
    private Integer totalColumns;


}
