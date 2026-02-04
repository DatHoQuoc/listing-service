package com.dathq.swd302.listingservice.repository;
import com.dathq.swd302.listingservice.model.ListingReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ListingReviewRepository extends JpaRepository<ListingReview, UUID>{
    List<ListingReview> findByListingIdOrderByReviewedAtDesc(UUID listingId);

    List<ListingReview> findByReviewerIdOrderByReviewedAtDesc(UUID reviewerId);

    Optional<ListingReview> findTopByListingIdOrderByReviewedAtDesc(UUID listingId);

    Optional<ListingReview> findTopByListingIdOrderByReviewVersionDesc(UUID listingId);

    boolean existsByListingId(UUID listingId);

    Long countByListingId(UUID listingId);

    Long countByReviewerId(UUID reviewerId);
}
