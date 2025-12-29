package com.booking.movieticket.service;

import com.booking.movieticket.dto.request.MovieRatingRequest;
import com.booking.movieticket.dto.response.MovieRatingResponse;

public interface MovieRatingService {

    MovieRatingResponse rateMovie(Long movieId, MovieRatingRequest request);
    MovieRatingResponse getMovieRating(Long movieId);
}
