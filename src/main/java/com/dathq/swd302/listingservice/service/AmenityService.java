package com.dathq.swd302.listingservice.service;

import com.dathq.swd302.listingservice.dto.response.AmenityResponse;
import com.dathq.swd302.listingservice.model.enums.AmenityCategory;

import java.util.List;
import java.util.UUID;
public interface AmenityService {
    /**
     * Get all available amenities
     * - Returns complete amenity catalog
     * - Sorted by category and name
     *
     * @return list of all amenities
     */
    List<AmenityResponse> getAllAmenities();

    /**
     * Get amenities by category
     *
     * @param category amenity category
     * @return filtered amenities
     */
    List<AmenityResponse> getAmenitiesByCategory(AmenityCategory category);

    /**
     * Get amenity by ID
     *
     * @param amenityId amenity ID
     * @return amenity details
     */
    AmenityResponse getAmenityById(UUID amenityId);

    /**
     * Search amenities by name
     *
     * @param keyword search keyword
     * @return matching amenities
     */
    List<AmenityResponse> searchAmenities(String keyword);

    /**
     * Get amenities for a listing
     *
     * @param listingId listing ID
     * @return list of amenities assigned to listing
     */
    List<AmenityResponse> getListingAmenities(UUID listingId);
}
