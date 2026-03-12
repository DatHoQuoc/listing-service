package com.dathq.swd302.listingservice.service;

import com.dathq.swd302.listingservice.dto.response.ListingDetailResponse;
import com.dathq.swd302.listingservice.dto.response.ListingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CustomerListingService {
    Page<ListingResponse> getPublishedListings(Pageable pageable);
    ListingDetailResponse getPublishedListingById(UUID listingId);
}
