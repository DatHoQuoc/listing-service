package com.dathq.swd302.listingservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.List;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "wards", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "province_id"}))
@Data
@NoArgsConstructor
public class Ward {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID wardId;

    @Column(nullable = false)
    private String name;

    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id", nullable = false)
    private Province province;

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point geolocation;

    @CreationTimestamp
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    private OffsetDateTime updatedAt;
}
