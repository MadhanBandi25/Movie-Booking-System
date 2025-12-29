package com.booking.movieticket.repository;

import com.booking.movieticket.entity.MovieRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MovieRatingRepository extends JpaRepository<MovieRating,Long> {


    Optional<MovieRating> findByMovie_IdAndUserId(Long movieId, Long userId);

    @Query("SELECT AVG(r.rating) FROM MovieRating r WHERE r.movie.id = :movieId")
    Double findAverageRating(Long movieId);

    @Query("SELECT COUNT(r) FROM MovieRating r WHERE r.movie.id = :movieId")
    Long countByMovieId(Long movieId);

}
