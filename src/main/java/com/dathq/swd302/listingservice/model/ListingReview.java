package com.dathq.swd302.listingservice.model;
import com.dathq.swd302.listingservice.model.enums.ReviewAction;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "listings_reviews")
@Data
public class ListingReview {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID reviewId;

    @Column(nullable = false)
    private UUID listingId;

    @Column(nullable = false)
    private UUID reviewerId;

    @Column
    private String reviewerRole; // NEW

    @Column
    private UUID feedbackReportId; // NEW - link to AI feedback

    private String previousStatus;
    private String newStatus;

    @Enumerated(EnumType.STRING)
    private ReviewAction reviewAction; // APPROVE, REJECT, REQUEST_CHANGES

    // Separate notes
    private String staffNotesInternal; // NEW
    private String feedbackToSeller;   // NEW (replaces reviewNotes)

    private String rejectionReason;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode requiredChangesJson; // NEW - structured changes

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode checklistResultsJson; // NEW

    // Resubmission tracking
    @Column
    private Boolean isResubmission = false; // NEW

    @Column
    private UUID previousReviewId; // NEW

    @Column
    private Integer reviewVersion = 1; // NEW

    @CreationTimestamp
    private OffsetDateTime reviewedAt;
}
