package com.dathq.swd302.listingservice.controller;

import com.dathq.swd302.listingservice.dto.request.ApproveListingRequest;
import com.dathq.swd302.listingservice.dto.request.RejectListingRequest;
import com.dathq.swd302.listingservice.dto.response.ListingResponse;
import com.dathq.swd302.listingservice.dto.response.ListingReviewResponse;
import com.dathq.swd302.listingservice.security.JwtClaims;
import com.dathq.swd302.listingservice.security.JwtUser;
import com.dathq.swd302.listingservice.service.ListingReviewService;
import com.dathq.swd302.listingservice.service.ListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/staff/listings")
@RequiredArgsConstructor
public class StaffListingController {
    private final ListingReviewService listingReviewService;


    @GetMapping("/pending")
    public ResponseEntity<Page<ListingResponse>> getPendingListings(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(listingReviewService.getPendingListings(pageable));
    }

    @PutMapping("/{listingId}/approve")
    public ResponseEntity<ListingReviewResponse> approveListing(
            @PathVariable UUID listingId,
            @RequestBody @Valid ApproveListingRequest request,
            @JwtUser JwtClaims claims,
            @RequestHeader("Authorization") String authHeader) {

        ListingReviewResponse response = listingReviewService.approveListing(claims.getUserId(), listingId, request,
                authHeader);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{listingId}/reject")
    public ResponseEntity<ListingReviewResponse> rejectListing(
            @JwtUser JwtClaims claims,
            @PathVariable UUID listingId,
            @Valid @RequestBody RejectListingRequest request) {

        ListingReviewResponse response = listingReviewService.rejectListing(claims.getUserId(), listingId, request);
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
            @JwtUser JwtClaims claims) {

        List<ListingReviewResponse> reviews = listingReviewService.getReviewsByStaff(claims.getUserId());
        return ResponseEntity.ok(reviews);
    }
}
