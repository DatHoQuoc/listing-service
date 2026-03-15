package com.dathq.swd302.listingservice.controller;

import com.dathq.swd302.listingservice.dto.SellerListingFeedDtos.BulkSubmitResponse;
import com.dathq.swd302.listingservice.dto.SellerListingFeedDtos.NeedsAttentionResponse;
import com.dathq.swd302.listingservice.dto.SellerListingFeedDtos.RecentListingActivityResponse;
import com.dathq.swd302.listingservice.dto.request.BulkSubmitListingsRequest;
import com.dathq.swd302.listingservice.dto.request.CreateListingRequest;
import com.dathq.swd302.listingservice.dto.request.UpdateListingLocationRequest;
import com.dathq.swd302.listingservice.dto.request.UpdateListingRequest;
import com.dathq.swd302.listingservice.dto.response.ListingDetailResponse;
import com.dathq.swd302.listingservice.dto.response.ListingResponse;
import com.dathq.swd302.listingservice.model.enums.ListingStatus;
import com.dathq.swd302.listingservice.security.JwtClaims;
import com.dathq.swd302.listingservice.security.JwtUser;
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
    private final ListingService sellerListingService;

    // --- 1. Create & Update Drafts ---

    @PostMapping("/draft")
    @Operation(summary = "Create draft", description = "")
    public ResponseEntity<ListingResponse> createDraftListing(
            @JwtUser JwtClaims claims,
            @Valid @RequestBody CreateListingRequest request) {

        ListingResponse response = sellerListingService.createDraft(claims.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ListingResponse> updateListingDetails(
            @JwtUser JwtClaims claims,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateListingRequest request) { // Fixed: Object -> UpdateListingRequest

        return ResponseEntity.ok(listingService.updateListing(claims.getUserId(), id, request));
    }

    // --- 2. Retrieve Listings ---

    @GetMapping
    public ResponseEntity<Page<ListingResponse>> getMyListings(
            @JwtUser JwtClaims claims,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(listingService.getMyListings(claims.getUserId(), pageable));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ListingResponse>> getMyListingsByStatus(
            @JwtUser JwtClaims claims,
            @PathVariable ListingStatus status) {

        return ResponseEntity.ok(sellerListingService.getMyListingsByStatus(claims.getUserId(), status));
    }

    @GetMapping("/needs-attention")
    public ResponseEntity<NeedsAttentionResponse> getNeedsAttention(
            @JwtUser JwtClaims claims,
            @RequestParam(defaultValue = "10") int limit) {

        return ResponseEntity.ok(sellerListingService.getNeedsAttention(claims.getUserId(), limit));
    }

    @GetMapping("/recent")
    public ResponseEntity<RecentListingActivityResponse> getRecentListings(
            @JwtUser JwtClaims claims,
            @RequestParam(defaultValue = "10") int limit) {

        return ResponseEntity.ok(sellerListingService.getRecentListings(claims.getUserId(), limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListingDetailResponse> getListingDetails(
            @PathVariable UUID id,
            @JwtUser JwtClaims claims) {

        return ResponseEntity.ok(sellerListingService.getListingById(id, claims.getUserId()));
    }

    // --- 3. Submission Workflow ---

    @PutMapping("/{id}/submit") // Changed to PUT as it updates status
    public ResponseEntity<ListingResponse> submitForReview(
            @JwtUser JwtClaims claims,
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader) {

        return ResponseEntity.ok(listingService.submitListing(claims.getUserId(), id, authHeader));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ListingResponse> cancelSubmission(
            @JwtUser JwtClaims claims,
            @PathVariable UUID id) {

        return ResponseEntity.ok(listingService.cancelSubmission(claims.getUserId(), id));
    }

    @PostMapping("/bulk-submit")
    public ResponseEntity<BulkSubmitResponse> bulkSubmitDrafts(
            @JwtUser JwtClaims claims,
            @Valid @RequestBody BulkSubmitListingsRequest request,
            @RequestHeader("Authorization") String authHeader) {

        return ResponseEntity.ok(
                sellerListingService.bulkSubmit(claims.getUserId(), request.getListingIds(), authHeader)
        );
    }

    // --- 4. Sub-resource Updates (Amenities & Location) ---

    @PutMapping("/{id}/amenities")
    public ResponseEntity<Void> updateListingAmenities(
            @JwtUser JwtClaims claims,
            @PathVariable UUID id,
            @RequestBody List<UUID>  request) {

        listingService.updateListingAmenities(claims.getUserId(), id, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/location")
    public ResponseEntity<Void> updateListingLocation(
            @JwtUser JwtClaims claims,
            @PathVariable UUID id,
            @RequestBody UpdateListingLocationRequest request) {

        listingService.updateListingLocation(
                claims.getUserId(),
                id,
                request.getWardId(),
                request.getStreetAddress(),
                request.getLatitude(),
                request.getLongitude()
        );
        return ResponseEntity.ok().build();
    }

    // --- 5. Delete ---

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDraft(
            @JwtUser JwtClaims claims,
            @PathVariable UUID id) {

        listingService.deleteListing(claims.getUserId(), id);
        return ResponseEntity.noContent().build();
    }
}
