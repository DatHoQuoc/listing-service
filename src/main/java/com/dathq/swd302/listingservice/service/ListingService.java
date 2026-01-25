package com.dathq.swd302.listingservice.service;

import java.util.UUID;
import java.util.List;

import com.dathq.swd302.listingservice.dto.request.CreateListingRequest;
import com.dathq.swd302.listingservice.dto.request.UpdateListingRequest;
import com.dathq.swd302.listingservice.dto.response.ListingDetailResponse;
import com.dathq.swd302.listingservice.dto.response.ListingResponse;
import com.dathq.swd302.listingservice.model.enums.ListingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListingService {
    /**
     * Create a new draft listing
     * - Sets status to 'draft'
     * - Determines if this is user's first post (free)
     * - Does NOT validate completeness (can save partial data)
     *
     * @param userId  the seller's user ID
     * @param request basic listing information
     * @return created listing response
     */
    ListingResponse createDraft(UUID userId, CreateListingRequest request);

    /**
     * Update an existing draft listing
     * - Only works for listings with status='draft'
     * - Allows partial updates
     * - Validates user ownership
     *
     * @param userId    the seller's user ID
     * @param listingId the listing to update
     * @param request   updated listing data
     * @return updated listing response
     * @throws //ListingNotFoundException     if listing not found
     * @throws //InvalidListingStateException if listing is not in draft state
     * @throws // UnauthorizedException        if user doesn't own the listing
     */
    ListingResponse updateListing(UUID userId, UUID listingId, UpdateListingRequest request);

    /**
     * Submit listing for review
     * - Changes status from 'draft' to 'pending_review'
     * - Validates listing is complete (has images, documents, all required fields)
     * - Sets submitted_at timestamp
     * - In future: will lock credits via Credit Service
     *
     * @param userId    the seller's user ID
     * @param listingId the listing to submit
     * @return submitted listing response
     * @throws //ListingNotFoundException     if listing not found
     * @throws //InvalidListingStateException if listing not in draft state
     * @throws //ListingValidationException   if listing is incomplete
     */
    ListingResponse submitListing(UUID userId, UUID listingId);

    /**
     * Cancel a pending listing submission
     * - Changes status from 'pending_review' back to 'draft'
     * - Clears submitted_at timestamp
     * - In future: will unlock credits
     *
     * @param userId    the seller's user ID
     * @param listingId the listing to cancel
     * @return cancelled listing response
     * @throws //InvalidListingStateException if listing not in pending_review state
     */
    ListingResponse cancelSubmission(UUID userId, UUID listingId);

    /**
     * Get all listings for a user (all statuses)
     * - Returns user's listings sorted by created_at DESC
     * - Includes draft, pending, published, rejected, etc.
     *
     * @param userId the seller's user ID
     * @return list of user's listings
     */
    List<ListingResponse> getMyListings(UUID userId);

    /**
     * Get user's listings with pagination
     *
     * @param userId   the seller's user ID
     * @param pageable pagination parameters
     * @return paginated listings
     */
    Page<ListingResponse> getMyListings(UUID userId, Pageable pageable);

    /**
     * Get user's listings filtered by status
     *
     * @param userId the seller's user ID
     * @param status listing status filter
     * @return filtered listings
     */
    List<ListingResponse> getMyListingsByStatus(UUID userId, ListingStatus status);

    /**
     * Get detailed information for a specific listing
     * - Returns complete listing with all related data
     * - Includes amenities, POIs, virtual tour info, document count
     * - Validates user ownership
     *
     * @param userId    the seller's user ID
     * @param listingId the listing ID
     * @return detailed listing response
     * @throws //ListingNotFoundException if listing not found
     * @throws //UnauthorizedException    if user doesn't own listing
     */
    ListingDetailResponse getListingById(UUID userId, UUID listingId);

    /**
     * Delete a draft listing
     * - Only allows deletion of 'draft' status
     * - Soft delete (sets status to 'deleted')
     * - Also deletes associated images, documents, tours from storage
     *
     * @param userId    the seller's user ID
     * @param listingId the listing to delete
     * @throws //InvalidListingStateException if not in draft state
     */
    void deleteListing(UUID userId, UUID listingId);

    /**
     * Check if user has free listing available
     * - First listing is free
     * - Counts only published/pending listings (not drafts)
     *
     * @param userId the user ID
     * @return true if user has never published a listing
     */
    boolean hasFreeListing(UUID userId);

    /**
     * Count user's listings by status
     *
     * @param userId the user ID
     * @param status listing status (null for all)
     * @return count of listings
     */
    Long countUserListings(UUID userId, ListingStatus status);

    /**
     * Add amenities to a listing
     * - Replaces existing amenities
     *
     * @param userId     the user ID
     * @param listingId  the listing ID
     * @param amenityIds list of amenity IDs to add
     */
    void updateListingAmenities(UUID userId, UUID listingId, List<UUID> amenityIds);

    /**
     * Update listing location
     * - Updates ward, province, country, street address, geolocation
     *
     * @param userId        the user ID
     * @param listingId     the listing ID
     * @param wardId        ward ID
     * @param streetAddress street address
     * @param latitude      latitude coordinate
     * @param longitude     longitude coordinate
     */
    void updateListingLocation(UUID userId, UUID listingId, UUID wardId, String streetAddress,
                               Double latitude, Double longitude);
}
