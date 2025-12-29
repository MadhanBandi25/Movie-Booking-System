package com.booking.movieticket.service;

import com.booking.movieticket.dto.request.ShowRequest;
import com.booking.movieticket.dto.response.ShowResponse;

import java.time.LocalDate;
import java.util.List;

public interface ShowService {

    ShowResponse createShow(ShowRequest request);
    ShowResponse getShowById(Long id);

    List<ShowResponse> getAllShows();

    List<ShowResponse> getNowAndUpcomingShows();
    List<ShowResponse> getNowShowingShows();



    List<ShowResponse> getShowsByMovieNameAndDates(String movieName, LocalDate showDate);
    List<ShowResponse> getShowsByMovieAndDate(Long movieId,LocalDate showDate);

    ShowResponse cancelShow(Long id);
    ShowResponse completeShow(Long id);


}
