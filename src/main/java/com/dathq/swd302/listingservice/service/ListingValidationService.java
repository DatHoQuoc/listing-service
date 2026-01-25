package com.dathq.swd302.listingservice.service;

import java.util.List;
import java.util.UUID;
public interface ListingValidationService {
    /**
     * Validate listing is ready for submission
     * - Checks all required fields are filled
     * - Validates at least 1 image exists
     * - Validates location is set
     * - Validates price is reasonable
     * - Checks if at least 1 document uploaded (recommended)
     *
     * @param listingId listing to validate
     * @return list of validation errors (empty if valid)
     */
    List<String> validateForSubmission(UUID listingId);

    /**
     * Validate listing can be updated
     * - Checks if listing is in 'draft' status
     *
     * @param listingId listing ID
     * @return true if can be updated
     */
    boolean canUpdate(UUID listingId);

    /**
     * Validate listing can be deleted
     * - Only 'draft' listings can be deleted
     *
     * @param listingId listing ID
     * @return true if can be deleted
     */
    boolean canDelete(UUID listingId);

    /**
     * Validate user owns the listing
     *
     * @param userId user ID
     * @param listingId listing ID
     * @return true if user owns listing
     */
    boolean isOwner(UUID userId, UUID listingId);
}
