package com.booking.movieticket.service.Impl;

import com.booking.movieticket.dto.request.MovieRequest;
import com.booking.movieticket.dto.response.MovieResponse;
import com.booking.movieticket.entity.Movie;
import com.booking.movieticket.entity.enums.MovieStatus;
import com.booking.movieticket.exception.DuplicateResourceException;
import com.booking.movieticket.exception.ResourceNotFoundException;
import com.booking.movieticket.repository.MovieRatingRepository;
import com.booking.movieticket.repository.MovieRepository;
import com.booking.movieticket.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Service
@Transactional
public class MovieServiceImpl implements MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieRatingRepository movieRatingRepository;

    @Override
    public MovieResponse createMovie(MovieRequest request) {

        if (movieRepository.existsByTitleIgnoreCaseAndLanguageIgnoreCase(request.getTitle(), request.getLanguage())){
            throw  new DuplicateResourceException("Movie already present with same title and language");
        }

        Movie movie= new Movie();
        mapRequestToEntity(request, movie);

        movie.setStatus(determineInitialStatus(request.getReleaseDate()));

        return mapToMovieResponse(movieRepository.save(movie));
    }

    @Override
    public MovieResponse getMovieById(Long id) {
        Movie movie= getMovie(id);
        return mapToMovieResponse(movie);
    }

    @Override
    public List<MovieResponse> getNowShowingMovies() {
        return  movieRepository.findByStatus(MovieStatus.NOW_SHOWING)
                .stream()
                .map(this::mapToMovieResponse)
                .toList();
    }

    @Override
    public List<MovieResponse> getComingSoonMovies() {
        return movieRepository.findByStatus(MovieStatus.COMING_SOON)
                .stream()
                .map(this::mapToMovieResponse)
                .toList();
    }

    @Override
    public List<MovieResponse> getArchivedMovies() {
        return movieRepository.findByStatus(MovieStatus.ARCHIVED)
                .stream()
                .map(this::mapToMovieResponse)
                .toList();
    }

    @Override
    public List<MovieResponse> getAllMovies() {
        return movieRepository.findAll()
                .stream()
                .map(this::mapToMovieResponse)
                .toList();
    }

    @Override
    public List<MovieResponse> searchMoviesByTitle(String title) {
        return movieRepository.findByTitleContainingIgnoreCaseAndStatusIn(title, List.of(MovieStatus.NOW_SHOWING,MovieStatus.COMING_SOON))
                .stream()
                .map(this::mapToMovieResponse)
                .toList();
    }

    @Override
    public List<MovieResponse> getMoviesByLanguage(String language) {
        return movieRepository.findByLanguageContainingIgnoreCaseAndStatus(language, MovieStatus.NOW_SHOWING)
                .stream()
                .map(this::mapToMovieResponse)
                .toList();
    }


    @Override
    public MovieResponse updateMovie(Long id, MovieRequest request) {

        Movie movie=getMovie(id);

        if (movieRepository.existsByTitleIgnoreCaseAndLanguageIgnoreCaseAndIdNot(
                request.getTitle(), request.getLanguage(), id)) {
            throw new DuplicateResourceException(
                    "Movie already present with same title and language");
        }

        mapRequestToEntity(request, movie);

        if (movie.getStatus() != MovieStatus.ARCHIVED) {
            movie.setStatus(determineInitialStatus(request.getReleaseDate()));
        }

        return mapToMovieResponse(movieRepository.save(movie));
    }

    @Override
    public MovieResponse archiveMovie(Long id) {
        Movie movie= getMovie(id);
        movie.setStatus(MovieStatus.ARCHIVED);
        return mapToMovieResponse(movieRepository.save(movie));
    }


    private Movie getMovie(Long id){
        return movieRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Movie not found with id: "+ id));
    }


    private MovieStatus determineInitialStatus(LocalDate releaseDate){
        return releaseDate.
                isAfter(LocalDate.now()) ? MovieStatus.COMING_SOON : MovieStatus.NOW_SHOWING;
    }

    private void mapRequestToEntity(MovieRequest request, Movie movie) {
        movie.setTitle(request.getTitle());
        movie.setDescription(request.getDescription());
        movie.setDurationMinutes(request.getDurationMinutes());
        movie.setGenre(request.getGenre());
        movie.setLanguage(request.getLanguage());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setDirector(request.getDirector());
        movie.setCast(request.getCast());
        movie.setPosterUrl(request.getPosterUrl());
        movie.setTrailerUrl(request.getTrailerUrl());
    }


    private MovieResponse mapToMovieResponse(Movie movie){

        Double avg = movieRatingRepository.findAverageRating(movie.getId());
        Long count = movieRatingRepository.countByMovieId(movie.getId());


        MovieResponse response= new MovieResponse();

        response.setId(movie.getId());
        response.setTitle(movie.getTitle());
        response.setDescription(movie.getDescription());
        response.setDurationMinutes(movie.getDurationMinutes());
        response.setGenre(movie.getGenre());
        response.setLanguage(movie.getLanguage());
        response.setReleaseDate(movie.getReleaseDate());
        response.setDirector(movie.getDirector());
        response.setCast(movie.getCast());
        response.setPosterUrl(movie.getPosterUrl());
        response.setTrailerUrl(movie.getTrailerUrl());
        response.setStatus(movie.getStatus());
        response.setAverageRating(avg == null ? 0.0 : avg);
        response.setTotalRatings(count);
        response.setCreatedAt(movie.getCreatedAt());
        response.setUpdatedAt(movie.getUpdatedAt());
        return response;
    }
}
