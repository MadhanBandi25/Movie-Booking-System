package com.booking.movieticket.controller;

import com.booking.movieticket.dto.request.TheatreRequest;
import com.booking.movieticket.dto.response.TheatreResponse;
import com.booking.movieticket.service.TheatreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api-theatres")
public class TheatreController {

    @Autowired
    private TheatreService theatreService;

    @PostMapping
    public ResponseEntity<TheatreResponse> createTheatre(@Valid @RequestBody TheatreRequest request){
        TheatreResponse response=theatreService.createTheatre(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TheatreResponse> getTheatreById(@PathVariable Long id){
        return ResponseEntity.ok(theatreService.getTheatreById(id));
    }

    @GetMapping
    public ResponseEntity<List<TheatreResponse>>  getAllTheatres(){
        return ResponseEntity.ok(theatreService.getAllTheatres());
    }

    @GetMapping("/search")
    public ResponseEntity<List<TheatreResponse>> searchByName(@RequestParam String name){
        return ResponseEntity.ok(theatreService.searchActiveTheatresByName(name));
    }

    @GetMapping("/city")
    public ResponseEntity<List<TheatreResponse>> getTheatresByCityName(@RequestParam String city) {
        return ResponseEntity.ok(theatreService.getActiveTheatresByCityName(city));
    }

    @GetMapping("/active")
    public ResponseEntity<List<TheatreResponse>> getActives(){
        return ResponseEntity.ok(theatreService.getActiveTheatres());
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<TheatreResponse>> getInactives(){
        return ResponseEntity.ok(theatreService.getInactiveTheatres());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TheatreResponse> updateTheatre(@PathVariable Long id,@Valid @RequestBody TheatreRequest request){
        return ResponseEntity.ok(theatreService.updateTheatre(id,request));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<TheatreResponse> activate(@PathVariable Long id){
        return ResponseEntity.ok(theatreService.activateTheatre(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<TheatreResponse> deactivate(@PathVariable Long id){
        return ResponseEntity.ok(theatreService.deactivateTheatre(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheatre(@PathVariable Long id){
        theatreService.deleteTheatre(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/city/{cityId}/active/paged")
    public ResponseEntity<Page<TheatreResponse>> getActiveByCityPaged(
            @PathVariable Long cityId,
            @RequestParam int page,
            @RequestParam int size
    ) {
        return ResponseEntity.ok(
                theatreService.getActiveTheatresByCityPaged(cityId, page, size)
        );
    }


}
