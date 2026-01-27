package com.dathq.swd302.listingservice.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class VirtualTourNotFoundException extends RuntimeException {
    public VirtualTourNotFoundException(String message) {
        super(message);
    }
}
