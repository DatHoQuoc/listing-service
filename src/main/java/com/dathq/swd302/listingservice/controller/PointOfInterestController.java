package com.dathq.swd302.listingservice.controller;

import com.dathq.swd302.listingservice.dto.request.CreatePOIRequest;
import com.dathq.swd302.listingservice.dto.response.POIResponse;
import com.dathq.swd302.listingservice.security.JwtClaims;
import com.dathq.swd302.listingservice.security.JwtUser;
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
            @JwtUser JwtClaims claims,
            @PathVariable UUID listingId,
            @Valid @RequestBody CreatePOIRequest request) {
        POIResponse response = poiService.addPointOfInterest(claims.getUserId(), listingId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<POIResponse>> addPointsOfInterest(
            @JwtUser JwtClaims claims,
            @PathVariable UUID listingId,
            @Valid @RequestBody List<CreatePOIRequest> requests) {
        List<POIResponse> responses = poiService.addPointsOfInterest(claims.getUserId(), listingId, requests);
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
            @JwtUser JwtClaims claims,
            @PathVariable UUID listingId,
            @PathVariable UUID poiId,
            @Valid @RequestBody CreatePOIRequest request) {
        POIResponse response = poiService.updatePointOfInterest(claims.getUserId(), listingId, poiId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{poiId}")
    public ResponseEntity<Void> deletePointOfInterest(
            @JwtUser JwtClaims claims,
            @PathVariable UUID listingId,
            @PathVariable UUID poiId) {
        poiService.deletePointOfInterest(claims.getUserId(), listingId, poiId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllPOIs(
            @JwtUser JwtClaims claims,
            @PathVariable UUID listingId) {
        poiService.deleteAllPOIs(claims.getUserId(), listingId);
        return ResponseEntity.noContent().build();
    }
}
