package com.booking.movieticket.service.Impl;

import com.booking.movieticket.dto.request.TheatreRequest;
import com.booking.movieticket.dto.response.TheatreResponse;
import com.booking.movieticket.entity.City;
import com.booking.movieticket.entity.Theatre;
import com.booking.movieticket.exception.DuplicateResourceException;
import com.booking.movieticket.exception.InvalidRequestException;
import com.booking.movieticket.exception.ResourceNotFoundException;
import com.booking.movieticket.repository.CityRepository;
import com.booking.movieticket.repository.TheatreRepository;
import com.booking.movieticket.service.TheatreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TheatreServiceImpl implements TheatreService {

    @Autowired
    private TheatreRepository theatreRepository;

    @Autowired
    public CityRepository cityRepository;

    @Override
    public TheatreResponse createTheatre(TheatreRequest request) {

        City city= cityRepository.findById(request.getCityId())
                .orElseThrow(()-> new ResourceNotFoundException("City not found"));

        if (!city.getActive()) {
            throw new InvalidRequestException("Cannot add theatre to inactive city");
        }
        if(theatreRepository.existsByNameAndCity_Id(request.getName(),request.getCityId())){
            throw new DuplicateResourceException("Theatre already exists in this city");
        }

        Theatre theatre=new Theatre();
        theatre.setName(request.getName());
        theatre.setAddress(request.getAddress());
        theatre.setCity(city);
        theatre.setActive(true);

        Theatre saved= theatreRepository.save(theatre);

        return mapToTheatreResponse(saved);
    }

    @Override
    public TheatreResponse getTheatreById(Long id) {
        Theatre theatre= getTheatre(id);
        return mapToTheatreResponse(theatre);
    }

    @Override
    public List<TheatreResponse> getAllTheatres() {
        return theatreRepository.findAll()
                .stream()
                .map(this::mapToTheatreResponse)
                .toList();
    }

    @Override
    public List<TheatreResponse> getActiveTheatresByCityName(String cityName) {

        return theatreRepository
                .findByCity_NameContainingIgnoreCaseAndActiveTrue(cityName)
                .stream()
                .map(this::mapToTheatreResponse)
                .toList();
    }

    @Override
    public List<TheatreResponse> searchActiveTheatresByName(String name) {
        return theatreRepository.findByNameContainingIgnoreCaseAndActiveTrue(name)
                .stream()
                .map(this::mapToTheatreResponse)
                .toList();
    }

    @Override
    public Page<TheatreResponse> getActiveTheatresByCityPaged(Long cityId, int page, int size) {
        return theatreRepository.findByCity_IdAndActiveTrue(cityId, PageRequest.of(page,size))
                .map(this::mapToTheatreResponse);
    }


    @Override
    public List<TheatreResponse> getActiveTheatres() {
        return theatreRepository.findByActiveTrue()
                .stream()
                .map(this::mapToTheatreResponse)
                .toList();
    }

    @Override
    public List<TheatreResponse> getInactiveTheatres() {
        return theatreRepository.findByActiveFalse()
                .stream()
                .map(this::mapToTheatreResponse)
                .toList();
    }



    @Override
    public TheatreResponse updateTheatre(Long id, TheatreRequest request) {
        Theatre theatre= getTheatre(id);
        City city= cityRepository.findById(request.getCityId())
                .orElseThrow(()->new ResourceNotFoundException("City not found"));

        if (!theatre.getName().equals(request.getName())
                && theatreRepository.existsByNameAndCity_Id(request.getName(), request.getCityId())) {
            throw new DuplicateResourceException("Theatre already exists in this city");
        }

        theatre.setName(request.getName());
        theatre.setAddress(request.getAddress());
        theatre.setCity(city);

        Theatre saved= theatreRepository.save(theatre);
        return mapToTheatreResponse(saved);
    }

    // admin can do this
    @Override
    public void deleteTheatre(Long id) {
        Theatre theatre=  getTheatre(id);
        theatre.setActive(false);
        theatreRepository.save(theatre);
    }

    @Override
    public TheatreResponse activateTheatre(Long id) {
        Theatre theatre= getTheatre(id);
        theatre.setActive(true);
        Theatre saved= theatreRepository.save(theatre);
        return mapToTheatreResponse(saved);
    }

    @Override
    public TheatreResponse deactivateTheatre(Long id) {
        Theatre theatre = getTheatre(id);
        theatre.setActive(false);
        return mapToTheatreResponse(theatreRepository.save(theatre));
    }


    private Theatre getTheatre(Long id){
        return theatreRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Theatre not found"));
    }


    private TheatreResponse mapToTheatreResponse(Theatre theatre){
        TheatreResponse response= new TheatreResponse();
        response.setId(theatre.getId());
        response.setName(theatre.getName());
        response.setAddress(theatre.getAddress());
        response.setCityId(theatre.getCity().getId());
        response.setCityName(theatre.getCity().getName());
        response.setActive(theatre.getActive());
        response.setCreatedAt(theatre.getCreatedAt());
        response.setUpdatedAt(theatre.getUpdatedAt());

        return response;

    }
}
