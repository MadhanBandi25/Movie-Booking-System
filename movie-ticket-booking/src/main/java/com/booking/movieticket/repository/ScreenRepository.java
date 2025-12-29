package com.booking.movieticket.repository;

import com.booking.movieticket.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScreenRepository extends JpaRepository<Screen, Long> {

    boolean existsByNameAndTheatre_Id(String name, Long theatreId);

    List<Screen> findByTheatre_NameContainingIgnoreCaseAndActiveTrue(String theatreName);
    List<Screen> findByTheatre_NameContainingIgnoreCaseAndTheatre_City_NameContainingIgnoreCaseAndActiveTrue(String theatreName, String cityName);


    List<Screen> findByTheatre_Id(Long theatreId);
    List<Screen> findByTheatre_IdAndActiveTrue(Long theatreId);

    List<Screen> findByActiveTrue();
    List<Screen> findByActiveFalse();
}
