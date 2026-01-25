package com.dathq.swd302.listingservice.model;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "listings_reviews")
@Data
public class ListingReview {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id")
    private Listing listing;

    @Column(nullable = false)
    private UUID reviewerId;

    private String previousStatus;
    private String newStatus;
    private String reviewAction;
    private String reviewNotes;
    private String rejectionReason;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode changesRequestedJson;

    @CreationTimestamp
    private Instant reviewedAt;
}
