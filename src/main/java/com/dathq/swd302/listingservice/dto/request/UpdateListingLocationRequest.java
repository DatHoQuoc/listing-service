package com.dathq.swd302.listingservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateListingLocationRequest {
    private UUID wardId;
    private String streetAddress;
    private Double latitude;
    private Double longitude;
}
