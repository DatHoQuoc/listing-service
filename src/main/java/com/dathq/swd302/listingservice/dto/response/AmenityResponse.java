package com.dathq.swd302.listingservice.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmenityResponse {
    private UUID amenityId;
    private String amenityName;
    private String amenityCategory;
    private String iconUrl;
}
