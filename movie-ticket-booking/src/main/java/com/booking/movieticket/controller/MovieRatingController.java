package com.booking.movieticket.controller;

import com.booking.movieticket.dto.request.MovieRatingRequest;
import com.booking.movieticket.dto.response.MovieRatingResponse;
import com.booking.movieticket.service.MovieRatingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api-movies/{movieId}/ratings")
public class MovieRatingController {

    @Autowired
    private MovieRatingService movieRatingService;

    @PostMapping
    public ResponseEntity<MovieRatingResponse> rateMovie(@PathVariable Long movieId,
            @Valid @RequestBody MovieRatingRequest request) {

        return ResponseEntity.ok(movieRatingService.rateMovie(movieId, request));
    }

    @GetMapping
    public ResponseEntity<MovieRatingResponse> getMovieRating(@PathVariable Long movieId) {

        return ResponseEntity.ok(movieRatingService.getMovieRating(movieId));
    }

}
