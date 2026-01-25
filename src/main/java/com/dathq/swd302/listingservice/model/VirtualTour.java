package com.dathq.swd302.listingservice.model;


import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "virtual_tours")
@Data
@NoArgsConstructor
public class VirtualTour {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID tourId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    private String tourUrl;
    private int totalScenes = 0;
    private String tourProvider;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode tourDataJson;

    private boolean isPublished = false;

    @OneToMany(mappedBy = "virtualTour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TourScene> scenes;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
