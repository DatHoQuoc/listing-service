package com.dathq.swd302.listingservice.exception;

public class InvalidListingStateException extends RuntimeException {
    public InvalidListingStateException(String message) {
        super(message);
    }
}
