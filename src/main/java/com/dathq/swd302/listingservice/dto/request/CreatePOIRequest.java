package com.dathq.swd302.listingservice.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePOIRequest {
    @NotBlank(message = "POI name is required")
    private String name;

    @NotBlank(message = "POI category is required")
    private String category;

    private Integer distanceMeters;

    private Double latitude;

    private Double longitude;
}
