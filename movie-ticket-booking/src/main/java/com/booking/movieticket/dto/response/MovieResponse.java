package com.booking.movieticket.dto.response;

import com.booking.movieticket.entity.enums.MovieStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieResponse {

    private Long id;
    private String title;
    private String description;
    private Integer durationMinutes;
    private String genre;
    private String language;
    private LocalDate releaseDate;
    private String director;
    private String cast;
    private String posterUrl;
    private String trailerUrl;
    private MovieStatus status;

    private Double averageRating;
    private Long totalRatings;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
