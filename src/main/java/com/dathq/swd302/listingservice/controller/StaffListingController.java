package com.dathq.swd302.listingservice.controller;
import com.dathq.swd302.listingservice.dto.request.ApproveListingRequest;
import com.dathq.swd302.listingservice.dto.request.RejectListingRequest;
import com.dathq.swd302.listingservice.dto.response.ListingReviewResponse;
import com.dathq.swd302.listingservice.service.ListingReviewService;
import com.dathq.swd302.listingservice.service.ListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/staff/listings")
@RequiredArgsConstructor
public class StaffListingController {
    private final ListingReviewService listingReviewService;

    @PostMapping("/{listingId}/approve")
    public ResponseEntity<ListingReviewResponse> approveListing(
            @RequestHeader("X-User-Id") UUID staffId,
            @PathVariable UUID listingId,
            @Valid @RequestBody ApproveListingRequest request) {

        ListingReviewResponse response = listingReviewService.approveListing(staffId, listingId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{listingId}/reject")
    public ResponseEntity<ListingReviewResponse> rejectListing(
            @RequestHeader("X-User-Id") UUID staffId,
            @PathVariable UUID listingId,
            @Valid @RequestBody RejectListingRequest request) {

        ListingReviewResponse response = listingReviewService.rejectListing(staffId, listingId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{listingId}/review-history")
    public ResponseEntity<List<ListingReviewResponse>> getReviewHistory(
            @PathVariable UUID listingId) {

        List<ListingReviewResponse> reviews = listingReviewService.getReviewHistory(listingId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/pending-reviews")
    public ResponseEntity<List<ListingReviewResponse>> getPendingReviews() {
        List<ListingReviewResponse> reviews = listingReviewService.getPendingReviews();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/my-reviews")
    public ResponseEntity<List<ListingReviewResponse>> getMyReviews(
            @RequestHeader("X-User-Id") UUID staffId) {

        List<ListingReviewResponse> reviews = listingReviewService.getReviewsByStaff(staffId);
        return ResponseEntity.ok(reviews);
    }
}
