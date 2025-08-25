package ru.t1.debut.muse.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
    public ErrorDetails<?> handleUserNotFound(ResourceNotFoundException ex, Locale locale) {
        String message = messageSource.getMessage("error.not_found", null, locale);
        return new ErrorDetails<>(message, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorDetails<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, Locale locale) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        String message = messageSource.getMessage("error.invalid_argument", null, locale);
        return new ErrorDetails<>(message, errors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDetails<?> handleDataIntegrityViolation(DataIntegrityViolationException ex, Locale locale) {
        String message = messageSource.getMessage("error.data_integrity", null, locale);
        return new ErrorDetails<>(message, null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDetails<?> handleJsonParseError(HttpMessageNotReadableException ex, Locale locale) {
        String message = messageSource.getMessage("error.invalid_json", null, locale);
        return new ErrorDetails<>(message, null);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDetails<?> handleAllOtherExceptions(Exception ex, Locale locale) {
        String message = messageSource.getMessage("error.internal_server", null, locale);
        return new ErrorDetails<>(message, null);
    }

}

