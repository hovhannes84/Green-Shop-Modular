package com.example.greenshopweb.ExceptionMVC;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorDetails {
    private final LocalDateTime timestamp;
    private final HttpStatus status;
    private final String error;
    private final String message;
    private final String path;
}
