package com.dathq.swd302.listingservice.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class TourSceneResponse {
    private UUID sceneId;
    private UUID tourId;
    private String sceneName;
    private String panoramaUrl;
    private Integer sceneOrder;
    private Double positionX;
    private Double positionY;
    private Double positionZ;
    private JsonNode hotspotsJson;
    private OffsetDateTime createdAt;
}
