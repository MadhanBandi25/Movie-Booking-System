package com.booking.movieticket.service.Impl;

import com.booking.movieticket.dto.request.CityRequest;
import com.booking.movieticket.dto.response.CityResponse;
import com.booking.movieticket.entity.City;
import com.booking.movieticket.exception.DuplicateResourceException;
import com.booking.movieticket.exception.ResourceNotFoundException;
import com.booking.movieticket.repository.CityRepository;
import com.booking.movieticket.service.CityService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CityServiceImpl implements CityService {

    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private ModelMapper modelMapper;

    // Create city (ADMIN)
    @Override
    public CityResponse createCity(CityRequest request) {

        if(cityRepository.existsByNameAndStateAndCountry(
                request.getName(),
                request.getState(),
                request.getCountry())){
            throw new DuplicateResourceException("City already exists in this state and country");
        }

        if (cityRepository.existsByPinCode(request.getPinCode())){
            throw new DuplicateResourceException("Pin Code already assigned to another city");
        }

        City city= modelMapper.map(request,City.class);
        city.setActive(true);

        City saved= cityRepository.save(city);
        return modelMapper.map(saved,CityResponse.class);
    }

    @Override
    public CityResponse getCityById(Long id) {
        City city= getCity(id);
        return modelMapper.map(city,CityResponse.class);
    }

    @Override
    public List<CityResponse> getAllCities() {
        return cityRepository.findAll()
                .stream()
                .map(city -> modelMapper.map(city,CityResponse.class))
                .toList();
    }

    @Override
    public List<CityResponse> getCitiesByState(String state) {
        return cityRepository.findByStateContainingIgnoreCase(state)
                .stream()
                .map(city -> modelMapper.map(city,CityResponse.class))
                .toList();
    }

    @Override
    public List<CityResponse> getCitiesByCountry(String country) {
        return cityRepository.findByCountryContainingIgnoreCase(country)
                .stream()
                .map(city -> modelMapper.map(city,CityResponse.class))
                .toList();
    }

    @Override
    public List<CityResponse> getCityByName(String name) {
        return cityRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(city -> modelMapper.map(city,CityResponse.class))
                .toList();
    }

    @Override
    public List<CityResponse> getActiveCities() {
        return cityRepository.findByActiveTrue()
                .stream()
                .map(city -> modelMapper.map(city,CityResponse.class))
                .toList();
    }

    @Override
    public List<CityResponse> getInactiveCities() {
        return cityRepository.findByActiveFalse()
                .stream()
                .map(city -> modelMapper.map(city,CityResponse.class))
                .toList();
    }

    @Override
    public CityResponse updateCity(Long id, CityRequest request) {
        City city= getCity(id);


        if (!city.getPinCode().equals(request.getPinCode()) &&
         cityRepository.existsByPinCode(request.getPinCode())){
            throw new DuplicateResourceException("Pin Code already assigned to another city");
        }

        if( (!city.getName().equals(request.getName())
                || !city.getState().equals(request.getState())
                || !city.getCountry().equals(request.getCountry()))
                && cityRepository.existsByNameAndStateAndCountry(
                        request.getName(),
                        request.getState(),
                        request.getCountry())){
            throw new DuplicateResourceException("City already exists in this state and country");
        }

        city.setName(request.getName());
        city.setState(request.getState());
        city.setCountry(request.getCountry());
        city.setPinCode(request.getPinCode());

        City updated= cityRepository.save(city);
        return modelMapper.map(updated,CityResponse.class);
    }

    @Override
    public CityResponse activateCity(Long id) {
        City city= getCity(id);

        city.setActive(true);
        City update = cityRepository.save(city);
        return modelMapper.map(update,CityResponse.class);
    }

    @Override
    public CityResponse deactivateCity(Long id) {
        City city=  getCity(id);
        city.setActive(false);
        City saved= cityRepository.save(city);
        return modelMapper.map(saved, CityResponse.class);
    }

    private City getCity(Long id){
        return  cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + id));
    }
}
