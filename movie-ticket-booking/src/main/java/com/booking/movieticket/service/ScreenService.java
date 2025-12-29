package com.booking.movieticket.service;

import com.booking.movieticket.dto.request.ScreenRequest;
import com.booking.movieticket.dto.response.ScreenResponse;

import java.util.List;

public interface ScreenService {

    ScreenResponse createScreen(ScreenRequest request);
    ScreenResponse getScreenById(Long id);
    List<ScreenResponse> getAllScreens();

    List<ScreenResponse> searchActiveScreensByTheatreName(String theatreName);
    List<ScreenResponse> searchActiveScreensByTheatreAndCity(String theatreName, String cityName);

    List<ScreenResponse> getScreensByTheatre(Long theatreId);
    List<ScreenResponse> getActiveScreensByTheatre(Long theatreId);

    List<ScreenResponse> getActiveScreens();
    List<ScreenResponse> getInactiveScreens();

    ScreenResponse updateScreen(Long id, ScreenRequest request);
    void deleteScreen(Long id);

    ScreenResponse activateScreen(Long id);
    ScreenResponse deactivateScreen(Long id);
}
