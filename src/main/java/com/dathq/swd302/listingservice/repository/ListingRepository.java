package com.dathq.swd302.listingservice.repository;

import com.dathq.swd302.listingservice.model.Listing;
import com.dathq.swd302.listingservice.model.enums.ListingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ListingRepository extends JpaRepository<Listing, UUID> {
    // Find listing and ensure it belongs to the user (Security)
    Optional<Listing> findByListingIdAndUserId(UUID listingId, UUID userId);

    // Count for business logic
    Long countByUserIdAndStatus(UUID userId, ListingStatus status);

    @Query("SELECT COUNT(l) FROM Listing l WHERE l.userId = :userId AND l.status IN :statuses")
    Long countByUserIdAndStatusIn(@Param("userId") UUID userId, @Param("statuses") List<ListingStatus> statuses);

    // List fetching
    List<Listing> findAllByUserId(UUID userId);
    Page<Listing> findAllByUserId(UUID userId, Pageable pageable);
    List<Listing> findAllByUserIdAndStatus(UUID userId, ListingStatus status);

    Long countByUserId(UUID userId);

    List<Listing> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Page<Listing> findByUserId(UUID userId, Pageable pageable);

    List<Listing> findByUserIdAndStatusOrderByCreatedAtDesc(UUID userId, ListingStatus status);
}
