package com.booking.movieticket.service.Impl;

import com.booking.movieticket.dto.response.ShowSeatResponse;
import com.booking.movieticket.entity.Seat;
import com.booking.movieticket.entity.Show;
import com.booking.movieticket.entity.ShowSeat;
import com.booking.movieticket.entity.enums.ShowSeatStatus;
import com.booking.movieticket.entity.enums.ShowStatus;
import com.booking.movieticket.exception.*;
import com.booking.movieticket.repository.SeatRepository;
import com.booking.movieticket.repository.ShowRepository;
import com.booking.movieticket.repository.ShowSeatRepository;
import com.booking.movieticket.service.ShowSeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class ShowSeatServiceImpl implements ShowSeatService {

    @Autowired
    private ShowSeatRepository showSeatRepository;
    @Autowired
    private ShowRepository showRepository;
    @Autowired
    private SeatRepository seatRepository;

    @Value("${app.seat-lock-duration:5}")
    private int lockDurationMinutes;

    @Override
    public void initializeShowSeat(Long showId) {

        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));

        if (show.getStatus() != ShowStatus.ACTIVE) {
            throw new BusinessException(
                    "Cannot initialize seats for cancelled or completed show"
            );
        }


        if (showSeatRepository.existsByShow_Id(showId)) {
            throw new ShowSeatAlreadyInitializedException("Show seats are already initialized for this show");
        }

        List<Seat> seats = seatRepository
                .findByScreen_IdAndActiveTrue(show.getScreen().getId());

        for (Seat seat : seats) {
            ShowSeat ss = new ShowSeat();
            ss.setShow(show);
            ss.setSeat(seat);
            ss.setPrice(show.getBasePrice());
            ss.setStatus(ShowSeatStatus.AVAILABLE);
            showSeatRepository.save(ss);
        }

    }

    @Override
    public List<ShowSeatResponse> getShowSeatLayout(Long showId) {

        if (!showSeatRepository.existsByShow_Id(showId)) {
            throw new BusinessException("Show is not assigned to the screen");
        }
        return showSeatRepository.findSeatLayout(showId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public Long getAvailableSeatCount(Long showId) {

        if (!showSeatRepository.existsByShow_Id(showId)) {
            throw new BusinessException("Show is not assigned");
        }

        return showSeatRepository.countAvailableSeats(showId);
    }

    @Override
    public void lockSeats(Long showId, List<Long> seatIds, Long userId) {

        validateActiveShow(showId);

        for (Long seatId : seatIds) {
            ShowSeat seat = showSeatRepository
                    .findByShowIdAndSeatId(showId, seatId)
                    .orElseThrow(() -> new ResourceNotFoundException("Seat not found with id: " + seatId));

            if (seat.getStatus() == ShowSeatStatus.BOOKED) {
                throw new SeatAlreadyBookedException("Seat " + seat.getSeat().getSeatNumber() + " is already booked");
            }


            if (seat.getStatus() == ShowSeatStatus.LOCKED) {

                if (!userId.equals(seat.getLockedByUserId())) {
                    throw new SeatNotAvailableException(
                            "Seat " + seat.getSeat().getSeatRow()
                                    + seat.getSeat().getSeatNumber()
                                    + " is locked by another user"
                    );
                }
                continue;
            }

            seat.setStatus(ShowSeatStatus.LOCKED);
            seat.setLockedByUserId(userId);
            seat.setLockedAt(LocalDateTime.now());
        }
    }

    @Override
    public void bookSeats(Long showId, List<Long> seatIds, Long userId) {

        validateActiveShow(showId);
        for (Long seatId : seatIds) {

            ShowSeat seat = showSeatRepository
                    .findByShowIdAndSeatId(showId, seatId)
                    .orElseThrow(() -> new ResourceNotFoundException("Seat not found with id: " + seatId));

            if (seat.getStatus() != ShowSeatStatus.LOCKED ||
                    !userId.equals(seat.getLockedByUserId())) {
                throw new UnauthorizedActionException("You are not authorized to book seat " + seat.getSeat().getSeatNumber());
            }

            seat.setStatus(ShowSeatStatus.BOOKED);
            seat.setLockedAt(null);
            seat.setLockedByUserId(null);
        }

    }

    @Override
    public void unlockSeats(Long showId, List<Long> seatIds, Long userId) {

        validateActiveShow(showId);

        List<ShowSeat> seats = new ArrayList<>();

        for (Long seatId : seatIds) {

            ShowSeat showSeat = showSeatRepository
                    .findByShowIdAndSeatId(showId, seatId)
                    .orElseThrow(() -> new ResourceNotFoundException("Seat not found"));

            if (showSeat.getStatus() == ShowSeatStatus.AVAILABLE) {
                throw new BusinessException(
                        "Seat " + showSeat.getSeat().getSeatNumber() + " is already unlocked"
                );
            }

            if (showSeat.getStatus() == ShowSeatStatus.LOCKED &&
                    !userId.equals(showSeat.getLockedByUserId())) {

                throw new SeatNotAvailableException(
                        "Seat " + seatId + " is locked by another user"
                );
            }

            if (showSeat.getStatus() == ShowSeatStatus.BOOKED ||
                    showSeat.getStatus() == ShowSeatStatus.LOCKED) {

                showSeat.setStatus(ShowSeatStatus.AVAILABLE);
                showSeat.setLockedAt(null);
                showSeat.setLockedByUserId(null);
                seats.add(showSeat);
            }
        }

        showSeatRepository.saveAll(seats);

    }

    @Override
    public void releaseExpiredLocks() {

        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(lockDurationMinutes);
        showSeatRepository.releaseExpiredLockedSeats(expiryTime);
    }

    private void validateActiveShow(Long showId) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + showId));

        if (!Boolean.TRUE.equals(show.getActive())) {
            throw new BusinessException("Show is not active. Seat operations are not allowed");
        }

        if (show.getStatus() == ShowStatus.RUNNING) {
            throw new BusinessException("Show is already running. Seat locking is not allowed");
        }


        if (show.getStatus() == ShowStatus.COMPLETED ||
                show.getStatus() == ShowStatus.CANCELLED) {

            throw new BusinessException("Show is closed. Seat locking is not allowed");
        }
    }


    private ShowSeatResponse mapToResponse(ShowSeat showSeat){

        ShowSeatResponse response = new ShowSeatResponse();
        response.setShowSeatId(showSeat.getId());
        response.setSeatId(showSeat.getSeat().getId());
        response.setSeatRow(showSeat.getSeat().getSeatRow());
        response.setSeatNumber(showSeat.getSeat().getSeatNumber());
        response.setSeatType(showSeat.getSeat().getSeatType());
        response.setPrice(showSeat.getPrice());
        response.setStatus(showSeat.getStatus());

        return  response;
    }
}
