package com.booking.movieticket.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MovieRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 500, message = "Title must be between 1 and 500 characters")
    private String  title;

    @Size(max = 3000, message = "Description must not exceed 3000 characters")
    private String description;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 500, message = "Duration must not exceed 500 minutes")
    private Integer durationMinutes;

    @NotBlank(message = "Genre is required")
    @Size(max = 200, message = "Genre must not exceed 200 characters")
    private String genre;

    @NotBlank(message = "Language is required")
    @Size(max = 200, message = "Language must not exceed 200 characters")
    private String language;

    @NotNull(message = "Release date is required")
    private LocalDate releaseDate;

    @Size(max = 200, message = "Director name must not exceed 200 characters")
    private String director;

    @Size(max = 500 , message = "Cast must not exceed 500 characters")
    private String cast;

    @Pattern(regexp = "^(http|https)://.*$", message = "Poster URL must be a valid URL")
    private String posterUrl;

    @Pattern(regexp = "^(http|https)://.*$", message = "Trailer URL must be a valid URL")
    private String trailerUrl;
}
