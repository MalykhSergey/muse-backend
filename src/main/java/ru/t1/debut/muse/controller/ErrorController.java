package ru.t1.debut.muse.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.t1.debut.muse.exception.ResourceNotFoundException;

import java.util.Locale;

@RestControllerAdvice
public class ErrorController {
    private final MessageSource messageSource;

    @Autowired
    public ErrorController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDetails handleUserNotFound(ResourceNotFoundException ex) {
        String message = messageSource.getMessage("error.not_found", null, Locale.getDefault());
        return new ErrorDetails(HttpStatus.NOT_FOUND.value(), message, null);
    }
}

