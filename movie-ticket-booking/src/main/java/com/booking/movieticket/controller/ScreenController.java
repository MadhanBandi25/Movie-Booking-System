package com.booking.movieticket.controller;

import com.booking.movieticket.dto.request.ScreenRequest;
import com.booking.movieticket.dto.response.ScreenResponse;
import com.booking.movieticket.service.ScreenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api-screens")
public class ScreenController {

    @Autowired
    private ScreenService screenService;

    @PostMapping
    public ResponseEntity<ScreenResponse> createScreen(@Valid @RequestBody ScreenRequest request){
        ScreenResponse response=screenService.createScreen(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScreenResponse> getScreenById(@PathVariable Long id){
        return ResponseEntity.ok(screenService.getScreenById(id));
    }

    @GetMapping
    public ResponseEntity<List<ScreenResponse>> getAllScreens(){
        return ResponseEntity.ok(screenService.getAllScreens());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ScreenResponse>> searchScreensByTheatre(@RequestParam String theatre) {

        return ResponseEntity.ok(screenService.searchActiveScreensByTheatreName(theatre));
    }

    @GetMapping("/search-by-city")
    public ResponseEntity<List<ScreenResponse>> searchScreensByTheatreAndCity(@RequestParam String theatre, @RequestParam String city) {

        return ResponseEntity.ok(screenService.searchActiveScreensByTheatreAndCity(theatre, city));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScreen(@PathVariable Long id){
        screenService.deleteScreen(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScreenResponse> updateScreen(@PathVariable Long id, @Valid @RequestBody ScreenRequest request){
        ScreenResponse response= screenService.updateScreen(id,request);
        return ResponseEntity.ok(response);
    }


    // admin part

    @GetMapping("/theatre/{id}")
    public ResponseEntity<List<ScreenResponse>> getScreensByTheatre(@PathVariable Long id){
        return ResponseEntity.ok(screenService.getScreensByTheatre(id));
    }

    @GetMapping("/theatre/{id}/active")
    public ResponseEntity<List<ScreenResponse>> getActiveScreensByTheatre(@PathVariable Long id){
        return ResponseEntity.ok(screenService.getActiveScreensByTheatre(id));
    }

    @GetMapping("/active")
    public ResponseEntity<List<ScreenResponse>>  getActiveScreens(){
        return ResponseEntity.ok(screenService.getActiveScreens());
    }
    @GetMapping("/inactive")
    public ResponseEntity<List<ScreenResponse>>  getInactiveScreens(){
        return ResponseEntity.ok(screenService.getInactiveScreens());
    }
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ScreenResponse> activateScreen(@PathVariable Long id){
        return ResponseEntity.ok(screenService.activateScreen(id));
    }
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ScreenResponse> deactivateScreen (@PathVariable Long id){
        return ResponseEntity.ok(screenService.deactivateScreen(id));
    }

}
