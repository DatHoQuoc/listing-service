package com.dathq.swd302.listingservice.model;


import com.dathq.swd302.listingservice.model.enums.PoiCategory;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "points_of_interest")
@Data
@NoArgsConstructor
public class PointOfInterest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID poiId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id")
    private Listing listing;

    @Column(nullable = false)
    private String name;

    // ── Changed: String → enum ──
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PoiCategory category;

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point geolocation;

    // ── Removed @Column: distanceMeters is computed, never stored ──
    @Column
    private Integer distanceMeters;


    @CreationTimestamp
    private OffsetDateTime createdAt;
}
