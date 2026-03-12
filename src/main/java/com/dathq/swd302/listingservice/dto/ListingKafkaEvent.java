package com.dathq.swd302.listingservice.dto;

import com.dathq.swd302.listingservice.model.Listing;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingKafkaEvent {

    // Identity
    private UUID listingId;
    private UUID userId;

    // Basic Info
    private String title;
    private String description;
    private String listingType;
    private String propertyType;

    // Status
    private String status;
    private boolean isFreePost;

    // Pricing
    private BigDecimal price;
    private String priceCurrency;
    private String pricePeriod;
    private boolean negotiable;

    // Property Details
    private BigDecimal areaSqm;
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer floors;
    private Integer floorNumber;
    private Integer yearBuilt;

    // Location (flattened — no lazy entity refs)
    private String streetAddress;
    private String buildingName;
    private String wardName;
    private String provinceName;
    private String countryName;
    private Double latitude;
    private Double longitude;

    // Media
    private String featuredImageUrl;
    private List<Listing.ImageMetadata> imagesJson;
    private JsonNode additionalInfoJson;

    // Metrics
    private int viewCount;
    private int saveCount;
    private int contactCount;

    // Credits
    private int creditsLocked;
    private int creditsCharged;
    private int creditsRefunded;

    // Relations (flattened to names/ids only)
    private List<String> amenityNames;
    private boolean hasVirtualTour;

    // Timestamps
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime submittedAt;
    private OffsetDateTime publishedAt;
    private OffsetDateTime expiredAt;
}
