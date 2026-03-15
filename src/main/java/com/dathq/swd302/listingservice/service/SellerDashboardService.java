package com.dathq.swd302.listingservice.service;

import com.dathq.swd302.listingservice.dto.SellerDashboardDtos.DashboardSummaryResponse;
import com.dathq.swd302.listingservice.dto.SellerDashboardDtos.PortfolioViewsSeriesResponse;
import com.dathq.swd302.listingservice.dto.SellerDashboardDtos.TopPerformersResponse;
import com.dathq.swd302.listingservice.model.enums.ViewAccuracy;

import java.time.Instant;
import java.util.UUID;

public interface SellerDashboardService {
    DashboardSummaryResponse getSummary(UUID userId);

    PortfolioViewsSeriesResponse getViewsSeries(UUID userId, Instant from, Instant to, ViewAccuracy accuracy);

    TopPerformersResponse getTopPerformers(UUID userId, String range, int limit);
}
