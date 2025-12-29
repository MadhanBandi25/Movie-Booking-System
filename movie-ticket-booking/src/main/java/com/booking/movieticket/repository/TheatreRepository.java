package com.booking.movieticket.repository;

import com.booking.movieticket.entity.Theatre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TheatreRepository extends JpaRepository<Theatre,Long> {

boolean existsByNameAndCity_Id(String name, Long cityId);

List<Theatre> findByActiveTrue();
List<Theatre> findByActiveFalse();

List<Theatre> findByCity_NameContainingIgnoreCaseAndActiveTrue(String cityName);
List<Theatre> findByNameContainingIgnoreCaseAndActiveTrue(String name);

Page<Theatre> findByCity_IdAndActiveTrue(Long cityId, Pageable pageable);

}
