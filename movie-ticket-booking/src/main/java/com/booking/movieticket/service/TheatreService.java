package com.booking.movieticket.service;

import com.booking.movieticket.dto.request.TheatreRequest;
import com.booking.movieticket.dto.response.TheatreResponse;
import org.springframework.data.domain.Page;


import java.util.List;

public interface TheatreService {

    TheatreResponse createTheatre (TheatreRequest request);
    TheatreResponse getTheatreById(Long id);
    List<TheatreResponse> getAllTheatres();

    Page<TheatreResponse> getActiveTheatresByCityPaged(Long cityId, int page, int size);

    List<TheatreResponse> getActiveTheatresByCityName(String cityName);
    List<TheatreResponse> searchActiveTheatresByName(String name);

    List<TheatreResponse> getActiveTheatres();
    List<TheatreResponse> getInactiveTheatres();

    TheatreResponse updateTheatre(Long id, TheatreRequest request);
    void deleteTheatre(Long id);

    TheatreResponse activateTheatre(Long id);
    TheatreResponse deactivateTheatre(Long id);

}
