package com.dathq.swd302.listingservice.model;


import com.dathq.swd302.listingservice.model.enums.ListingStatus;
import com.dathq.swd302.listingservice.model.enums.ListingType;
import com.dathq.swd302.listingservice.model.enums.PropertyType;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point; // Requires Hibernate Spatial

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "listings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID listingId;

    // --- Owner Info (External User Service) ---
    @Column(nullable = false)
    private UUID userId;

    // --- Basic Info ---
    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListingType listingType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PropertyType propertyType;

    // --- Status & Lifecycle ---
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListingStatus status = ListingStatus.DRAFT;

    @Column(nullable = false)
    private boolean isFreePost = false;

    // --- Pricing ---
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(length = 3)
    private String priceCurrency = "VND";

    private String pricePeriod; // monthly, yearly, etc.
    private boolean negotiable = true;

    // --- Property Details ---
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal areaSqm;

    private Integer bedrooms;
    private Integer bathrooms;
    private Integer floors;
    private Integer floorNumber;
    private Integer yearBuilt;

    // --- Location ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id")
    private Ward ward;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id")
    private Province province;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    @Column(length = 500)
    private String streetAddress;

    private String buildingName;

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point geolocation;

    // --- Media (JSONB) ---
    @Column(columnDefinition = "TEXT")
    private String featuredImageUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<ImageMetadata> imagesJson; // Define POJO below

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode additionalInfoJson;

    // --- Metrics ---
    private int viewCount = 0;
    private int saveCount = 0;
    private int contactCount = 0;

    // --- Review ---
    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(columnDefinition = "TEXT")
    private String adminNotes;

    // --- Credits ---
    private int creditsLocked = 0;
    private int creditsCharged = 0;
    private int creditsRefunded = 0;

    // --- Relationships ---
    @ManyToMany
    @JoinTable(
            name = "listing_amenities",
            joinColumns = @JoinColumn(name = "listing_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private List<Amenity> amenities;

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PointOfInterest> pointsOfInterest;

    @OneToOne(mappedBy = "listing", cascade = CascadeType.ALL)
    private VirtualTour virtualTour;

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL)
    private List<LegalDocument> documents;

    // --- Timestamps ---
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    private OffsetDateTime submittedAt;
    private OffsetDateTime reviewedAt;
    private OffsetDateTime publishedAt;
    private OffsetDateTime expiredAt;

    // Helper POJO for JSON mapping
    @Data
    public static class ImageMetadata {
        private String url;
        private int order;
        private String caption;
    }
}
