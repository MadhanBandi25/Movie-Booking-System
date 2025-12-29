package com.booking.movieticket.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException exception, HttpServletRequest request){
        return buildError(HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException exception,HttpServletRequest request){
        return buildError(HttpStatus.CONFLICT, exception.getMessage(), request);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(DuplicateResourceException exception, HttpServletRequest request){
        return buildError(HttpStatus.CONFLICT,
                exception.getMessage(),
                request);
    }

    @ExceptionHandler(ShowSeatAlreadyInitializedException.class)
    public ResponseEntity<ErrorResponse> handleShowSeatAlready(ShowSeatAlreadyInitializedException exception,HttpServletRequest request){
        return buildError(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    @ExceptionHandler(SeatNotAvailableException.class)
    public ResponseEntity<ErrorResponse> handleSeatNotAvailable(SeatNotAvailableException exception,HttpServletRequest request){
        return buildError(HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request);
    }

    @ExceptionHandler(SeatAlreadyBookedException.class)
    public ResponseEntity<ErrorResponse> handleSeatAlreadyBooked(SeatAlreadyBookedException exception, HttpServletRequest request){

        return buildError(HttpStatus.CONFLICT,
                exception.getMessage(),
                request);
    }

    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<ErrorResponse> handlePaymentFailed(PaymentFailedException exception, HttpServletRequest request){

        String message = exception.getMessage();

        if (message.contains("Invalid booking")) {
            return buildError(HttpStatus.BAD_REQUEST, message, request);
        }

        if (message.contains("already")) {
            return buildError(HttpStatus.CONFLICT, message, request);
        }

        if (message.contains("not eligible")) {
            return buildError(HttpStatus.CONFLICT, message, request);
        }

        // Real payment failure (card / UPI declined)
        return buildError(HttpStatus.PAYMENT_REQUIRED, message, request);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequest(InvalidRequestException exception, HttpServletRequest request){
        return buildError(HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request);
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedActionException exception,HttpServletRequest request){
        return buildError(HttpStatus.FORBIDDEN,
                exception.getMessage(),
                request);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleErrors(Exception ex, HttpServletRequest request){
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error", request);
    }

    @ExceptionHandler(InvalidBookingException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBooking(InvalidBookingException exception,HttpServletRequest request){
        return buildError(HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(MethodArgumentNotValidException ex, HttpServletRequest request){
        Map<String ,String > errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach( err->
                        errors.put(err.getField(),err.getDefaultMessage()));

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Error")
                .message("Invalid request data")
                .path(request.getRequestURI())
                .validationErrors(errors)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }



    // common method
    private ResponseEntity<ErrorResponse> buildError(HttpStatus status,String message, HttpServletRequest request){
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(response,status);
    }
}
