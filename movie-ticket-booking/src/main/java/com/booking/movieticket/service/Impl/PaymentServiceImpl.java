package com.booking.movieticket.service.Impl;

import com.booking.movieticket.dto.request.PaymentRequest;
import com.booking.movieticket.dto.response.PaymentResponse;
import com.booking.movieticket.entity.*;
import com.booking.movieticket.entity.enums.BookingStatus;
import com.booking.movieticket.entity.enums.PaymentStatus;
import com.booking.movieticket.entity.enums.ShowSeatStatus;
import com.booking.movieticket.exception.PaymentFailedException;
import com.booking.movieticket.repository.BookingRepository;
import com.booking.movieticket.repository.BookingSeatRepository;
import com.booking.movieticket.repository.PaymentRepository;
import com.booking.movieticket.repository.ShowSeatRepository;
import com.booking.movieticket.service.NotificationService;
import com.booking.movieticket.service.PaymentService;
import com.booking.movieticket.service.ShowSeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {


    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private BookingSeatRepository bookingSeatRepository;

    @Autowired
    private ShowSeatService showSeatService;

    @Autowired
    private NotificationService notificationService;

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {

        Booking booking= bookingRepository.findByBookingNumber(request.getBookingNumber())
                .orElseThrow(()-> new PaymentFailedException("Invalid booking number"));



        if (paymentRepository.existsByBooking_Id(booking.getId())) {
            throw new PaymentFailedException("Payment already attempted");
        }

        List<ShowSeat> showSeats = bookingSeatRepository
                .findByBooking_Id(booking.getId())
                .stream()
                .map(BookingSeat::getShowSeat)
                .toList();

        if (showSeats.isEmpty()) {
            throw new PaymentFailedException("Seat lock expired");
        }

        boolean invalidSeat = showSeats.stream()
                .anyMatch(ss -> ss.getStatus() != ShowSeatStatus.LOCKED ||
                        !booking.getUser().getId().equals(ss.getLockedByUserId()));
        if (invalidSeat) {
            throw new PaymentFailedException("Seat lock expired");
        }



        boolean paymentSuccess = simulatePaymentGateway();

        Payment payment = new Payment();
        payment.setTransactionId(generateTransactionId());
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(paymentSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);

        paymentRepository.save(payment);

        List<Long> seatIds = showSeats.stream()
                .map(ss -> ss.getSeat().getId())
                .toList();

        List<String> seatNumbers = showSeats.stream()
                .map(s -> s.getSeat().getSeatRow() + s.getSeat().getSeatNumber())
                .toList();


        if (paymentSuccess) {
            showSeatService.bookSeats(
                    booking.getShow().getId(),
                    seatIds,
                    booking.getUser().getId()
            );
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            notificationService.sendBookingConfirmation(booking, seatNumbers);

        } else {
            showSeatService.unlockSeats(
                    booking.getShow().getId(),
                    seatIds,
                    booking.getUser().getId()
            );

            booking.setStatus(BookingStatus.FAILED);
            bookingRepository.save(booking);

            notificationService.sendPaymentFailure(booking);
        }

        return buildPaymentResponse(payment, booking, showSeats);

    }

    @Override
    public void cancelBooking(String bookingNumber) {

        Booking booking = bookingRepository.findByBookingNumber(bookingNumber)
                .orElseThrow(() -> new PaymentFailedException("Invalid booking number"));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new PaymentFailedException("Booking already cancelled");
        }
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new PaymentFailedException("Only confirmed bookings can be cancelled");
        }
        List<ShowSeat> showSeats = bookingSeatRepository
                .findByBooking_Id(booking.getId())
                .stream()
                .map(BookingSeat::getShowSeat)
                .toList();

        List<Long> seatIds = showSeats.stream()
                .map(ss -> ss.getSeat().getId())
                .toList();

        List<String> seatNumbers = showSeats.stream()
                .map(s -> s.getSeat().getSeatRow() + s.getSeat().getSeatNumber())
                .toList();

        showSeatService.unlockSeats(
                booking.getShow().getId(),
                seatIds,
                booking.getUser().getId()
        );


        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        notificationService.sendBookingCancellation(booking,seatNumbers);
    }


    private boolean simulatePaymentGateway(){
        return Math.random() < 0.8;   // 80 % success and 20% failure
    }

    private String generateTransactionId(){
        return "TXN-"+ UUID.randomUUID().toString().substring(0,12).toUpperCase();
    }

    private PaymentResponse buildPaymentResponse(
            Payment payment,
            Booking booking,
            List<ShowSeat> showSeats) {

        List<String> seatNumbers = showSeats.stream()
                .map(s -> s.getSeat().getSeatRow() + s.getSeat().getSeatNumber())
                .toList();

        PaymentResponse response = new PaymentResponse();
        response.setBookingNumber(booking.getBookingNumber());
        response.setTransactionId(payment.getTransactionId());
        response.setAmount(payment.getAmount());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setPaymentStatus(payment.getStatus());
        response.setCreatedAt(payment.getCreatedAt());

        response.setTheatreName(
                booking.getShow().getScreen().getTheatre().getName()
        );
        response.setScreenName(
                booking.getShow().getScreen().getName()
        );
        response.setSeats(seatNumbers);
        response.setShowDate(booking.getShow().getShowDate());
        response.setShowTime(booking.getShow().getShowTime());

        return response;
    }
} 