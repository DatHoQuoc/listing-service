package com.dathq.swd302.listingservice.service.impl;
import com.dathq.swd302.listingservice.model.ListingView;
import com.dathq.swd302.listingservice.model.enums.ViewAccuracy;
import com.dathq.swd302.listingservice.repository.ListingRepository;
import com.dathq.swd302.listingservice.repository.ListingViewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dathq.swd302.listingservice.dto.AnalyticsDtos.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ListingViewRepository viewRepository;
    private final ListingRepository listingRepository;

    // Maps ViewAccuracy → PostgreSQL date_trunc unit
    private static final Map<ViewAccuracy, String> TRUNC_MAP = Map.of(
            ViewAccuracy.MONTH, "month",
            ViewAccuracy.DAY,   "day",
            ViewAccuracy.HOUR,  "hour"
    );

    // ── Record a view ─────────────────────────────────────────────────────────

    @Transactional
    public void recordView(UUID listingId) {
        // Verify listing exists — throws EntityNotFoundException if not found
        if (!listingRepository.existsById(listingId)) {
            throw new EntityNotFoundException("Listing not found: " + listingId);
        }

        // getReferenceById returns a proxy — no extra SELECT needed
        viewRepository.save(
                ListingView.builder()
                        .listing(listingRepository.getReferenceById(listingId))
                        .build()
        );

        // Keep denormalized counter in sync for fast reads (e.g. listing cards)
        listingRepository.incrementViewCount(listingId);
    }

    // ── Query view stats ──────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public ViewStatsResponse getViewStats(
            UUID         listingId,
            Instant      from,
            Instant      to,
            ViewAccuracy accuracy
    ) {
        if (!listingRepository.existsById(listingId)) {
            throw new EntityNotFoundException("Listing not found: " + listingId);
        }

        List<Object[]> rows = accuracy == ViewAccuracy.QUARTER_HOUR
                ? viewRepository.aggregateByQuarterHour(listingId, from, to)
                : viewRepository.aggregateByTrunc(listingId, TRUNC_MAP.get(accuracy), from, to);

        List<ViewBucket> buckets = rows.stream()
                .map(row -> new ViewBucket(
                        ((java.sql.Timestamp) row[0]).toInstant(),
                        ((Number) row[1]).longValue()
                ))
                .toList();

        long total = buckets.stream().mapToLong(ViewBucket::views).sum();

        return new ViewStatsResponse(listingId, accuracy, from, to, total, buckets);
    }
}
