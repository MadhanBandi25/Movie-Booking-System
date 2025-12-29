package com.booking.movieticket.service.Impl;

import com.booking.movieticket.dto.request.MovieRatingRequest;
import com.booking.movieticket.dto.response.MovieRatingResponse;
import com.booking.movieticket.entity.Movie;
import com.booking.movieticket.entity.MovieRating;
import com.booking.movieticket.exception.ResourceNotFoundException;
import com.booking.movieticket.repository.MovieRatingRepository;
import com.booking.movieticket.repository.MovieRepository;
import com.booking.movieticket.service.MovieRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MovieRatingServiceImpl implements MovieRatingService {

    @Autowired
    private MovieRatingRepository movieRatingRepository;
    @Autowired
    private MovieRepository movieRepository;

    @Override
    public MovieRatingResponse rateMovie(Long movieId, MovieRatingRequest request) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Movie not found with id: " + movieId));

        MovieRating movieRating = movieRatingRepository
                .findByMovie_IdAndUserId(movieId, request.getUserId())
                .orElse(new MovieRating());

        movieRating.setMovie(movie);
        movieRating.setUserId(request.getUserId());
        movieRating.setRating(request.getRating()); // 1–10
        movieRating.setReview(request.getReview());

        movieRatingRepository.save(movieRating);

        return buildResponse(movieId);
    }

    @Override
    public MovieRatingResponse getMovieRating(Long movieId) {
        return buildResponse(movieId);
    }

    private MovieRatingResponse buildResponse(Long movieId) {
        Double avg = movieRatingRepository.findAverageRating(movieId);
        Long count = movieRatingRepository.countByMovieId(movieId);

        MovieRatingResponse response = new MovieRatingResponse();
        response.setMovieId(movieId);
        response.setAverageRating(avg == null ? 0.0 : Math.round(avg * 10) / 10.0);
        response.setTotalRatings(count);

        return response;
    }

}
