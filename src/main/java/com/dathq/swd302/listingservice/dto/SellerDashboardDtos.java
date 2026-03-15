package com.dathq.swd302.listingservice.dto;

import com.dathq.swd302.listingservice.model.enums.ViewAccuracy;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class SellerDashboardDtos {

    public record DashboardSummaryResponse(
            long totalListings,
            long published,
            long pendingReview,
            long rejected,
            long draft,
            long totalViews7d
    ) {
    }

    public record PortfolioViewsSeriesItem(
            Instant bucket,
            long views
    ) {
    }

    public record PortfolioViewsSeriesResponse(
            Instant from,
            Instant to,
            ViewAccuracy accuracy,
            List<PortfolioViewsSeriesItem> data
    ) {
    }

    public record TopPerformerItem(
            UUID listingId,
            String title,
            long views
    ) {
    }

    public record TopPerformersResponse(
            String range,
            List<TopPerformerItem> items
    ) {
    }
}
