package com.dathq.swd302.listingservice.controller;
import com.dathq.swd302.listingservice.dto.request.CreatePOIRequest;
import com.dathq.swd302.listingservice.dto.response.POIResponse;
import com.dathq.swd302.listingservice.service.PointOfInterestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/seller/listings/{listingId}/pois")
@RequiredArgsConstructor
public class PointOfInterestController {
    private final PointOfInterestService poiService;

    @PostMapping
    public ResponseEntity<POIResponse> addPointOfInterest(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID listingId,
            @Valid @RequestBody CreatePOIRequest request) {
        POIResponse response = poiService.addPointOfInterest(userId, listingId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<POIResponse>> addPointsOfInterest(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID listingId,
            @Valid @RequestBody List<CreatePOIRequest> requests) {
        List<POIResponse> responses = poiService.addPointsOfInterest(userId, listingId, requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @GetMapping
    public ResponseEntity<List<POIResponse>> getListingPOIs(@PathVariable UUID listingId) {
        List<POIResponse> pois = poiService.getListingPOIs(listingId);
        return ResponseEntity.ok(pois);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<POIResponse>> getListingPOIsByCategory(
            @PathVariable UUID listingId,
            @PathVariable String category) {
        List<POIResponse> pois = poiService.getListingPOIsByCategory(listingId, category);
        return ResponseEntity.ok(pois);
    }

    @PutMapping("/{poiId}")
    public ResponseEntity<POIResponse> updatePointOfInterest(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID listingId,
            @PathVariable UUID poiId,
            @Valid @RequestBody CreatePOIRequest request) {
        POIResponse response = poiService.updatePointOfInterest(userId, listingId, poiId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{poiId}")
    public ResponseEntity<Void> deletePointOfInterest(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID listingId,
            @PathVariable UUID poiId) {
        poiService.deletePointOfInterest(userId, listingId, poiId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllPOIs(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID listingId) {
        poiService.deleteAllPOIs(userId, listingId);
        return ResponseEntity.noContent().build();
    }
}
