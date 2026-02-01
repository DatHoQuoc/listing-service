package com.dathq.swd302.listingservice.service.impl;

import com.dathq.swd302.listingservice.dto.response.AmenityResponse;
import com.dathq.swd302.listingservice.mapper.AmenityMapper;
import com.dathq.swd302.listingservice.model.Amenity;
import com.dathq.swd302.listingservice.model.Listing;
import com.dathq.swd302.listingservice.model.enums.AmenityCategory;
import com.dathq.swd302.listingservice.repository.AmenityRepository;
import com.dathq.swd302.listingservice.repository.ListingRepository;
import com.dathq.swd302.listingservice.service.AmenityService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AmenityServiceImpl implements AmenityService {


        private final AmenityRepository amenityRepository;
        private final ListingRepository listingRepository;
        private final AmenityMapper amenityMapper;

        @Override
        public List<AmenityResponse> getAllAmenities() {
            log.info("Fetching all amenities");
            List<Amenity> amenities = amenityRepository.findAllByOrderByAmenityCategoryAscAmenityNameAsc();
            return amenityMapper.toResponseList(amenities);
        }

        @Override
        public List<AmenityResponse> getAmenitiesByCategory(AmenityCategory category) {
            log.info("Fetching amenities by category: {}", category);
            List<Amenity> amenities = amenityRepository.findByAmenityCategoryOrderByAmenityName(category.name());
            return amenityMapper.toResponseList(amenities);
        }

        @Override
        public AmenityResponse getAmenityById(UUID amenityId) {
            log.info("Fetching amenity: {}", amenityId);
            Amenity amenity = amenityRepository.findById(amenityId)
                    .orElseThrow(() -> new RuntimeException("Amenity not found with id: " + amenityId));
            return amenityMapper.toResponse(amenity);
        }

        @Override
        public List<AmenityResponse> searchAmenities(String keyword) {
            log.info("Searching amenities with keyword: {}", keyword);
            List<Amenity> amenities = amenityRepository.findByAmenityNameContainingIgnoreCase(keyword);
            return amenityMapper.toResponseList(amenities);
        }

        @Override
        public List<AmenityResponse> getListingAmenities(UUID listingId) {
            log.info("Fetching amenities for listing: {}", listingId);
            return listingRepository.findById(listingId)
                    .map(Listing::getAmenities)
                    .map(amenityMapper::toResponseList)
                    .orElseThrow(() -> new EntityNotFoundException("Listing not found with id: " + listingId));
        }
}
