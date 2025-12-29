package com.booking.movieticket.controller;

import com.booking.movieticket.dto.request.CityRequest;
import com.booking.movieticket.dto.response.CityResponse;
import com.booking.movieticket.service.CityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api-cities")
public class CityController {

    @Autowired
    private CityService cityService;

    // create city
    @PostMapping
    public ResponseEntity<CityResponse> createCity(@Valid @RequestBody CityRequest request){
        CityResponse response=cityService.createCity(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    // get city by id
    @GetMapping("/{id}")
    public ResponseEntity<CityResponse> getCityById(
            @PathVariable Long id) {

        return ResponseEntity.ok(cityService.getCityById(id));
    }

    // get all cities
    @GetMapping
    public ResponseEntity<List<CityResponse>> getAllCities() {

        return ResponseEntity.ok(cityService.getAllCities());
    }

    // get state by cities
    @GetMapping("/state/{state}")
    public ResponseEntity<List<CityResponse>> getCitiesByState(
            @PathVariable String state) {

        return ResponseEntity.ok(cityService.getCitiesByState(state));
    }

    // get country by states
    @GetMapping("/country/{country}")
    public ResponseEntity<List<CityResponse>> getCitiesByCountry(
            @PathVariable String country) {

        return ResponseEntity.ok(cityService.getCitiesByCountry(country));
    }
    // get country by cities
    @GetMapping("/city/{cityName}")
    public ResponseEntity<List<CityResponse>> getCitiesByName(
            @PathVariable String cityName) {

        return ResponseEntity.ok(cityService.getCityByName(cityName));
    }


    // get active  and inactive cities
    @GetMapping("/active")
    public ResponseEntity<List<CityResponse>> getActiveCities() {

        return ResponseEntity.ok(cityService.getActiveCities());
    }
    @GetMapping("/inactive")
    public ResponseEntity<List<CityResponse>> getInactiveCities() {

        return ResponseEntity.ok(cityService.getInactiveCities());
    }

    // update a city
    @PutMapping("/{id}")
    public ResponseEntity<CityResponse> updateCity(@PathVariable Long id, @Valid @RequestBody CityRequest request){
        return ResponseEntity.ok(cityService.updateCity(id,request));
    }

    // update a city to activate or deactivate
    @PatchMapping("/{id}/activate")
    public ResponseEntity<CityResponse> activateCity(@PathVariable Long id){
        return ResponseEntity.ok(cityService.activateCity(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<CityResponse> deactivateCity(@PathVariable Long id){
        return ResponseEntity.ok(cityService.deactivateCity(id));
    }
}
