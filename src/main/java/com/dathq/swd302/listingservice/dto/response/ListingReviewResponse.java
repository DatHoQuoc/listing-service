package com.dathq.swd302.listingservice.dto.response;
import com.dathq.swd302.listingservice.model.enums.ReviewAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingReviewResponse {
    private UUID reviewId;
    private UUID listingId;
    private UUID reviewerId;
    private String reviewerRole;
    private UUID feedbackReportId;
    private String previousStatus;
    private String newStatus;
    private ReviewAction reviewAction;
    private String staffNotesInternal;
    private String feedbackToSeller;
    private String rejectionReason;
    private List<RequiredChangeResponse> requiredChanges;
    private Map<String, Boolean> checklistResults;
    private Boolean isResubmission;
    private UUID previousReviewId;
    private Integer reviewVersion;
    private OffsetDateTime reviewedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequiredChangeResponse {
        private String field;
        private String issue;
        private String suggestion;
    }
}
