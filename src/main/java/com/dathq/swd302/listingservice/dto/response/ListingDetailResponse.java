package com.dathq.swd302.listingservice.dto.response;
import com.dathq.swd302.listingservice.model.enums.ListingStatus;
import com.dathq.swd302.listingservice.model.enums.ListingType;
import com.dathq.swd302.listingservice.model.enums.PropertyType;
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
public class ListingDetailResponse {
    private UUID listingId;
    private UUID userId;
    private String title;
    private String description;
    private ListingType listingType;
    private PropertyType propertyType;
    private ListingStatus status;
    private Boolean isFreePost;
    private BigDecimal price;
    private String priceCurrency;
    private String pricePeriod;
    private Boolean negotiable;
    private BigDecimal areaSqm;
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer floors;
    private Integer floorNumber;
    private Integer yearBuilt;
    private UUID wardId;
    private UUID provinceId;
    private UUID countryId;
    private String streetAddress;
    private String buildingName;
    private String featuredImageUrl;
    private Integer viewCount;
    private Integer saveCount;
    private Integer contactCount;
    private Integer creditsLocked;
    private Integer creditsCharged;
    private Integer creditsRefunded;
    private String rejectionReason;
    private String adminNotes;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime submittedAt;
    private OffsetDateTime reviewedAt;
    private OffsetDateTime publishedAt;
    private OffsetDateTime expiredAt;

    private List<String> imageUrls;
    private Integer documentCount;
    private Boolean hasVirtualTour;
    private List<AmenityResponse> amenities;
}
