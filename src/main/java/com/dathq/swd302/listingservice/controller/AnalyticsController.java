package com.dathq.swd302.listingservice.controller;


import com.dathq.swd302.listingservice.model.enums.ViewAccuracy;
import com.dathq.swd302.listingservice.service.impl.AnalyticsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.dathq.swd302.listingservice.dto.AnalyticsDtos.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /**
     * POST /analytics/listings/view
     * Body: { "listingId": "uuid" }
     *
     * Records a single view event for a listing.
     * Also increments the denormalized viewCount on the Listing entity.
     */
    @PostMapping("/listings/view")
    public ResponseEntity<Void> recordView(@Valid @RequestBody RecordViewRequest request) {
        analyticsService.recordView(request.listingId());
        return ResponseEntity.noContent().build(); // 204
    }

    /**
     * GET /analytics/listings/{listingId}/views
     * Query params:
     *   from     - ISO-8601 timestamp, e.g. 2025-01-01T00:00:00Z  (optional)
     *   to       - ISO-8601 timestamp, e.g. 2025-03-01T00:00:00Z  (optional)
     *   accuracy - MONTH | DAY | HOUR | QUARTER_HOUR              (default: DAY)
     *
     * Returns time-bucketed view counts.
     */
    @GetMapping("/listings/{listingId}/views")
    public ResponseEntity<ViewStatsResponse> getViews(
            @PathVariable UUID listingId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant to,

            @RequestParam(defaultValue = "DAY")
            ViewAccuracy accuracy
    ) {
        ViewStatsResponse stats = analyticsService.getViewStats(listingId, from, to, accuracy);
        return ResponseEntity.ok(stats);
    }
}
