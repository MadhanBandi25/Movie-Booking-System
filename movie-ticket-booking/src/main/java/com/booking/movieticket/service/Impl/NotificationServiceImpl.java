package com.booking.movieticket.service.Impl;

import com.booking.movieticket.entity.Booking;
import com.booking.movieticket.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final EmailService emailService;

    @Override
    public void sendBookingConfirmation(Booking booking, List<String> seats) {

        String email = booking.getUser().getEmail();

        String subject = "Ticket Confirmed - " + booking.getBookingNumber();


        String emailBody =

                        "Hello " + booking.getUser().getName() + " 👋,\n\n" +
                        "Your movie ticket has been CONFIRMED!\n\n" +
                        "🎟 Booking Number : " + booking.getBookingNumber() + "\n" +
                        "🎬 Movie          : " + booking.getShow().getMovie().getTitle() + "\n" +
                        "🏢 Theatre        : " + booking.getShow().getScreen().getTheatre().getName() + "\n" +
                        "🖥 Screen         : " + booking.getShow().getScreen().getName() + "\n" +
                        "📅 Date & Time    : " + booking.getShow().getShowDate() + ", " +
                        booking.getShow().getShowTime() + "\n" +
                        "💺 Seats          : " + String.join(", ", seats) + "\n\n" +
                        "Please arrive 15 minutes early.\n" +
                        "Show this email at the theatre entrance.\n\n" +
                        "Enjoy your movie 🍿🎉\n\n" +
                        "— Movie Ticket Booking Team";


        if (email != null) {
            emailService.sendEmail(email, subject, emailBody);
        }
    }

    @Override
    public void sendBookingCancellation(Booking booking, List<String> seats) {

        String email = booking.getUser().getEmail();
        String subject = "Ticket Cancelled - " + booking.getBookingNumber();

        String emailBody =

                        "BOOKING CANCELLED\n" +

                        "Hello " + booking.getUser().getName() + " 👋,\n\n" +
                        "Your movie ticket has been CANCELLED successfully.\n\n" +
                        "🎟 Booking Number : " + booking.getBookingNumber() + "\n" +
                        "🎬 Movie          : " + booking.getShow().getMovie().getTitle() + "\n" +
                        "🏢 Theatre        : " + booking.getShow().getScreen().getTheatre().getName() + "\n" +
                        "🖥 Screen         : " + booking.getShow().getScreen().getName() + "\n" +
                        "📅 Date & Time    : " + booking.getShow().getShowDate() + ", " +
                        booking.getShow().getShowTime() + "\n" +
                        "💺 Seats          : " + String.join(", ", seats) + "\n\n" +
                        "💰 Refund Information:\n" +
                        "If applicable, your refund will be processed automatically\n" +
                        "to your original payment method within 3–5 business days.\n\n" +
                        "If this cancellation was unintentional,\n" +
                        "you can book again anytime.\n\n" +
                        "— Movie Ticket Booking Team";

        String smsMessage =
                "Ticket CANCELLED \n" +
                        "Booking: " + booking.getBookingNumber() + "\n" +
                        "Refund will be processed if applicable.";

        if (email != null) {
            emailService.sendEmail(email, subject, emailBody);
        }
    }

    @Override
    public void sendPaymentFailure(Booking booking) {

        String email = booking.getUser().getEmail();

        String subject = "Payment Failed - " + booking.getBookingNumber();

        String body =
                "Hello " + booking.getUser().getName() + " 👋,\n\n" +
                        "Unfortunately, your payment has FAILED.\n\n" +
                        "Booking Number : " + booking.getBookingNumber() + "\n" +
                        "Movie          : " + booking.getShow().getMovie().getTitle() + "\n\n" +
                        "Any locked seats have been released.\n" +
                        "Please try booking again.\n\n" +
                        "— Movie Ticket Booking Team";



        if (email != null) {
            emailService.sendEmail(email, subject, body);
        }

    }

}
