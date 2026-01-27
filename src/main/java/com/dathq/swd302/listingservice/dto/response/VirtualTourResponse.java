package com.dathq.swd302.listingservice.dto.response;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class VirtualTourResponse {
    private UUID tourId;
    private UUID listingId;
    private String tourUrl;
    private String tourProvider;
    private Integer totalScenes;
    private Boolean isPublished;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // Nested list of scenes associated with this tour
    private List<TourSceneResponse> scenes;
}
