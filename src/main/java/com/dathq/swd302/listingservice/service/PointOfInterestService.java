package com.dathq.swd302.listingservice.service;


import com.dathq.swd302.listingservice.dto.request.CreatePOIRequest;
import com.dathq.swd302.listingservice.dto.response.POIResponse;

import java.util.List;
import java.util.UUID;
public interface PointOfInterestService {

    /**
     * Add a point of interest to listing
     * - Manually added by seller
     * - Stores name, category, distance, coordinates
     *
     * @param userId user ID
     * @param listingId listing ID
     * @param request POI details
     * @return created POI
     */
    POIResponse addPointOfInterest(UUID userId, UUID listingId, CreatePOIRequest request);

    /**
     * Add multiple POIs at once
     *
     * @param userId user ID
     * @param listingId listing ID
     * @param requests list of POIs
     * @return list of created POIs
     */
    List<POIResponse> addPointsOfInterest(UUID userId, UUID listingId, List<CreatePOIRequest> requests);

    /**
     * Get all POIs for a listing
     * - Sorted by distance (nearest first)
     *
     * @param listingId listing ID
     * @return list of POIs
     */
    List<POIResponse> getListingPOIs(UUID listingId);

    /**
     * Get POIs by category
     *
     * @param listingId listing ID
     * @param category POI category (school, hospital, transport, etc.)
     * @return filtered POIs
     */
    List<POIResponse> getListingPOIsByCategory(UUID listingId, String category);

    /**
     * Update a POI
     *
     * @param userId user ID
     * @param listingId listing ID
     * @param poiId POI ID
     * @param request updated POI data
     * @return updated POI
     */
    POIResponse updatePointOfInterest(UUID userId, UUID listingId, UUID poiId,
                                      CreatePOIRequest request);

    /**
     * Delete a POI
     *
     * @param userId user ID
     * @param listingId listing ID
     * @param poiId POI ID to delete
     */
    void deletePointOfInterest(UUID userId, UUID listingId, UUID poiId);

    /**
     * Delete all POIs for a listing
     *
     * @param userId user ID
     * @param listingId listing ID
     */
    void deleteAllPOIs(UUID userId, UUID listingId);
}
