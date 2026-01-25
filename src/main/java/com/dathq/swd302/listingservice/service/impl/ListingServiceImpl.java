package com.dathq.swd302.listingservice.service.impl;

import com.dathq.swd302.listingservice.dto.request.CreateListingRequest;
import com.dathq.swd302.listingservice.dto.request.UpdateListingRequest;
import com.dathq.swd302.listingservice.dto.response.ListingDetailResponse;
import com.dathq.swd302.listingservice.dto.response.ListingResponse;

import com.dathq.swd302.listingservice.mapper.ListingMapper;
import com.dathq.swd302.listingservice.model.Listing;
import com.dathq.swd302.listingservice.model.enums.ListingStatus;
import com.dathq.swd302.listingservice.repository.ListingRepository;
import com.dathq.swd302.listingservice.service.ListingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ListingServiceImpl implements ListingService {

    private final ListingRepository listingRepository;
    private final ListingMapper listingMapper;
    @Override
    public ListingResponse createDraft(UUID userId, CreateListingRequest request) {
        log.info("Creating draft listing for user: {}", userId);

        Long userListingCount = listingRepository.countByUserIdAndStatusIn(
                userId,
                java.util.List.of(ListingStatus.PUBLISHED, ListingStatus.PENDING_REVIEW)
        );

        boolean isFreePost = (userListingCount == 0);

        Listing listing = listingMapper.toEntity(request);
        listing.setUserId(userId);
        listing.setStatus(ListingStatus.DRAFT);
        listing.setFreePost(isFreePost);
        listing.setViewCount(0);
        listing.setSaveCount(0);
        listing.setContactCount(0);
        listing.setCreditsLocked(0);
        listing.setCreditsCharged(0);
        listing.setCreditsRefunded(0);
        listing.setCreatedAt(OffsetDateTime.now());
        listing.setUpdatedAt(OffsetDateTime.now());

        Listing savedListing = listingRepository.save(listing);

        log.info("Draft listing created with ID: {} for user: {}", savedListing.getListingId(), userId);

        return listingMapper.toResponse(savedListing);
    }

    @Override
    public ListingResponse updateListing(UUID userId, UUID listingId, UpdateListingRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ListingResponse submitListing(UUID userId, UUID listingId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ListingResponse cancelSubmission(UUID userId, UUID listingId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public java.util.List<ListingResponse> getMyListings(UUID userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public org.springframework.data.domain.Page<ListingResponse> getMyListings(UUID userId, org.springframework.data.domain.Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public java.util.List<ListingResponse> getMyListingsByStatus(UUID userId, ListingStatus status) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ListingDetailResponse getListingById(UUID userId, UUID listingId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteListing(UUID userId, UUID listingId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean hasFreeListing(UUID userId) {
        Long publishedCount = listingRepository.countByUserIdAndStatusIn(
                userId,
                java.util.List.of(ListingStatus.PUBLISHED, ListingStatus.PENDING_REVIEW)
        );
        return publishedCount == 0;
    }

    @Override
    public Long countUserListings(UUID userId, ListingStatus status) {
        if (status == null) {
            return listingRepository.countByUserId(userId);
        }
        return listingRepository.countByUserIdAndStatus(userId, status);
    }

    @Override
    public void updateListingAmenities(UUID userId, UUID listingId, java.util.List<UUID> amenityIds) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void updateListingLocation(UUID userId, UUID listingId, UUID wardId, String streetAddress, Double latitude, Double longitude) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
