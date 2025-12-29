package com.booking.movieticket.repository;

import com.booking.movieticket.entity.ShowSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShowSeatRepository extends JpaRepository<ShowSeat, Long> {


    List<ShowSeat> findByShowId(Long showId);
    Optional<ShowSeat>  findByShowIdAndSeatId(Long showId, Long seatId);

    @Query(" SELECT COUNT(ss) FROM ShowSeat ss WHERE ss.show.id = :showId AND ss.status = 'AVAILABLE' " )
    Long countAvailableSeats(@Param("showId") Long showId);


    @Query(" SELECT ss FROM ShowSeat ss WHERE ss.show.id = :showId ORDER BY ss.seat.seatRow, ss.seat.seatNumber")
    List<ShowSeat> findSeatLayout(@Param("showId") Long showId);

    @Modifying
    @Query("UPDATE ShowSeat ss SET ss.status = 'AVAILABLE', ss.lockedAt = null, ss.lockedByUserId = null WHERE ss.status = 'LOCKED' AND ss.lockedAt < :expiryTime")
     void releaseExpiredLockedSeats(@Param("expiryTime") LocalDateTime expiryTime);

    boolean existsByShow_Id(Long showId);


}
