package com.booking.movieticket.controller;

import com.booking.movieticket.dto.request.MovieRequest;
import com.booking.movieticket.dto.response.MovieResponse;
import com.booking.movieticket.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api-movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    // created a movie
    @PostMapping
    public ResponseEntity<MovieResponse> createMovie(@Valid @RequestBody MovieRequest request) {

        MovieResponse response = movieService.createMovie(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // get movie by given id
    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    // present running movies
    @GetMapping("/now-showing")
    public ResponseEntity<List<MovieResponse>> nowShowingMovies() {
        return ResponseEntity.ok(movieService.getNowShowingMovies());
    }

    // coming-soon movies
    @GetMapping("/coming-soon")
    public ResponseEntity<List<MovieResponse>> getComingSoonMovies() {
        return ResponseEntity.ok(movieService.getComingSoonMovies());
    }

    // archived movies
    @GetMapping("/archived")
    public ResponseEntity<List<MovieResponse>> getArchivedMovies() {
        return ResponseEntity.ok(movieService.getArchivedMovies());
    }


    @GetMapping
    public ResponseEntity<List<MovieResponse>> getAllMovies(){
        List<MovieResponse> responses= movieService.getAllMovies();
        return ResponseEntity.ok(responses);
    }

    // search by movie as title
    @GetMapping("/search")
    public ResponseEntity<List<MovieResponse>> searchByTitle(@RequestParam String title) {
        return ResponseEntity.ok(movieService.searchMoviesByTitle(title));
    }

    // search by movie as language
    @GetMapping("/language/{language}")
    public ResponseEntity<List<MovieResponse>> getByLanguage(@PathVariable String language) {
        return ResponseEntity.ok(movieService.getMoviesByLanguage(language));
    }

    // update a movie
    @PutMapping("/{id}")
    public ResponseEntity<MovieResponse> updateMovie(@PathVariable Long id,
            @Valid @RequestBody MovieRequest request) {
        return ResponseEntity.ok(movieService.updateMovie(id, request));
    }

    @PutMapping("/{id}/archive")
    public ResponseEntity<MovieResponse> archive(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.archiveMovie(id));
    }

}
