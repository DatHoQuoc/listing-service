package com.dathq.swd302.listingservice.controller;


import com.dathq.swd302.listingservice.dto.response.ListingDetailResponse;
import com.dathq.swd302.listingservice.dto.response.ListingResponse;
import com.dathq.swd302.listingservice.service.CustomerListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/listings")
@RequiredArgsConstructor
public class CustomerListingController {

    private final CustomerListingService customerListingService;

    @GetMapping
    public ResponseEntity<Page<ListingResponse>> getPublishedListings(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(customerListingService.getPublishedListings(pageable));
    }

    @GetMapping("/{listingId}")
    public ResponseEntity<ListingDetailResponse> getListingDetail(
            @PathVariable UUID listingId) {

        return ResponseEntity.ok(customerListingService.getPublishedListingById(listingId));
    }
}
