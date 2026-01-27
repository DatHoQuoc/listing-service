package com.dathq.swd302.listingservice.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class VirtualTourAlreadyExistsException extends RuntimeException {
    public VirtualTourAlreadyExistsException(String message) {
        super(message);
    }
}
