package me.dodowhat.example.config.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestControllerAdvice
public class RESTResponseExceptionHandler {
    private ExceptionResponseDTO createResponseDTO(String message, String path) {
        ExceptionResponseDTO responseDTO = new ExceptionResponseDTO();
        responseDTO.setTimestamp(new Date());
        responseDTO.setPath(path);
        responseDTO.setMessage(message);
        return responseDTO;
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ExceptionResponseDTO> actionNotAllowed(Exception e, HttpServletRequest request) {
        ExceptionResponseDTO responseBody = createResponseDTO(e.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponseDTO> entityNotFound(Exception e, HttpServletRequest request) {
        ExceptionResponseDTO responseBody = createResponseDTO(e.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnprocessableEntityException.class)
    public ResponseEntity<ExceptionResponseDTO> unprocessableEntity(Exception e, HttpServletRequest request) {
        ExceptionResponseDTO responseBody = createResponseDTO(e.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(responseBody, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponseDTO> badRequest(Exception e, HttpServletRequest request) {
        ExceptionResponseDTO responseBody = createResponseDTO(e.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

}
