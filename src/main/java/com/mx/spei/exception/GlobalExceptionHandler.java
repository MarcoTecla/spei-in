package com.mx.spei.exception;

import com.mx.spei.client.FineractClient.FineractException;
import jakarta.validation.ConstraintViolationException;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> validation(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> Map.of("field", e.getField(), "message", e.getDefaultMessage()))
                .toList();
        return ResponseEntity.badRequest().body(error(400, "Body inválido", errors));
    }

    @ExceptionHandler(AccountValidationException.class)
    ResponseEntity<?> account(AccountValidationException ex) {
        return ResponseEntity.unprocessableEntity().body(error(422, ex.getMessage(), null));
    }

    @ExceptionHandler(FineractException.class)
    ResponseEntity<?> fineract(FineractException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(error(502, ex.getMessage(), null));
    }

    @ExceptionHandler(NoSuchElementException.class)
    ResponseEntity<?> notFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error(404, ex.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<?> generic(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error(500, "Error interno", null));
    }

    private Map<String, Object> error(int status, String message, Object details) {
        return Map.of("timestamp", OffsetDateTime.now(), "status", status, "message", message,
                "details", details == null ? Map.of() : details);
    }
}
