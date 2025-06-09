package io.github.configservice.config_service.exception.handler;

import io.github.configservice.config_service.exception.ConfigNotFoundException;
import io.github.configservice.config_service.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ConfigNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(ConfigNotFoundException ex, HttpServletRequest request){
        log.warn("Resource not found: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request){
        log.error("Internal error: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex){
        StringBuilder message = new StringBuilder("Validation error in fields: ");

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()){
            message.append(String.format("[%s: %s]", fieldError.getField(), fieldError.getDefaultMessage()));
        }

        log.warn("Validation error: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildErrorResponse(message.toString(), HttpStatus.BAD_REQUEST));

    }

    public ErrorResponse buildErrorResponse(String message, HttpStatus status) {
        return new ErrorResponse(
                message,
                status.getReasonPhrase(),
                status.value(),
                MDC.get("traceId"),
                LocalDateTime.now()
        );
    }

}
