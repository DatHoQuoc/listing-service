package com.dathq.swd302.listingservice.service.impl;
import com.dathq.swd302.listingservice.model.Listing;
import com.dathq.swd302.listingservice.model.enums.ListingStatus;
import com.dathq.swd302.listingservice.repository.ListingRepository;
import com.dathq.swd302.listingservice.service.DocumentService;
import com.dathq.swd302.listingservice.service.ImageService;
import com.dathq.swd302.listingservice.service.ListingValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListingValidationServiceImpl implements ListingValidationService {

    private final ListingRepository listingRepository;
    private final ImageService imageService;
    private final DocumentService documentService;

    @Override
    public List<String> validateForSubmission(UUID listingId) {
        log.info("Validating listing for submission: {}", listingId);

        List<String> errors = new ArrayList<>();

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        if (listing.getTitle() == null || listing.getTitle().trim().isEmpty()) {
            errors.add("Title is required");
        }

        if (listing.getDescription() == null || listing.getDescription().trim().isEmpty()) {
            errors.add("Description is required");
        }

        if (listing.getPrice() == null || listing.getPrice().doubleValue() <= 0) {
            errors.add("Valid price is required");
        }

        if (listing.getAreaSqm() == null || listing.getAreaSqm().doubleValue() <= 0) {
            errors.add("Valid area is required");
        }

        if (listing.getWard().getWardId() == null) {
            errors.add("Location (ward) is required");
        }

        if (listing.getStreetAddress() == null || listing.getStreetAddress().trim().isEmpty()) {
            errors.add("Street address is required");
        }

        if (listing.getGeolocation() == null) {
            errors.add("Coordinates (latitude/longitude) are required");
        }

        Integer imageCount = imageService.countListingImages(listingId);
        if (imageCount == null || imageCount == 0) {
            errors.add("At least one image is required");
        }

        Integer documentCount = documentService.countListingDocuments(listingId);
        if (documentCount == null || documentCount == 0) {
            errors.add("At least one document is recommended");
        }

        if (!errors.isEmpty()) {
            log.warn("Listing validation failed with {} errors", errors.size());
        } else {
            log.info("Listing validation passed");
        }

        return errors;
    }

    @Override
    public boolean canUpdate(UUID listingId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
        return listing.getStatus() == ListingStatus.DRAFT;
    }

    @Override
    public boolean canDelete(UUID listingId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
        return listing.getStatus() == ListingStatus.DRAFT;
    }

    @Override
    public boolean isOwner(UUID userId, UUID listingId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
        return listing.getUserId().equals(userId);
    }
}
