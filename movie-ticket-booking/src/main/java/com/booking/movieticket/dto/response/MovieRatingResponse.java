package com.booking.movieticket.dto.response;

import lombok.Data;

@Data
public class MovieRatingResponse {

    private Long movieId;
    private Double averageRating;
    private Long totalRatings;
}
