package com.dathq.swd302.listingservice.model;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
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
@Table(name = "feedback_reports")
@Data
public class FeedbackReport {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID feedbackReportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id")
    private Listing listing;

    @Column(nullable = false)
    private UUID sellerUserId;

    @Column(nullable = false)
    private String checkType;

    private String overallStatus;
    private BigDecimal aiConfidenceScore;

    private UUID reviewedByStaffId;
    private OffsetDateTime reviewedAt;

    private boolean isResubmission = false;

    @OneToOne
    @JoinColumn(name = "previous_feedback_id")
    private FeedbackReport previousReport;

    @OneToMany(mappedBy = "feedbackReport", cascade = CascadeType.ALL)
    private List<FeedbackItem> items;

    @CreationTimestamp
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    private OffsetDateTime updatedAt;
}
