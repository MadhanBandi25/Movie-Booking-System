package com.booking.movieticket.service.Impl;

import com.booking.movieticket.dto.request.BookingRequest;
import com.booking.movieticket.dto.response.BookingResponse;
import com.booking.movieticket.entity.*;
import com.booking.movieticket.entity.enums.BookingStatus;
import com.booking.movieticket.entity.enums.ShowSeatStatus;
import com.booking.movieticket.exception.InvalidBookingException;
import com.booking.movieticket.exception.ResourceNotFoundException;
import com.booking.movieticket.repository.*;
import com.booking.movieticket.service.BookingService;
import com.booking.movieticket.service.ShowSeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private BookingSeatRepository bookingSeatRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ShowRepository showRepository;
    @Autowired
    private ShowSeatRepository showSeatRepository;
    @Autowired
    private ShowSeatService showSeatService;

    @Override
    public BookingResponse createBooking(BookingRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));


        BigDecimal totalAmount = BigDecimal.ZERO;
        List<ShowSeat> showSeats = new ArrayList<>();

        for (Long seatId : request.getSeatIds()) {
            ShowSeat seat = showSeatRepository
                    .findByShowIdAndSeatId(show.getId(), seatId)
                    .orElseThrow(() -> new ResourceNotFoundException("Seat not found"));

            if (seat.getStatus() == ShowSeatStatus.BOOKED) {
                throw new InvalidBookingException(
                        "Seat " + seat.getSeat().getSeatNumber() + " already booked");
            }
            if (!request.getUserId().equals(seat.getLockedByUserId())) {
                throw new InvalidBookingException(
                        "Seat " + seat.getSeat().getSeatNumber()
                                + " is locked by another user"
                );
            }

            showSeats.add(seat);
            totalAmount = totalAmount.add(seat.getPrice());

        }

        Booking booking = new Booking();
        booking.setBookingNumber(generateBookingNumber());
        booking.setUser(user);
        booking.setShow(show);
        booking.setTotalSeats(showSeats.size());
        booking.setTotalAmount(totalAmount);
        booking.setStatus(BookingStatus.PENDING);

        bookingRepository.save(booking);

        for (ShowSeat seat : showSeats) {
            BookingSeat bs = new BookingSeat();
            bs.setBooking(booking);
            bs.setShowSeat(seat);
            bs.setPrice(seat.getPrice());
            bookingSeatRepository.save(bs);
        }

        return mapToResponse(booking, showSeats);

    }

    @Override
    public BookingResponse confirmBooking(String bookingNumber) {
        Booking booking = getBookingEntity(bookingNumber);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new InvalidBookingException("Booking cannot be confirmed");
        }


        List<ShowSeat> showSeats = bookingSeatRepository.
                findByBooking_Id(booking.getId())
                .stream()
                .map(BookingSeat::getShowSeat)
                .toList();

        List<Long> seatIds = showSeats.stream()
                .map(s -> s.getSeat().getId())
                .toList();

        showSeatService.bookSeats(
                booking.getShow().getId(),
                seatIds,
                booking.getUser().getId()
        );

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        return mapToResponse(booking, showSeats);
    }

    @Override
    public BookingResponse cancelBooking(String bookingNumber) {
        Booking booking = getBookingEntity(bookingNumber);

        if (booking.getStatus() == BookingStatus.CANCELLED ||
                booking.getStatus() == BookingStatus.FAILED) {

            throw new InvalidBookingException("Booking already closed");
        }

        List<ShowSeat> showSeats = bookingSeatRepository
                .findByBooking_Id(booking.getId())
                .stream()
                .map(BookingSeat::getShowSeat)
                .toList();

        List<Long> seatIds = showSeats.stream()
                .map(s -> s.getSeat().getId())
                .toList();

        showSeatService.unlockSeats(
                booking.getShow().getId(),
                seatIds,
                booking.getUser().getId()
        );

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        return mapToResponse(booking, showSeats);
    }

    @Override
    public BookingResponse failedBooking(String bookingNumber) {
        Booking booking = getBookingEntity(bookingNumber);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new InvalidBookingException("Only pending bookings can be failed");
        }
        List<ShowSeat> showSeats = bookingSeatRepository
                .findByBooking_Id(booking.getId())
                .stream()
                .map(BookingSeat::getShowSeat)
                .toList();

        List<Long> seatIds = showSeats.stream()
                .map(s -> s.getSeat().getId())
                .toList();

        showSeatService.unlockSeats(
                booking.getShow().getId(),
                seatIds,
                booking.getUser().getId()
        );

        booking.setStatus(BookingStatus.FAILED);
        bookingRepository.save(booking);

        return mapToResponse(booking, showSeats);
    }

    private Booking getBookingEntity(String bookingNumber) {
        return bookingRepository.findByBookingNumber(bookingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    private String generateBookingNumber() {
        return "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }


    private BookingResponse mapToResponse(Booking booking, List<ShowSeat> seats){

        List<String> seatLabels = seats.stream()
                .map(s-> s.getSeat().getSeatRow() + s.getSeat().getSeatNumber())
                .toList();

        return new BookingResponse(
                booking.getId(),
                booking.getBookingNumber(),
                booking.getStatus(),
                booking.getTotalSeats(),
                booking.getTotalAmount(),
                seatLabels,
                booking.getCreatedAt()
        );
    }
}