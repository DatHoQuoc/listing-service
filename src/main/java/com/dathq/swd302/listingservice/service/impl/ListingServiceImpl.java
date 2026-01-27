package com.dathq.swd302.listingservice.service.impl;

import com.dathq.swd302.listingservice.dto.request.CreateListingRequest;
import com.dathq.swd302.listingservice.dto.request.UpdateListingRequest;
import com.dathq.swd302.listingservice.dto.response.ListingDetailResponse;
import com.dathq.swd302.listingservice.dto.response.ListingResponse;

import com.dathq.swd302.listingservice.exception.InvalidListingStateException;
import com.dathq.swd302.listingservice.exception.ListingNotFoundException;
import com.dathq.swd302.listingservice.exception.ListingValidationException;
import com.dathq.swd302.listingservice.exception.UnauthorizedException;
import com.dathq.swd302.listingservice.mapper.ListingMapper;
import com.dathq.swd302.listingservice.model.Amenity;
import com.dathq.swd302.listingservice.model.Listing;
import com.dathq.swd302.listingservice.model.Ward;
import com.dathq.swd302.listingservice.model.enums.ListingStatus;
import com.dathq.swd302.listingservice.repository.AmenityRepository;
import com.dathq.swd302.listingservice.repository.ListingRepository;
import com.dathq.swd302.listingservice.repository.WardRepository;
import com.dathq.swd302.listingservice.service.DocumentService;
import com.dathq.swd302.listingservice.service.ImageService;
import com.dathq.swd302.listingservice.service.ListingService;
import com.dathq.swd302.listingservice.service.ListingValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.postgis.Point;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ListingServiceImpl implements ListingService {

    private final ListingRepository listingRepository;
    private final AmenityRepository amenityRepository;
    private final WardRepository wardRepository;
    private final ListingMapper listingMapper;
    private final ListingValidationService listingValidationService;
    private final ImageService imageService;
    private final DocumentService documentService;

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

        if (request.getLatitude() != null && request.getLongitude() != null) {
            Point point = new Point(request.getLongitude(), request.getLatitude());
            listing.setGeolocation(point);
        }
        Listing savedListing = listingRepository.save(listing);

        log.info("Draft listing created with ID: {} for user: {}", savedListing.getListingId(), userId);

        return listingMapper.toResponse(savedListing);
    }

    @Override
    public ListingResponse updateListing(UUID userId, UUID listingId, UpdateListingRequest request) {
        log.info("Updating listing: {} by user: {}", listingId, userId);

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }

        if (listing.getStatus() != ListingStatus.DRAFT) {
            throw new InvalidListingStateException("Can only update listings in DRAFT status");
        }

        listingMapper.updateEntityFromRequest(request, listing);

        if (request.getLatitude() != null && request.getLongitude() != null) {
            Point point = new Point(request.getLongitude(), request.getLatitude());
            listing.setGeolocation(point);
        }

        listing.setUpdatedAt(OffsetDateTime.now());

        Listing updatedListing = listingRepository.save(listing);

        log.info("Listing updated successfully: {}", listingId);

        return listingMapper.toResponse(updatedListing);
    }

    @Override
    public ListingResponse submitListing(UUID userId, UUID listingId) {
        log.info("Submitting listing: {} by user: {}", listingId, userId);

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }

        if (listing.getStatus() != ListingStatus.DRAFT) {
            throw new InvalidListingStateException("Only DRAFT listings can be submitted");
        }

        List<String> validationErrors = listingValidationService.validateForSubmission(listingId);
        if (!validationErrors.isEmpty()) {
            throw new ListingValidationException("Listing validation failed: " + String.join(", ", validationErrors));
        }

        listing.setStatus(ListingStatus.PENDING_REVIEW);
        listing.setSubmittedAt(OffsetDateTime.now());
        listing.setUpdatedAt(OffsetDateTime.now());

        if (!listing.isFreePost()) {
            listing.setCreditsLocked(10);
        }

        Listing submittedListing = listingRepository.save(listing);

        log.info("Listing submitted successfully: {}", listingId);

        return listingMapper.toResponse(submittedListing);
    }

    @Override
    public ListingResponse cancelSubmission(UUID userId, UUID listingId) {
        log.info("Cancelling submission for listing: {}", listingId);

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }

        if (listing.getStatus() != ListingStatus.PENDING_REVIEW) {
            throw new InvalidListingStateException("Only PENDING_REVIEW listings can be cancelled");
        }

        listing.setStatus(ListingStatus.DRAFT);
        listing.setSubmittedAt(null);
        listing.setCreditsLocked(0);
        listing.setUpdatedAt(OffsetDateTime.now());

        Listing cancelledListing = listingRepository.save(listing);

        log.info("Listing submission cancelled: {}", listingId);

        return listingMapper.toResponse(cancelledListing);
    }

    @Override
    public java.util.List<ListingResponse> getMyListings(UUID userId) {
        log.info("Fetching all listings for user: {}", userId);

        List<Listing> listings = listingRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return listingMapper.toResponseList(listings);
    }

    @Override
    public org.springframework.data.domain.Page<ListingResponse> getMyListings(UUID userId, org.springframework.data.domain.Pageable pageable) {
        log.info("Fetching paginated listings for user: {}", userId);

        Page<Listing> listings = listingRepository.findByUserId(userId, pageable);
        return listings.map(listingMapper::toResponse);
    }

    @Override
    public java.util.List<ListingResponse> getMyListingsByStatus(UUID userId, ListingStatus status) {
        log.info("Fetching listings for user: {} with status: {}", userId, status);

        List<Listing> listings = listingRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status);
        return listingMapper.toResponseList(listings);
    }

    @Override
    @Transactional(readOnly = true)
    public ListingDetailResponse getListingById(UUID userId, UUID listingId) {
        log.info("Fetching listing details: {} for user: {}", listingId, userId);

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }

        ListingDetailResponse response = listingMapper.toDetailResponse(listing);

        Integer imageCount = imageService.countListingImages(listingId);
        response.setImageUrls(imageService.getListingImages(listingId).stream()
                .map(img -> img.getUrl())
                .collect(Collectors.toList()));

        response.setDocumentCount(documentService.countListingDocuments(listingId));

        return response;
    }

    @Override
    public void deleteListing(UUID userId, UUID listingId) {
        log.info("Deleting listing: {} by user: {}", listingId, userId);

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }

        if (listing.getStatus() != ListingStatus.DRAFT) {
            throw new InvalidListingStateException("Only DRAFT listings can be deleted");
        }

        listing.setStatus(ListingStatus.DELETED);
        listing.setUpdatedAt(OffsetDateTime.now());
        listingRepository.save(listing);

        log.info("Listing deleted successfully: {}", listingId);
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
        log.info("Updating amenities for listing: {}", listingId);

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }

        List<Amenity> amenities = amenityRepository.findAllById(amenityIds);

        if (amenities.size() != amenityIds.size()) {
            throw new IllegalArgumentException("One or more amenity IDs are invalid");
        }
        listing.setAmenities(amenities);
        listing.setUpdatedAt(OffsetDateTime.now());

        listingRepository.save(listing);

        log.info("Amenities updated for listing: {}", listingId);
    }

    @Override
    public void updateListingLocation(UUID userId, UUID listingId, UUID wardId, String streetAddress, Double latitude, Double longitude) {
        log.info("Updating location for listing: {}", listingId);

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }

        if (wardId != null) {
            Ward ward = wardRepository.findById(wardId)
                    .orElseThrow(() -> new RuntimeException("Ward not found"));
            listing.setWard(ward);
            listing.setProvince(ward.getProvince());
        }

        listing.setStreetAddress(streetAddress);

        if (latitude != null && longitude != null) {
            Point point = new Point(longitude, latitude);
            listing.setGeolocation(point);
        }

        listing.setUpdatedAt(OffsetDateTime.now());
        listingRepository.save(listing);

        log.info("Location updated for listing: {}", listingId);
    }
}
