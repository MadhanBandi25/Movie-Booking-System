package com.booking.movieticket.dto.request;

import com.booking.movieticket.entity.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 40, message = "Name must be between 3 and 40 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[6-9]{1}[0-9]{9}$", message = "Phone must be 10 digits")
    private String phone;

    private UserRole role= UserRole.CUSTOMER;
}
