package com.dathq.swd302.listingservice.dto.response;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryResponse {
    private UUID countryId;
    private String name;
    private String code;
    private OffsetDateTime createdAt;
}
