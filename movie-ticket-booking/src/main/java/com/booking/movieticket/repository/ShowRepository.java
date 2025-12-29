package com.booking.movieticket.repository;

import com.booking.movieticket.entity.Show;
import com.booking.movieticket.entity.enums.ShowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ShowRepository extends JpaRepository<Show,Long> {


    List<Show> findByMovie_IdAndShowDateAndActiveTrue(Long movieId, LocalDate showDate);

    List<Show> findByActiveTrue();


    boolean  existsByScreen_IdAndShowDateAndShowTimeAndActiveTrue(
            Long screenId,
            LocalDate showDate,
            LocalTime showTime
    );

    @Query("""
        SELECT s FROM Show s
        WHERE s.screen.id = :screenId
        AND s.active = true
    """)
    List<Show> findActiveShowsByScreen(@Param("screenId") Long screenId);

    @Query("""
SELECT s FROM Show s
WHERE s.active = true
AND s.status IN (:statuses)
AND (
      s.showDate > :today
   OR (s.showDate = :today AND s.showTime >= :now)
)
ORDER BY s.showDate, s.showTime
""")
    List<Show> findUpcomingAndRunningShows(
            @Param("statuses") List<ShowStatus> statuses,
            @Param("today") LocalDate today,
            @Param("now") LocalTime now
    );



    List<Show> findByStatus(ShowStatus status);

    @Query("""
SELECT s FROM Show s
WHERE s.active = true
AND s.status = com.booking.movieticket.entity.enums.ShowStatus.RUNNING
ORDER BY s.showDate, s.showTime
""")
    List<Show> findNowShowingShows();



}
