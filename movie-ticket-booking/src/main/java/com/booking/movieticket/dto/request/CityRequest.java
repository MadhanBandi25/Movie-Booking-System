package com.booking.movieticket.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CityRequest {

    @NotBlank(message = "City name is required")
    @Size(min = 3, max = 50, message = "City name must be between 3 and 50 characters")
    private String name;

    @NotBlank(message = "State is required")
    @Size(min = 2, max = 50, message = "State must be between 2 and 50 characters")
    private String state;

    @NotBlank(message = "Country is required")
    @Size(min = 2, max = 50 , message = "Country must be between 2 and 50 characters")
    private String country;

    @NotBlank(message = "PinCode is required")
    @Size(min = 5, max = 10, message = "pin code must be between 5 and 10 characters")
    private String pinCode;
}
