package com.dathq.swd302.listingservice.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ListingValidationException extends RuntimeException {
    public ListingValidationException(String message) {
        super(message);
    }
}
