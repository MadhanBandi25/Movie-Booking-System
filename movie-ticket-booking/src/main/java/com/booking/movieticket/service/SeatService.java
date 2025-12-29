package com.booking.movieticket.service;

import com.booking.movieticket.dto.request.SeatBulkRequest;
import com.booking.movieticket.dto.request.SeatBulkUpdateRequest;
import com.booking.movieticket.dto.request.SeatRequest;
import com.booking.movieticket.dto.response.SeatResponse;
import com.booking.movieticket.entity.enums.SeatType;

import java.util.List;

public interface SeatService {

    SeatResponse createSeat(SeatRequest request);
    List<SeatResponse>  createBulkSeat(SeatBulkRequest request);
    SeatResponse getSeatById(Long id);
    List<SeatResponse> getAllSeats();

    List<SeatResponse> getSeatsByScreen(Long screenId);
    List<SeatResponse> getActiveSeatsByScreen(Long screenId);

    List<SeatResponse> getSeatsByType(SeatType seatType);
    List<SeatResponse> getSeatsByScreenAndRow(Long screenId, String  seatRow);


    List<SeatResponse> getActiveSeats();
    List<SeatResponse> getInactiveSeats();

    int bulkUpdateSeats(SeatBulkUpdateRequest request);
    SeatResponse updateSeat(Long id, SeatRequest request);
    void deleteSeat(Long id);

    SeatResponse activateSeat(Long id);
    SeatResponse deactivateSeat(Long id);

}
