package com.dathq.swd302.listingservice.dto;
import com.dathq.swd302.listingservice.model.enums.ViewAccuracy;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
public class AnalyticsDtos {
    // ── POST /analytics/listings/view ─────────────────────────────────────────

    public record RecordViewRequest(
            @NotNull(message = "listingId is required")
            UUID listingId
    ) {}

    // ── GET /analytics/listings/{listingId}/views ─────────────────────────────

    public record ViewBucket(
            Instant bucket,
            long    views
    ) {}

    public record ViewStatsResponse(
            UUID           listingId,
            ViewAccuracy accuracy,
            Instant        from,
            Instant        to,
            long           totalViews,
            List<ViewBucket> data
    ) {}
}
