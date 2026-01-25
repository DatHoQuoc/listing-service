package com.dathq.swd302.listingservice.service;

public interface ListingService {
    /**
     * Create a new draft listing
     * - Automatically determines if this is user's first post (free)
     * - Sets status to DRAFT
     * - Does NOT lock credits yet
     *
     * @param userId the seller's user ID
     * @param request listing details
     * @return created listing response
     */
    ListingResponse createDraft(UUID userId, CreateListingRequest request);

    /**
     * Update an existing draft listing
     * - Only allows updates for DRAFT status
     * - Throws exception if listing is not in DRAFT state
     *
     * @param userId the seller's user ID (for authorization)
     * @param listingId the listing to update
     * @param request updated listing details
     * @return updated listing response
     */
    ListingResponse updateListing(UUID userId, UUID listingId, UpdateListingRequest request);

    /**
     * Submit listing for review
     * - Changes status from DRAFT to PENDING
     * - Locks credits (10) if not first post
     * - Creates credit lock record
     * - Calls Credit Service via gRPC to lock credits
     * - Sets submitted_at timestamp
     *
     * @param userId the seller's user ID
     * @param listingId the listing to submit
     * @return submitted listing response
     * @throws InsufficientCreditsException if user doesn't have enough credits
     * @throws InvalidListingStateException if listing is not in DRAFT state
     */
    ListingResponse submitListing(UUID userId, UUID listingId);

    /**
     * Get all listings for a specific seller
     * - Returns all statuses (DRAFT, PENDING, PUBLISHED, REJECTED, etc.)
     * - Sorted by created_at DESC
     *
     * @param userId the seller's user ID
     * @return list of seller's listings
     */
    List<ListingResponse> getMyListings(UUID userId);

    /**
     * Get all listings for a seller with pagination
     *
     * @param userId the seller's user ID
     * @param pageable pagination parameters
     * @return paginated list of listings
     */
    Page<ListingResponse> getMyListings(UUID userId, Pageable pageable);

    /**
     * Get all listings by status for a seller
     *
     * @param userId the seller's user ID
     * @param status the post status filter
     * @return list of listings with specified status
     */
    List<ListingResponse> getMyListingsByStatus(UUID userId, String status);

    /**
     * Get detailed information for a specific listing
     * - Returns full details including images, documents, address
     * - Only returns if listing belongs to the user
     *
     * @param userId the seller's user ID (for authorization)
     * @param listingId the listing ID
     * @return detailed listing response
     * @throws ListingNotFoundException if listing not found or doesn't belong to user
     */
    ListingDetailResponse getListingById(UUID userId, UUID listingId);

    /**
     * Delete a draft listing
     * - Only allows deletion of DRAFT status listings
     * - Permanently deletes the listing
     *
     * @param userId the seller's user ID (for authorization)
     * @param listingId the listing to delete
     * @throws InvalidListingStateException if listing is not in DRAFT state
     */
    void deleteListing(UUID userId, UUID listingId);

    /**
     * Cancel a pending listing
     * - Changes status from PENDING back to DRAFT
     * - Unlocks credits (if locked)
     * - Calls Credit Service to unlock credits
     *
     * @param userId the seller's user ID
     * @param listingId the listing to cancel
     * @throws InvalidListingStateException if listing is not in PENDING state
     */
    ListingResponse cancelSubmission(UUID userId, UUID listingId);

    /**
     * Check if user has any free post available
     *
     * @param userId the user ID
     * @return true if user has never posted before
     */
    boolean hasFreeListing(UUID userId);

    /**
     * Count total listings for a user
     *
     * @param userId the user ID
     * @return total count of listings
     */
    Long countUserListings(UUID userId);
}
