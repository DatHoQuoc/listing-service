package com.dathq.swd302.listingservice.dto.request;

import lombok.Data;

@Data
public class UpdateCaptionRequest {
    private String imageUrl;
    private String caption;
}
