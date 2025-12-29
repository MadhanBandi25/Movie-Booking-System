package com.booking.movieticket.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TheatreRequest {

    @NotBlank(message = "Theatre name is required")
    @Size(min = 2, max = 200, message = "Theatre name must be between 2 and 200 characters")
    private String  name;

    @NotBlank(message = "Theatre address is required")
    @Size(min = 3, max = 500, message = "Address must be between 3 and 500 characters")
    private String address;

    @NotNull(message = "City ID is required")
    private Long cityId;
}
