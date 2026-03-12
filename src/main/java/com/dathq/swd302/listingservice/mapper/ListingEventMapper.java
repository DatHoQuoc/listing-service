package com.dathq.swd302.listingservice.mapper;

import com.dathq.swd302.listingservice.dto.ListingKafkaEvent;
import com.dathq.swd302.listingservice.model.Amenity;
import com.dathq.swd302.listingservice.model.Listing;

import java.util.List;

public class ListingEventMapper {

    public static ListingKafkaEvent toKafkaEvent(Listing listing) {
        return ListingKafkaEvent.builder()
                .listingId(listing.getListingId())
                .userId(listing.getUserId())
                .title(listing.getTitle())
                .description(listing.getDescription())
                .listingType(listing.getListingType() != null ? listing.getListingType().name() : null)
                .propertyType(listing.getPropertyType() != null ? listing.getPropertyType().name() : null)
                .status(listing.getStatus() != null ? listing.getStatus().name() : null)
                .isFreePost(listing.isFreePost())
                .price(listing.getPrice())
                .priceCurrency(listing.getPriceCurrency())
                .pricePeriod(listing.getPricePeriod())
                .negotiable(listing.isNegotiable())
                .areaSqm(listing.getAreaSqm())
                .bedrooms(listing.getBedrooms())
                .bathrooms(listing.getBathrooms())
                .floors(listing.getFloors())
                .floorNumber(listing.getFloorNumber())
                .yearBuilt(listing.getYearBuilt())
                .streetAddress(listing.getStreetAddress())
                .buildingName(listing.getBuildingName())
                // Safe null checks for lazy relations
                .wardName(listing.getWard() != null ? listing.getWard().getName() : null)
                .provinceName(listing.getProvince() != null ? listing.getProvince().getName() : null)
                .countryName(listing.getCountry() != null ? listing.getCountry().getName() : null)
                .latitude(listing.getGeolocation() != null ? listing.getGeolocation().getY() : null)
                .longitude(listing.getGeolocation() != null ? listing.getGeolocation().getX() : null)
                .featuredImageUrl(listing.getFeaturedImageUrl())
                .imagesJson(listing.getImagesJson())
                .additionalInfoJson(listing.getAdditionalInfoJson())
                .viewCount(listing.getViewCount())
                .saveCount(listing.getSaveCount())
                .contactCount(listing.getContactCount())
                .creditsLocked(listing.getCreditsLocked())
                .creditsCharged(listing.getCreditsCharged())
                .creditsRefunded(listing.getCreditsRefunded())
                .amenityNames(listing.getAmenities() != null
                        ? listing.getAmenities().stream().map(Amenity::getAmenityName).toList()
                        : List.of())
                .hasVirtualTour(listing.getVirtualTour() != null)
                .createdAt(listing.getCreatedAt())
                .updatedAt(listing.getUpdatedAt())
                .submittedAt(listing.getSubmittedAt())
                .publishedAt(listing.getPublishedAt())
                .expiredAt(listing.getExpiredAt())
                .build();
    }
}
