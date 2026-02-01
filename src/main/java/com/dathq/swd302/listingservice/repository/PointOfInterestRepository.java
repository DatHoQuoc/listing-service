package com.dathq.swd302.listingservice.repository;

import com.dathq.swd302.listingservice.model.PointOfInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface PointOfInterestRepository extends JpaRepository<PointOfInterest, UUID>{
    List<PointOfInterest> findByListing_ListingId(UUID listingId);

    List<PointOfInterest> findByListing_ListingIdOrderByDistanceMeters(UUID listingId);

    List<PointOfInterest> findByListing_ListingIdAndCategory(UUID listingId, String category);

    List<PointOfInterest> findByListing_ListingIdAndCategoryOrderByDistanceMeters(UUID listingId, String category);

    // Fixed: added _ListingId
    void deleteByListing_ListingId(UUID listingId);

    // Fixed: added _ListingId
    long countByListing_ListingId(UUID listingId);
}
