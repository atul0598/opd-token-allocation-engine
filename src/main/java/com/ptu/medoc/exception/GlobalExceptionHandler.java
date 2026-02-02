package com.ptu.medoc.exception;
import com.ptu.medoc.dto.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TokenGenerationException.class)
    public ResponseEntity<ErrorResponse> handleTokenException(
            TokenGenerationException ex){

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                ex.getStatus().value(),
                ex.getMessage(),
                ex.getErrorCode().name()
        );

        return new ResponseEntity<>(error, ex.getStatus());
    }

    // Validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> validation(
            MethodArgumentNotValidException ex){

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField()+" : "+err.getDefaultMessage())
                .toList();

        Map<String,Object> body = new HashMap<>();
        body.put("status",400);
        body.put("errors",errors);

        return ResponseEntity.badRequest().body(body);
    }

//    duplicate token exception
    @ExceptionHandler(DuplicateTokenException.class)
    public ResponseEntity<Map<String, String>> handleDuplicate(
            DuplicateTokenException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409
                .body(Map.of(
                        "error", ex.getMessage()
                ));
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrity(
            DataIntegrityViolationException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("error", "Token already booked"));
    }




    // Safety net
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> fallback(Exception ex){

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                500,
                "Unexpected error occurred",
                "INTERNAL_SERVER_ERROR"
        );

        return ResponseEntity.status(500).body(error);
    }

}
