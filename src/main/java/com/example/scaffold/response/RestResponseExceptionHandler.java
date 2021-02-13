package com.example.scaffold.response;

import com.example.scaffold.exceptions.ActionNotAllowedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestControllerAdvice
public class RestResponseExceptionHandler {
    private ExceptionResponseBody buildResponseBody(String message, String path) {
        ExceptionResponseBody response = new ExceptionResponseBody();
        response.setTimestamp(new Date());
        response.setPath(path);
        response.setMessage(message);
        return response;
    }

    @ExceptionHandler(ActionNotAllowedException.class)
    public ResponseEntity<ExceptionResponseBody> actionNotAllowed(ActionNotAllowedException e, HttpServletRequest request) {
        ExceptionResponseBody responseBody = buildResponseBody(e.getMessage(), request.getRequestURI());
        return new ResponseEntity<ExceptionResponseBody>(responseBody, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponseBody> entityNotFound(EntityNotFoundException e, HttpServletRequest request) {
        ExceptionResponseBody responseBody = buildResponseBody(e.getMessage(), request.getRequestURI());
        return new ResponseEntity<ExceptionResponseBody>(responseBody, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponseBody> methodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        FieldError fieldError = (FieldError) e.getAllErrors().get(0);
        String message = fieldError.getField() + " " + fieldError.getDefaultMessage();
        ExceptionResponseBody responseBody = buildResponseBody(message, request.getRequestURI());
        return new ResponseEntity<ExceptionResponseBody>(responseBody, HttpStatus.BAD_REQUEST);
    }
}
