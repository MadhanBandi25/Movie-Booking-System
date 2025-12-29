package com.booking.movieticket.service.Impl;

import com.booking.movieticket.dto.request.ScreenRequest;
import com.booking.movieticket.dto.response.ScreenResponse;
import com.booking.movieticket.entity.Screen;
import com.booking.movieticket.entity.Theatre;
import com.booking.movieticket.exception.DuplicateResourceException;
import com.booking.movieticket.exception.ResourceNotFoundException;
import com.booking.movieticket.repository.ScreenRepository;
import com.booking.movieticket.repository.TheatreRepository;
import com.booking.movieticket.service.ScreenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ScreenServiceImpl implements ScreenService {

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private TheatreRepository theatreRepository;


    @Override
    public ScreenResponse createScreen(ScreenRequest request) {
        Theatre theatre=theatreRepository.findById(request.getTheatreId())
                .orElseThrow(()-> new ResourceNotFoundException("Theatre not found"));

        if (screenRepository.existsByNameAndTheatre_Id(request.getName(),request.getTheatreId())){
            throw new DuplicateResourceException("Screen already exists in this theatre");
        }

        Screen screen= new Screen();
        screen.setName(request.getName());
        screen.setTotalRows(request.getTotalRows());
        screen.setTotalColumns(request.getTotalColumns());
        screen.setCapacity(request.getTotalRows() * request.getTotalColumns());
        screen.setTheatre(theatre);
        screen.setActive(true);

        Screen saved= screenRepository.save(screen);

        return mapToScreenResponse(saved);
    }

    @Override
    public ScreenResponse getScreenById(Long id) {
        Screen screen= getScreen(id);

        return mapToScreenResponse(screen);
    }

    @Override
    public List<ScreenResponse> getAllScreens() {
        return screenRepository.findAll()
                .stream()
                .map(this::mapToScreenResponse)
                .toList();
    }

    @Override
    public List<ScreenResponse> searchActiveScreensByTheatreName(String theatreName) {
        List<Screen> screens =
                screenRepository.findByTheatre_NameContainingIgnoreCaseAndActiveTrue(theatreName);

        if (screens.isEmpty()) {
            throw new ResourceNotFoundException("No active screens found for theatre: " + theatreName);
        }

        return screens.stream()
                .map(this::mapToScreenResponse)
                .toList();
    }

    @Override
    public List<ScreenResponse> searchActiveScreensByTheatreAndCity(String theatreName, String cityName) {
        List<Screen> screens = screenRepository
                        .findByTheatre_NameContainingIgnoreCaseAndTheatre_City_NameContainingIgnoreCaseAndActiveTrue(theatreName, cityName);

        if (screens.isEmpty()) {
            throw new ResourceNotFoundException("No screens found for theatre containing '" + theatreName + "' in city '" + cityName + "'");
        }

        return screens.stream()
                .map(this::mapToScreenResponse)
                .toList();
    }

    @Override
    public List<ScreenResponse> getScreensByTheatre(Long theatreId) {
        return screenRepository.findByTheatre_Id(theatreId)
                .stream()
                .map(this::mapToScreenResponse)
                .toList();
    }

    @Override
    public List<ScreenResponse> getActiveScreens() {
        return screenRepository.findByActiveTrue()
                .stream()
                .map(this::mapToScreenResponse)
                .toList();
    }

    @Override
    public List<ScreenResponse> getInactiveScreens() {
        return screenRepository.findByActiveFalse()
                .stream()
                .map(this::mapToScreenResponse)
                .toList();
    }

    @Override
    public List<ScreenResponse> getActiveScreensByTheatre(Long theatreId) {

        return screenRepository.findByTheatre_IdAndActiveTrue(theatreId)
                .stream()
                .map(this::mapToScreenResponse)
                .toList();
    }

    @Override
    public ScreenResponse updateScreen(Long id, ScreenRequest request) {
        Screen screen=  getScreen(id);
        Theatre theatre=theatreRepository.findById(request.getTheatreId())
                        .orElseThrow(()-> new ResourceNotFoundException("Theatre not found with id"));

        if (!screen.getName().equals(request.getName())
                && screenRepository.existsByNameAndTheatre_Id(
                request.getName(), request.getTheatreId())) {
            throw new DuplicateResourceException(
                    "Screen already exists in this theatre");
        }

        screen.setName(request.getName());
        screen.setTheatre(theatre);
        screen.setTotalRows(request.getTotalRows());
        screen.setTotalColumns(request.getTotalColumns());
        screen.setCapacity(request.getTotalRows() * request.getTotalColumns());

        return mapToScreenResponse(screenRepository.save(screen));
    }

    @Override
    public void deleteScreen(Long id) {

        Screen screen= getScreen(id);
        screen.setActive(false);
        screenRepository.save(screen);
    }

    @Override
    public ScreenResponse activateScreen(Long id) {
        Screen screen= getScreen(id);
        screen.setActive(true);
        return mapToScreenResponse(screenRepository.save(screen));
    }

    @Override
    public ScreenResponse deactivateScreen(Long id) {
        Screen screen= getScreen(id);
        screen.setActive(false);
        return mapToScreenResponse(screenRepository.save(screen));
    }

    private Screen getScreen(Long id){
        return  screenRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Screen not found with id: "+ id));
    }


    private ScreenResponse mapToScreenResponse(Screen screen){

        ScreenResponse response= new ScreenResponse();
        response.setId(screen.getId());
        response.setName(screen.getName());
        response.setTotalRows(screen.getTotalRows());
        response.setTotalColumns(screen.getTotalColumns());
        response.setCapacity(screen.getCapacity());

        response.setTheatreId(screen.getTheatre().getId());
        response.setTheatreName(screen.getTheatre().getName());
        response.setTheatreAddress(screen.getTheatre().getAddress());

        response.setActive(screen.getActive());
        response.setCreatedAt(screen.getCreatedAt());
        return response;
    }
}
