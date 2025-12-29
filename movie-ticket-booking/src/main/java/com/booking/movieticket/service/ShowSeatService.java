package com.booking.movieticket.service;

import com.booking.movieticket.dto.response.ShowSeatResponse;

import java.util.List;

public interface ShowSeatService {

    void initializeShowSeat(Long showId);

    List<ShowSeatResponse> getShowSeatLayout(Long showId);
    Long getAvailableSeatCount(Long showId);

    void lockSeats(Long showId, List<Long> seatIds, Long userId);
    void bookSeats(Long showId, List<Long> seatIds, Long userId);
    void unlockSeats(Long showId, List<Long> seatIds, Long userId);
    void releaseExpiredLocks();
}
