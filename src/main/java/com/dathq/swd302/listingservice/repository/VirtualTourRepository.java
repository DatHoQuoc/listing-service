package com.dathq.swd302.listingservice.repository;

import com.dathq.swd302.listingservice.model.VirtualTour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VirtualTourRepository extends JpaRepository<VirtualTour, UUID> {
    boolean existsByListing_ListingId(UUID listingId);

    Optional<VirtualTour> findByListing_ListingId(UUID listingId);
}
