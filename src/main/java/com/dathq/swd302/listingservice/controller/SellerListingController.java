package com.dathq.swd302.listingservice.controller;


import com.dathq.swd302.listingservice.dto.request.CreateListingRequest;
import com.dathq.swd302.listingservice.dto.request.UpdateListingRequest;
import com.dathq.swd302.listingservice.dto.response.ListingDetailResponse;
import com.dathq.swd302.listingservice.dto.response.ListingResponse;
import com.dathq.swd302.listingservice.model.enums.ListingStatus;
import com.dathq.swd302.listingservice.service.ListingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/seller/listings")
@RequiredArgsConstructor
@Tag(name = "Seller", description = "API for seller create listing")
public class SellerListingController {
    private final ListingService listingService;

    // --- 1. Create & Update Drafts ---

    @PostMapping
    @Operation(summary = "Create draft", description = "")
    public ResponseEntity<ListingResponse> createDraftListing(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody CreateListingRequest request) {

        ListingResponse response = listingService.createDraft(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ListingResponse> updateListingDetails(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateListingRequest request) { // Fixed: Object -> UpdateListingRequest

        return ResponseEntity.ok(listingService.updateListing(userId, id, request));
    }

    // --- 2. Retrieve Listings ---

    @GetMapping
    public ResponseEntity<Page<ListingResponse>> getMyListings(
            @RequestHeader("X-User-Id") UUID userId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(listingService.getMyListings(userId, pageable));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ListingResponse>> getMyListingsByStatus(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable ListingStatus status) {

        return ResponseEntity.ok(listingService.getMyListingsByStatus(userId, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListingDetailResponse> getListingDetails(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id) {

        return ResponseEntity.ok(listingService.getListingById(userId, id));
    }

    // --- 3. Submission Workflow ---

    @PutMapping("/{id}/submit") // Changed to PUT as it updates status
    public ResponseEntity<ListingResponse> submitForReview(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id) {

        return ResponseEntity.ok(listingService.submitListing(userId, id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ListingResponse> cancelSubmission(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id) {

        return ResponseEntity.ok(listingService.cancelSubmission(userId, id));
    }

    // --- 4. Sub-resource Updates (Amenities & Location) ---

    @PutMapping("/{id}/amenities")
    public ResponseEntity<Void> updateListingAmenities(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id,
            @RequestBody List<UUID> amenityIds) {

        listingService.updateListingAmenities(userId, id, amenityIds);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/location")
    public ResponseEntity<Void> updateListingLocation(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id,
            @RequestParam(required = false) UUID wardId,
            @RequestParam(required = false) String streetAddress,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude) {

        listingService.updateListingLocation(userId, id, wardId, streetAddress, latitude, longitude);
        return ResponseEntity.ok().build();
    }

    // --- 5. Delete ---

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDraft(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id) {

        listingService.deleteListing(userId, id);
        return ResponseEntity.noContent().build();
    }
}
