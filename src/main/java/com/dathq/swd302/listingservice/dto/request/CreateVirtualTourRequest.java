package com.dathq.swd302.listingservice.dto.request;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateVirtualTourRequest {
    @Size(max = 255, message = "Tour URL must not exceed 255 characters")
    private String tourUrl; // Optional: External URL if the tour is hosted elsewhere

    @Size(max = 100, message = "Tour provider name must not exceed 100 characters")
    private String tourProvider; // e.g., "Matterport", "Kuula", or "Internal"
}
