package com.booking.movieticket.service;

import com.booking.movieticket.dto.request.CityRequest;
import com.booking.movieticket.dto.response.CityResponse;
import java.util.List;

public interface CityService {

    CityResponse createCity(CityRequest request);
    CityResponse getCityById(Long id);
    List<CityResponse> getAllCities();

    List<CityResponse> getCitiesByState(String state);
    List<CityResponse> getCitiesByCountry(String country);
    List<CityResponse> getCityByName(String name);


    List<CityResponse> getInactiveCities();
    List<CityResponse> getActiveCities();

    CityResponse updateCity(Long id, CityRequest request);

    CityResponse activateCity(Long id);
    CityResponse deactivateCity(Long id);
}
