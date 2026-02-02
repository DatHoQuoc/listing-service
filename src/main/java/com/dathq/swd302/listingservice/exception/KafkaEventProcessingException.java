package com.dathq.swd302.listingservice.exception;

public class KafkaEventProcessingException extends RuntimeException {
    public KafkaEventProcessingException(String message) {
        super(message);
    }

    public KafkaEventProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
