package com.dathq.swd302.listingservice.controller;
import com.dathq.swd302.listingservice.dto.response.AmenityResponse;
import com.dathq.swd302.listingservice.model.enums.AmenityCategory;
import com.dathq.swd302.listingservice.service.AmenityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/amenities")
@RequiredArgsConstructor
public class AmenityController {

    private final AmenityService amenityService;

    @GetMapping
    public ResponseEntity<List<AmenityResponse>> getAllAmenities() {
        List<AmenityResponse> amenities = amenityService.getAllAmenities();
        return ResponseEntity.ok(amenities);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<AmenityResponse>> getAmenitiesByCategory(
            @PathVariable AmenityCategory category) {
        List<AmenityResponse> amenities = amenityService.getAmenitiesByCategory(category);
        return ResponseEntity.ok(amenities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AmenityResponse> getAmenityById(@PathVariable UUID id) {
        AmenityResponse amenity = amenityService.getAmenityById(id);
        return ResponseEntity.ok(amenity);
    }

    @GetMapping("/search")
    public ResponseEntity<List<AmenityResponse>> searchAmenities(
            @RequestParam String keyword) {
        List<AmenityResponse> amenities = amenityService.searchAmenities(keyword);
        return ResponseEntity.ok(amenities);
    }

    @GetMapping("/listing/{listingId}")
    public ResponseEntity<List<AmenityResponse>> getListingAmenities(
            @PathVariable UUID listingId) {
        List<AmenityResponse> amenities = amenityService.getListingAmenities(listingId);
        return ResponseEntity.ok(amenities);
    }
}
