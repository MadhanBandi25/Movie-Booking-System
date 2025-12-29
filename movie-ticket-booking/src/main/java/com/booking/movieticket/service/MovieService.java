package com.booking.movieticket.service;

import com.booking.movieticket.dto.request.MovieRequest;
import com.booking.movieticket.dto.response.MovieResponse;

import java.util.List;

public interface MovieService {

    MovieResponse createMovie(MovieRequest request);
    MovieResponse getMovieById(Long id);
    List<MovieResponse> getNowShowingMovies();
    List<MovieResponse> getComingSoonMovies();
    List<MovieResponse> getArchivedMovies();
    List<MovieResponse>  getAllMovies();

    List<MovieResponse> searchMoviesByTitle(String title);
    List<MovieResponse> getMoviesByLanguage(String language);

    MovieResponse updateMovie(Long id, MovieRequest request);
    MovieResponse archiveMovie(Long id);

}
