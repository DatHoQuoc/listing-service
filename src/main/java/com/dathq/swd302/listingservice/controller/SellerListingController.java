package com.dathq.swd302.listingservice.controller;


import com.dathq.swd302.listingservice.dto.request.CreateListingRequest;
import com.dathq.swd302.listingservice.dto.response.ListingResponse;
import com.dathq.swd302.listingservice.service.ListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/seller/listings")
@RequiredArgsConstructor
public class SellerListingController {
    private final ListingService listingService;

    @PostMapping
    public ResponseEntity<ListingResponse> createDraftListing(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody CreateListingRequest request) {

        ListingResponse response = listingService.createDraft(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getMyListings(@RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok("Not implemented yet");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getListingDetails(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id) {
        return ResponseEntity.ok("Not implemented yet");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateListingDetails(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id,
            @RequestBody Object request) {
        return ResponseEntity.ok("Not implemented yet");
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submitForReview(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id) {
        return ResponseEntity.ok("Not implemented yet");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDraft(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id) {
        return ResponseEntity.noContent().build();
    }
}
