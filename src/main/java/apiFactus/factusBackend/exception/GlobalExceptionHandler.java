package apiFactus.factusBackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Map<String, Object>> handleDisabledException(DisabledException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("dateTime", LocalDateTime.now());
        response.put("message", ex.getMessage());
        response.put("details", "/api/v1/auth/login");

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("dateTime", LocalDateTime.now());
        response.put("message", "Credenciales incorrectas");
        response.put("details", "/api/v1/auth/login");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
