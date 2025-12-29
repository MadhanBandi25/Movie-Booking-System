package com.booking.movieticket.service.Impl;

import com.booking.movieticket.dto.request.SeatBulkRequest;
import com.booking.movieticket.dto.request.SeatBulkUpdateRequest;
import com.booking.movieticket.dto.request.SeatRequest;
import com.booking.movieticket.dto.response.SeatResponse;
import com.booking.movieticket.entity.Screen;
import com.booking.movieticket.entity.Seat;
import com.booking.movieticket.entity.enums.SeatType;
import com.booking.movieticket.exception.DuplicateResourceException;
import com.booking.movieticket.exception.ResourceNotFoundException;
import com.booking.movieticket.repository.ScreenRepository;
import com.booking.movieticket.repository.SeatRepository;
import com.booking.movieticket.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SeatServiceImpl implements SeatService {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ScreenRepository screenRepository;


    private int rowIndex(String row) {
        return row.charAt(0) - 'A' + 1;
    }

    private SeatType resolveSeatType(SeatType type) {
        return type != null ? type : SeatType.NORMAL;
    }

    private void validateRowFormat(String row) {
        if (!row.matches("[A-Z]")) {
            throw new IllegalArgumentException(
                    "Seat row must be a single alphabet character (A-Z)");
        }
    }


    @Override
    public SeatResponse createSeat(SeatRequest request) {

        Screen screen = screenRepository.findById(request.getScreenId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Screen not found"));

        if (!screen.getActive()) {
            throw new IllegalStateException("Cannot create seat for inactive screen");
        }

        String row = request.getSeatRow().trim().toUpperCase();
        validateRowFormat(row);

        if (rowIndex(row) > screen.getTotalRows()) {
            throw new IllegalArgumentException("Seat row exceeds screen row limit");
        }

        if (request.getSeatNumber() > screen.getTotalColumns()) {
            throw new IllegalArgumentException("Seat number exceeds screen column limit");
        }

        long activeSeatCount =
                seatRepository.countByScreen_IdAndActiveTrue(screen.getId());

        if (activeSeatCount >= screen.getCapacity()) {
            throw new IllegalStateException("Screen capacity exceeded");
        }

        if (seatRepository.existsByScreen_IdAndSeatRowAndSeatNumber(
                screen.getId(), row, request.getSeatNumber())) {
            throw new DuplicateResourceException("Seat already exists");
        }

        Seat seat = new Seat();
        seat.setScreen(screen);
        seat.setSeatRow(row);
        seat.setSeatNumber(request.getSeatNumber());
        seat.setSeatType(resolveSeatType(request.getSeatType()));
        seat.setBasePrice(request.getBasePrice());
        seat.setActive(true);

        return mapToSeatResponse(seatRepository.save(seat));
    }


    @Override
    public List<SeatResponse> createBulkSeat(SeatBulkRequest request) {

        Screen screen = screenRepository.findById(request.getScreenId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Screen not found"));

        if (!screen.getActive()) {
            throw new IllegalStateException("Cannot create seats for inactive screen");
        }

        if (request.getEndSeat() < request.getStartSeat()) {
            throw new IllegalArgumentException("End seat must be >= start seat");
        }

        String row = request.getSeatRow().trim().toUpperCase();
        validateRowFormat(row);

        if (rowIndex(row) > screen.getTotalRows()) {
            throw new IllegalArgumentException("Seat row exceeds screen row limit");
        }

        if (request.getEndSeat() > screen.getTotalColumns()) {
            throw new IllegalArgumentException("Seat number exceeds screen column limit");
        }

        long activeSeatCount =
                seatRepository.countByScreen_IdAndActiveTrue(screen.getId());

        long seatsToAdd = request.getEndSeat() - request.getStartSeat() + 1;

        if (activeSeatCount + seatsToAdd > screen.getCapacity()) {
            throw new IllegalStateException("Screen capacity exceeded");
        }

        List<Seat> seats = new ArrayList<>();
        SeatType seatType = resolveSeatType(request.getSeatType());

        for (int i = request.getStartSeat(); i <= request.getEndSeat(); i++) {

            if (seatRepository.existsByScreen_IdAndSeatRowAndSeatNumber(
                    screen.getId(), row, i)) {
                throw new DuplicateResourceException(
                        "Seat already exists at " + row + i);
            }

            Seat seat = new Seat();
            seat.setScreen(screen);
            seat.setSeatRow(row);
            seat.setSeatNumber(i);
            seat.setSeatType(seatType);
            seat.setBasePrice(request.getBasePrice());
            seat.setActive(true);
            seats.add(seat);
        }

        return seatRepository.saveAll(seats)
                .stream()
                .map(this::mapToSeatResponse)
                .toList();
    }


    @Override
    public SeatResponse getSeatById(Long id) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Seat not found"));
        return mapToSeatResponse(seat);
    }

    @Override
    public List<SeatResponse> getAllSeats() {
        return seatRepository.findAll()
                .stream()
                .map(this::mapToSeatResponse)
                .toList();
    }

    @Override
    public List<SeatResponse> getSeatsByScreen(Long screenId) {

        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found"));

        List<Seat> seats = seatRepository.findByScreen_IdAndActiveTrue(screenId);

        if (seats.isEmpty()) {
            throw new ResourceNotFoundException("Seats not assigned to this screen");
        }

        return  seats.stream()
                .map(this::mapToSeatResponse)
                .toList();
    }

    @Override
    public List<SeatResponse> getActiveSeatsByScreen(Long screenId) {
        return seatRepository.findByScreen_IdAndActiveTrue(screenId)
                .stream()
                .map(this::mapToSeatResponse)
                .toList();
    }

    @Override
    public List<SeatResponse> getSeatsByType(SeatType seatType) {
        return seatRepository.findBySeatType(seatType)
                .stream()
                .map(this::mapToSeatResponse)
                .toList();
    }

    @Override
    public List<SeatResponse> getSeatsByScreenAndRow(Long screenId, String seatRow) {
        String row = seatRow.trim().toUpperCase();
        return seatRepository.findByScreen_IdAndSeatRow(screenId, row)
                .stream()
                .map(this::mapToSeatResponse)
                .toList();
    }

    @Override
    public List<SeatResponse> getActiveSeats() {
        return seatRepository.findByActiveTrue()
                .stream()
                .map(this::mapToSeatResponse)
                .toList();
    }

    @Override
    public List<SeatResponse> getInactiveSeats() {
        return seatRepository.findByActiveFalse()
                .stream()
                .map(this::mapToSeatResponse)
                .toList();
    }

    @Override
    public SeatResponse updateSeat(Long id, SeatRequest request) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Seat not found"));

        seat.setSeatType(resolveSeatType(request.getSeatType()));
        seat.setBasePrice(request.getBasePrice());

        return mapToSeatResponse(seatRepository.save(seat));
    }

    @Override
    public void deleteSeat(Long id) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Seat not found"));
        if (!seat.getActive()) {
            throw new ResourceNotFoundException(
                    "Seat already deleted with id: " + id);
        }

        seat.setActive(false);
        seatRepository.save(seat);
    }

    @Override
    public SeatResponse activateSeat(Long id) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Seat not found"));

        seat.setActive(true);
        return mapToSeatResponse(seatRepository.save(seat));
    }

    @Override
    public SeatResponse deactivateSeat(Long id) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Seat not found"));

        seat.setActive(false);
        return mapToSeatResponse(seatRepository.save(seat));
    }

    @Override
    public int bulkUpdateSeats(SeatBulkUpdateRequest request) {

        screenRepository.findById(request.getScreenId())
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found"));

        String row = request.getSeatRow() != null ? request.getSeatRow().trim().toUpperCase() : null;

        return seatRepository.bulkUpdateSeats(request.getScreenId(), row, request.getSeatType(), request.getBasePrice(), request.getActive());
    }



    private SeatResponse mapToSeatResponse(Seat seat) {
        SeatResponse response = new SeatResponse();
        response.setId(seat.getId());
        response.setScreenId(seat.getScreen().getId());
        response.setScreenName(seat.getScreen().getName());
        response.setSeatRow(seat.getSeatRow());
        response.setSeatNumber(seat.getSeatNumber());
        response.setSeatType(seat.getSeatType());
        response.setBasePrice(seat.getBasePrice());
        response.setActive(seat.getActive());
        response.setCreatedAt(seat.getCreatedAt());
        return response;
    }
}
