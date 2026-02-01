package com.dathq.swd302.listingservice.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class POIResponse {
    private UUID poiId;
    private UUID listingId;
    private String name;
    private String category;
    private Integer distanceMeters;
    private Double latitude;
    private Double longitude;
    private OffsetDateTime createdAt;
}
