package com.dathq.swd302.listingservice.model;


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

    @Column(nullable = false)
    private String category;

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point geolocation;

    private Integer distanceMeters;

    @CreationTimestamp
    private OffsetDateTime createdAt;
}
