package com.booking.movieticket.repository;

import com.booking.movieticket.entity.Seat;
import com.booking.movieticket.entity.enums.SeatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat,Long> {

    boolean existsByScreen_IdAndSeatRowAndSeatNumber(Long screenId, String seatRow, Integer seatNumber);

    long countByScreen_IdAndActiveTrue(Long screenId);

    List<Seat> findByScreen_Id(Long screenId);
    List<Seat> findByScreen_IdAndSeatRow(Long screenId, String seatRow);

    List<Seat> findByScreen_IdAndActiveTrue(Long screenId);
    List<Seat> findBySeatType(SeatType seatType);

    List<Seat> findByActiveTrue();
    List<Seat> findByActiveFalse();

    @Modifying
    @Query("""
    UPDATE Seat s
    SET 
        s.seatType = COALESCE(:seatType, s.seatType),
        s.basePrice = COALESCE(:basePrice, s.basePrice),
        s.active = COALESCE(:active, s.active)
    WHERE s.screen.id = :screenId
      AND (:seatRow IS NULL OR s.seatRow = :seatRow)
""")
    int bulkUpdateSeats(
            @Param("screenId") Long screenId,
            @Param("seatRow") String seatRow,
            @Param("seatType") SeatType seatType,
            @Param("basePrice") BigDecimal basePrice,
            @Param("active") Boolean active
    );


}

