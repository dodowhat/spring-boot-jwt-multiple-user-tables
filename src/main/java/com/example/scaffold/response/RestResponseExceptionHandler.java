package com.example.scaffold.response;

import com.example.scaffold.exceptions.ActionNotAllowedException;
import com.example.scaffold.exceptions.EntityUnprocessableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {
    private ExceptionResponseBody buildResponseBody(Exception e, HttpServletRequest request) {
        ExceptionResponseBody response = new ExceptionResponseBody();
        response.setTimestamp(new Date());
        response.setMessage(e.getMessage());
        response.setPath(request.getRequestURI());
        return response;
    }
    @ExceptionHandler(ActionNotAllowedException.class)
    public ResponseEntity<ExceptionResponseBody> actionNotAllowed(ActionNotAllowedException e, HttpServletRequest request) {
        ExceptionResponseBody response = buildResponseBody(e, request);
        return new ResponseEntity<ExceptionResponseBody>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponseBody> entityNotFound(EntityNotFoundException e, HttpServletRequest request) {
        ExceptionResponseBody response = buildResponseBody(e, request);
        return new ResponseEntity<ExceptionResponseBody>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityUnprocessableException.class)
    public ResponseEntity<ExceptionResponseBody> entityUnprocessable(EntityUnprocessableException e, HttpServletRequest request) {
        ExceptionResponseBody response = buildResponseBody(e, request);
        return new ResponseEntity<ExceptionResponseBody>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
