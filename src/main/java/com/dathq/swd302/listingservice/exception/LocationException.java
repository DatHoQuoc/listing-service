package com.dathq.swd302.listingservice.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;

// ── Custom Exceptions ─────────────────────────────────────────────────────────

@RestControllerAdvice
public class LocationException {
    record ApiError(Instant timestamp, int status, String code, String message, String path) {}

    @ExceptionHandler(LocationNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(LocationNotFoundException ex, WebRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(
                Instant.now(), 404, "NOT_FOUND", ex.getMessage(), extractPath(req)
        ));
    }

    @ExceptionHandler(InvalidCoordinateException.class)
    public ResponseEntity<ApiError> handleInvalidCoord(InvalidCoordinateException ex, WebRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(
                Instant.now(), 400, "INVALID_COORDINATE", ex.getMessage(), extractPath(req)
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArg(IllegalArgumentException ex, WebRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(
                Instant.now(), 400, "INVALID_QUERY", ex.getMessage(), extractPath(req)
        ));
    }

    private String extractPath(WebRequest req) {
        return req.getDescription(false).replace("uri=", "");
    }
}
