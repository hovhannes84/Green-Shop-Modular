package com.example.greenshopweb.ExceptionMVC;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ValidationException;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = {ValidationException.class})
    protected ResponseEntity<ErrorDetails> handleValidationException(ValidationException ex, WebRequest request) {
        LOGGER.error("Validation error occurred: {}", ex.getMessage());
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), HttpStatus.BAD_REQUEST, "Validation Error", ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {RuntimeException.class})
    protected ResponseEntity<ErrorDetails> handleRuntimeException(RuntimeException ex, WebRequest request) {
        LOGGER.error("Internal server error occurred: {}", ex.getMessage());
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetails> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        LOGGER.error("Bad request error occurred: {}", ex.getMessage());
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {
        LOGGER.error("Unexpected error occurred: {}", ex.getMessage());
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

