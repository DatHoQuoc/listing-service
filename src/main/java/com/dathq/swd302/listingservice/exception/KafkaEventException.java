package com.dathq.swd302.listingservice.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class KafkaEventException extends RuntimeException {
    public KafkaEventException(String message) {
        super(message);
    }

    public KafkaEventException(String message, Throwable cause) {
        super(message, cause);
    }
}
