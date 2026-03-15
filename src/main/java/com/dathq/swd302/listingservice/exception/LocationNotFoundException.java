package com.dathq.swd302.listingservice.exception;

public class LocationNotFoundException extends RuntimeException {
    public LocationNotFoundException(String msg) {
        super(msg);
    }
}
