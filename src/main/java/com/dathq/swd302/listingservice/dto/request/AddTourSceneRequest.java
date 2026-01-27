package com.dathq.swd302.listingservice.dto.request;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddTourSceneRequest {
    @NotBlank(message = "Scene name is required")
    @Size(max = 100, message = "Scene name must not exceed 100 characters")
    private String sceneName;

    // Coordinates for the starting viewing angle or position in the tour web
    @NotNull(message = "Position X is required")
    private Double positionX; // e.g., Pitch

    @NotNull(message = "Position Y is required")
    private Double positionY; // e.g., Yaw

    @NotNull(message = "Position Z is required")
    private Double positionZ; // e.g., Zoom/FOV

    // JSON string storing interactive elements (arrows, info tags)
    // Example: [{"type":"link", "targetSceneId":"...", "x":10, "y":20}]
    private JsonNode hotspotsJson;
}
