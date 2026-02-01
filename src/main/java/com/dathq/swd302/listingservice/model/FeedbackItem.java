package com.dathq.swd302.listingservice.model;

import com.dathq.swd302.listingservice.model.enums.FeedbackCategory;
import com.dathq.swd302.listingservice.model.enums.FeedbackSeverity;
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
@Table(name = "feedback_items")
@Data
public class FeedbackItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID feedbackItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedback_report_id")
    private FeedbackReport feedbackReport;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackSeverity severity;

    private String fieldName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String errorMessage;

    private String suggestion;

    private String detectedBy;
    private boolean isFixed = false;
    private OffsetDateTime fixedAt;

    @CreationTimestamp
    private OffsetDateTime createdAt;
}
