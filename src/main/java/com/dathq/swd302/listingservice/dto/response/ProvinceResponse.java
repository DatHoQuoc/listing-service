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
public class ProvinceResponse {
    private UUID provinceId;
    private String name;
    private UUID countryId;
    private String code;
    private OffsetDateTime createdAt;
}
