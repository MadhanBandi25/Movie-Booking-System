package com.booking.movieticket.repository;

import com.booking.movieticket.entity.Movie;
import com.booking.movieticket.entity.enums.MovieStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    boolean existsByTitleIgnoreCaseAndLanguageIgnoreCase(String title, String language);
    boolean existsByTitleIgnoreCaseAndLanguageIgnoreCaseAndIdNot(String title, String language, Long id);

    List<Movie> findByLanguageContainingIgnoreCaseAndStatus(String language, MovieStatus status);
    List<Movie> findByTitleContainingIgnoreCaseAndStatusIn(String title, List<MovieStatus> statuses);

    List<Movie> findByTitleContainingIgnoreCaseAndActiveTrue(String title);

    List<Movie> findByStatus(MovieStatus status);

}
