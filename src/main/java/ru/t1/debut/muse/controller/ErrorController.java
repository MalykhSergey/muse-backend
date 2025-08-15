package ru.t1.debut.muse.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.t1.debut.muse.exception.ResourceNotFoundException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
public class ErrorController {
    private final MessageSource messageSource;

    @Autowired
    public ErrorController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDetails<?> handleUserNotFound(ResourceNotFoundException ex) {
        String message = messageSource.getMessage("error.not_found", null, Locale.getDefault());
        return new ErrorDetails<>(message, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorDetails<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        String message = messageSource.getMessage("error.invalid_argument", null, Locale.getDefault());
        return new ErrorDetails<>(message, errors);
    }
}

