package com.dathq.swd302.listingservice.service;
import com.dathq.swd302.listingservice.dto.request.ApproveListingRequest;
import com.dathq.swd302.listingservice.dto.request.RejectListingRequest;
import com.dathq.swd302.listingservice.dto.request.RequestChangesRequest;
import com.dathq.swd302.listingservice.dto.response.ListingReviewResponse;

import java.util.List;
import java.util.UUID;
public interface ListingReviewService {
    ListingReviewResponse approveListing(UUID staffId, UUID listingId, ApproveListingRequest request);

    ListingReviewResponse rejectListing(UUID staffId, UUID listingId, RejectListingRequest request);

    ListingReviewResponse requestChanges(UUID staffId, UUID listingId, RequestChangesRequest request);

    List<ListingReviewResponse> getReviewHistory(UUID listingId);

    ListingReviewResponse getLatestReview(UUID listingId);

    List<ListingReviewResponse> getPendingReviews();

    List<ListingReviewResponse> getReviewsByStaff(UUID staffId);

    boolean hasBeenReviewed(UUID listingId);

    Integer getReviewVersion(UUID listingId);
}
