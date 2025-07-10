package com.ascend.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String message = error.getDefaultMessage();
            
            // Provide more specific error messages
            if ("discipline".equals(fieldName)) {
                errors.put("message", "Invalid discipline. Supported disciplines: BOULDER, LEAD, TOP_ROPE");
            } else if ("grade".equals(fieldName)) {
                errors.put("message", "Invalid grade format. Use V-scale for bouldering (V0-V17) or YDS for lead/top rope (5.6-5.15d)");
            } else if ("gradeCompatibleWithDiscipline".equals(fieldName)) {
                errors.put("message", message);
            } else {
                errors.put("message", fieldName + ": " + message);
            }
        });
        
        // For global errors (like @AssertTrue), use only the message (do not prefix with field name)
        ex.getBindingResult().getGlobalErrors().forEach(error -> {
            errors.put("message", error.getDefaultMessage());
        });
        
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNoSuchElementException(NoSuchElementException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Map<String, String>> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", "Authorization header is required");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Map<String, String> error = new HashMap<>();
        String message = ex.getMessage();
        if (message != null && message.contains("SessionDiscipline")) {
            error.put("message", "Invalid discipline. Supported disciplines: BOULDER, LEAD, TOP_ROPE");
        } else if (message != null && message.contains("Grade")) {
            error.put("message", "Invalid grade format. Use V-scale for bouldering (V0-V17) or YDS for lead/top rope (5.6-5.15d)");
        } else {
            error.put("message", "Invalid request body format. Please check your JSON format and ensure all required fields are present");
        }
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        
        if (ex.getMessage().contains("Invalid token")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } else if (ex.getMessage().contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } else if (ex.getMessage().contains("Unauthorized")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", "An unexpected error occurred: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
} 