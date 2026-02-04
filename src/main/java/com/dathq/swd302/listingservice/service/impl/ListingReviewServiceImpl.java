package com.dathq.swd302.listingservice.service.impl;

import com.dathq.swd302.listingservice.dto.request.ApproveListingRequest;
import com.dathq.swd302.listingservice.dto.request.RejectListingRequest;
import com.dathq.swd302.listingservice.dto.request.RequestChangesRequest;
import com.dathq.swd302.listingservice.dto.response.ListingReviewResponse;
import com.dathq.swd302.listingservice.exception.InvalidListingStateException;
import com.dathq.swd302.listingservice.exception.ListingNotFoundException;
import com.dathq.swd302.listingservice.mapper.ListingReviewMapper;
import com.dathq.swd302.listingservice.model.Listing;
import com.dathq.swd302.listingservice.model.ListingReview;
import com.dathq.swd302.listingservice.model.enums.ListingStatus;
import com.dathq.swd302.listingservice.model.enums.ReviewAction;
import com.dathq.swd302.listingservice.repository.ListingRepository;
import com.dathq.swd302.listingservice.repository.ListingReviewRepository;
import com.dathq.swd302.listingservice.service.ListingReviewService;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ListingReviewServiceImpl implements ListingReviewService {

    private final ListingReviewRepository listingReviewRepository;
    private final ListingRepository listingRepository;
    private final ListingReviewMapper listingReviewMapper;
    private final ObjectMapper objectMapper;

    @Override
    public ListingReviewResponse approveListing(UUID staffId, UUID listingId, ApproveListingRequest request) {
        log.info("Approving listing: {} by staff: {}", listingId, staffId);

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (listing.getStatus() != ListingStatus.PENDING_REVIEW) {
            throw new InvalidListingStateException("Only PENDING_REVIEW listings can be approved");
        }

        String previousStatus = listing.getStatus().name();

        listing.setStatus(ListingStatus.PUBLISHED);
        listing.setPublishedAt(OffsetDateTime.now());
        listing.setReviewedAt(OffsetDateTime.now());

        if (!listing.isFreePost()) {
            listing.setCreditsCharged(listing.getCreditsLocked());
            listing.setCreditsLocked(0);
        }

        listingRepository.save(listing);

        Integer reviewVersion = getNextReviewVersion(listingId);
        UUID previousReviewId = getPreviousReviewId(listingId);
        boolean isResubmission = previousReviewId != null;

        ListingReview review = new ListingReview();
        review.setListingId(listingId);
        review.setReviewerId(staffId);
        review.setPreviousStatus(previousStatus);
        review.setNewStatus(ListingStatus.PUBLISHED.name());
        review.setReviewAction(ReviewAction.APPROVE);
        review.setStaffNotesInternal(request.getStaffNotesInternal());
        review.setFeedbackToSeller(request.getFeedbackToSeller());
        review.setIsResubmission(isResubmission);
        review.setPreviousReviewId(previousReviewId);
        review.setReviewVersion(reviewVersion);
        review.setReviewedAt(OffsetDateTime.now());

        try {
            String checklistJson = objectMapper.writeValueAsString(request.getChecklist());
            JsonNode node = objectMapper.readTree(checklistJson);
            review.setChecklistResultsJson(node);
        } catch (Exception e) {
            log.error("Error serializing checklist results: {}", e.getMessage());
        }

        ListingReview savedReview = listingReviewRepository.save(review);

        log.info("Listing approved: {}, review ID: {}", listingId, savedReview.getReviewId());

        return listingReviewMapper.toResponse(savedReview);
    }

    @Override
    public ListingReviewResponse rejectListing(UUID staffId, UUID listingId, RejectListingRequest request) {
        log.info("Rejecting listing: {} by staff: {}", listingId, staffId);

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (listing.getStatus() != ListingStatus.PENDING_REVIEW) {
            throw new InvalidListingStateException("Only PENDING_REVIEW listings can be rejected");
        }

        String previousStatus = listing.getStatus().name();

        listing.setStatus(ListingStatus.REJECTED);
        listing.setRejectionReason(request.getRejectionReason());
        listing.setReviewedAt(OffsetDateTime.now());

        if (!listing.isFreePost()) {
            listing.setCreditsRefunded(listing.getCreditsLocked());
            listing.setCreditsLocked(0);
        }

        listingRepository.save(listing);

        Integer reviewVersion = getNextReviewVersion(listingId);
        UUID previousReviewId = getPreviousReviewId(listingId);
        boolean isResubmission = previousReviewId != null;

        ListingReview review = new ListingReview();
        review.setListingId(listingId);
        review.setReviewerId(staffId);
        review.setPreviousStatus(previousStatus);
        review.setNewStatus(ListingStatus.REJECTED.name());
        review.setReviewAction(ReviewAction.REJECT);
        review.setStaffNotesInternal(request.getStaffNotesInternal());
        review.setFeedbackToSeller(request.getFeedbackToSeller());
        review.setRejectionReason(request.getRejectionReason());
        review.setIsResubmission(isResubmission);
        review.setPreviousReviewId(previousReviewId);
        review.setReviewVersion(reviewVersion);
        review.setReviewedAt(OffsetDateTime.now());

        ListingReview savedReview = listingReviewRepository.save(review);

        log.info("Listing rejected: {}, review ID: {}", listingId, savedReview.getReviewId());

        return listingReviewMapper.toResponse(savedReview);
    }

    @Override
    public ListingReviewResponse requestChanges(UUID staffId, UUID listingId, RequestChangesRequest request) {
        return null;
    }

    @Override
    public List<ListingReviewResponse> getReviewHistory(UUID listingId) {
        return List.of();
    }

    @Override
    public ListingReviewResponse getLatestReview(UUID listingId) {
        return null;
    }

    @Override
    public List<ListingReviewResponse> getPendingReviews() {
        return List.of();
    }

    @Override
    public List<ListingReviewResponse> getReviewsByStaff(UUID staffId) {
        return List.of();
    }

    @Override
    public boolean hasBeenReviewed(UUID listingId) {
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getReviewVersion(UUID listingId) {
        return listingReviewRepository.findTopByListingIdOrderByReviewVersionDesc(listingId)
                .map(ListingReview::getReviewVersion)
                .orElse(0);
    }

    private Integer getNextReviewVersion(UUID listingId) {
        Integer currentVersion = getReviewVersion(listingId);
        return currentVersion + 1;
    }

    private UUID getPreviousReviewId(UUID listingId) {
        return listingReviewRepository.findTopByListingIdOrderByReviewedAtDesc(listingId)
                .map(ListingReview::getReviewId)
                .orElse(null);
    }
}
