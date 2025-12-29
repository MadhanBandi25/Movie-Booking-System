package com.booking.movieticket.repository;

import com.booking.movieticket.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityRepository extends JpaRepository<City,Long> {

    boolean existsByNameAndStateAndCountry(String name, String  state,String country);
    boolean existsByPinCode(String pinCode);

    List<City> findByStateContainingIgnoreCase(String  state);
    List<City> findByCountryContainingIgnoreCase(String country);
    List<City> findByNameContainingIgnoreCase(String name);

    List<City> findByActiveTrue();
    List<City> findByActiveFalse();
}
