package com.booking.movieticket.exception;

public class ShowSeatAlreadyInitializedException extends RuntimeException {
    public ShowSeatAlreadyInitializedException(String message) {
        super(message);
    }
}
