package com.dathq.swd302.listingservice.repository;
import com.dathq.swd302.listingservice.model.ListingView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ListingViewRepository extends JpaRepository<ListingView, UUID>{

    @Query(value = """
        SELECT
            date_trunc(:truncUnit, viewed_at) AS bucket,
            COUNT(*)                          AS views
        FROM listing_views
        WHERE listing_id = :listingId
          AND (:from IS NULL OR viewed_at >= CAST(:from AS timestamptz))
          AND (:to   IS NULL OR viewed_at <= CAST(:to   AS timestamptz))
        GROUP BY bucket
        ORDER BY bucket ASC
        """, nativeQuery = true)
    List<Object[]> aggregateByTrunc(
            @Param("listingId") UUID listingId,
            @Param("truncUnit") String truncUnit,
            @Param("from")      Instant from,
            @Param("to")        Instant to
    );

    // ── Aggregation query for QUARTER_HOUR (15-min buckets) ───────────────────
    @Query(value = """
        SELECT
            date_trunc('hour', viewed_at)
              + INTERVAL '15 min'
                * FLOOR(EXTRACT(MINUTE FROM viewed_at) / 15) AS bucket,
            COUNT(*)                                          AS views
        FROM listing_views
        WHERE listing_id = :listingId
          AND (:from IS NULL OR viewed_at >= CAST(:from AS timestamptz))
          AND (:to   IS NULL OR viewed_at <= CAST(:to   AS timestamptz))
        GROUP BY bucket
        ORDER BY bucket ASC
        """, nativeQuery = true)
    List<Object[]> aggregateByQuarterHour(
            @Param("listingId") UUID listingId,
            @Param("from")      Instant from,
            @Param("to")        Instant to
    );
}
