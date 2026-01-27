package com.dathq.swd302.listingservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadResponse {
    private String url;
    private Integer order;
    private String caption;
    private OffsetDateTime uploadedAt;
}
