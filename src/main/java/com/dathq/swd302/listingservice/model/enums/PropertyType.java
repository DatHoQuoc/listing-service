package com.dathq.swd302.listingservice.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PropertyType {
    APARTMENT,
    HOUSE,
    VILLA,
    LAND,
    COMMERCIAL;

    @JsonCreator
    public static PropertyType fromString(String value) {
        if (value == null) return null;
        try {
            return PropertyType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Optional: Handle invalid strings gracefully or let it throw for 400 Bad Request
            throw new IllegalArgumentException("Unknown property type: " + value);
        }
    }
}
