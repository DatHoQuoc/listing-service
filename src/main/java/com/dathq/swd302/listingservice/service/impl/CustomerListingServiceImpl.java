package com.dathq.swd302.listingservice.service.impl;


import com.dathq.swd302.listingservice.dto.response.ListingDetailResponse;
import com.dathq.swd302.listingservice.dto.response.ListingResponse;
import com.dathq.swd302.listingservice.exception.ListingNotFoundException;
import com.dathq.swd302.listingservice.mapper.ListingMapper;
import com.dathq.swd302.listingservice.model.Listing;
import com.dathq.swd302.listingservice.model.enums.ListingStatus;
import com.dathq.swd302.listingservice.repository.ListingRepository;
import com.dathq.swd302.listingservice.service.CustomerListingService;
import com.dathq.swd302.listingservice.service.DocumentService;
import com.dathq.swd302.listingservice.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerListingServiceImpl implements CustomerListingService {

    private final ListingRepository listingRepository;
    private final ListingMapper listingMapper;
    private final ImageService imageService;
    private final DocumentService documentService;

    @Override
    public Page<ListingResponse> getPublishedListings(Pageable pageable) {
        log.info("Fetching paginated published listings for customers");

        Page<Listing> listings = listingRepository.findByStatus(ListingStatus.PUBLISHED, pageable);

        if (listings.isEmpty()) {
            log.info("No published listings found");
            return Page.empty(pageable);
        }

        return listings.map(listing -> {
            ListingResponse response = listingMapper.toResponse(listing);
            List<String> imageUrls = imageService.getListingImages(listing.getListingId()).stream()
                    .map(img -> img.getUrl())
                    .collect(Collectors.toList());
            if (!imageUrls.isEmpty()) {
                response.setFeaturedImageUrl(imageUrls.get(0));
            }
            return response;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public ListingDetailResponse getPublishedListingById(UUID listingId) {
        log.info("Fetching published listing details for customer: {}", listingId);

        Listing listing = listingRepository.findByListingIdAndStatus(listingId, ListingStatus.PUBLISHED)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        ListingDetailResponse response = listingMapper.toDetailResponse(listing);

        response.setImageUrls(imageService.getListingImages(listingId).stream()
                .map(img -> img.getUrl())
                .collect(Collectors.toList()));

        response.setDocumentCount(documentService.countListingDocuments(listingId));

        return response;
    }
}
