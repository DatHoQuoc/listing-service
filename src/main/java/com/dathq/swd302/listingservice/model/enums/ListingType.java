package com.dathq.swd302.listingservice.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ListingType {
    SALE, RENT, LEASE;

    @JsonCreator
    public static ListingType fromString(String value) {
        return value == null ? null : ListingType.valueOf(value.toUpperCase());
    }
}
