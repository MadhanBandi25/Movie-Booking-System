package com.booking.movieticket.controller;

import com.booking.movieticket.dto.request.ShowRequest;
import com.booking.movieticket.dto.response.ShowResponse;
import com.booking.movieticket.service.ShowService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api-shows")
public class ShowController {


    @Autowired
    private ShowService showService;

    @PostMapping
    public ResponseEntity<ShowResponse> createShow(@Valid @RequestBody ShowRequest request) {
        ShowResponse response = showService.createShow(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShowResponse> getShowById(@PathVariable Long id) {
        return ResponseEntity.ok(showService.getShowById(id));
    }

    @GetMapping("/now-upcoming")
    public ResponseEntity<List<ShowResponse>> getNowAndUpcomingShows() {
        return ResponseEntity.ok(showService.getNowAndUpcomingShows());
    }

    @GetMapping("/now-showing")
    public ResponseEntity<List<ShowResponse>> getNowShowingShows() {
        return ResponseEntity.ok(showService.getNowShowingShows());
    }


    @GetMapping
    public ResponseEntity<List<ShowResponse>> getAllShows() {
        return ResponseEntity.ok(showService.getAllShows());
    }



    @GetMapping("/movie-name/{movieName}/date/{showDate}")
    public ResponseEntity<List<ShowResponse>> getShowsByMovieName(@PathVariable String movieName, @PathVariable LocalDate showDate) {

        return ResponseEntity.ok(showService.getShowsByMovieNameAndDates(movieName, showDate));
    }


    @GetMapping("/movie/{movieId}/date/{showDate}")
    public ResponseEntity<List<ShowResponse>> getShowsByMovieAndDate(
            @PathVariable Long movieId, @PathVariable LocalDate showDate) {

        return ResponseEntity.ok(showService.getShowsByMovieAndDate(movieId, showDate));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ShowResponse> cancelShow(@PathVariable Long id) {
        return ResponseEntity.ok(showService.cancelShow(id));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<ShowResponse> completeShow(@PathVariable Long id) {
        return ResponseEntity.ok(showService.completeShow(id));
    }


}

