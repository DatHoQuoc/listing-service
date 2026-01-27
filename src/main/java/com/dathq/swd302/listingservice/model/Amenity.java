package com.dathq.swd302.listingservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "amenities")
@Data
@NoArgsConstructor
public class Amenity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID amenityId;

    @Column(nullable = false, unique = true)
    private String amenityName;

    private String amenityCategory;
    private String iconUrl;

    @CreationTimestamp
    private OffsetDateTime createdAt;
}
