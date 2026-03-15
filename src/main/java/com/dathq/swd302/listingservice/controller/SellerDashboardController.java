package com.dathq.swd302.listingservice.controller;

import com.dathq.swd302.listingservice.dto.SellerDashboardDtos.DashboardSummaryResponse;
import com.dathq.swd302.listingservice.dto.SellerDashboardDtos.PortfolioViewsSeriesResponse;
import com.dathq.swd302.listingservice.dto.SellerDashboardDtos.TopPerformersResponse;
import com.dathq.swd302.listingservice.model.enums.ViewAccuracy;
import com.dathq.swd302.listingservice.security.JwtClaims;
import com.dathq.swd302.listingservice.security.JwtUser;
import com.dathq.swd302.listingservice.service.SellerDashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/seller/dashboard")
@RequiredArgsConstructor
@Tag(name = "Seller Dashboard", description = "Dashboard analytics for seller")
public class SellerDashboardController {

    private final SellerDashboardService sellerDashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary(@JwtUser JwtClaims claims) {
        return ResponseEntity.ok(sellerDashboardService.getSummary(claims.getUserId()));
    }

    @GetMapping("/views-series")
    public ResponseEntity<PortfolioViewsSeriesResponse> getViewsSeries(
            @JwtUser JwtClaims claims,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant from,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant to,
            @RequestParam(defaultValue = "DAY") ViewAccuracy accuracy
    ) {
        return ResponseEntity.ok(sellerDashboardService.getViewsSeries(claims.getUserId(), from, to, accuracy));
    }

    @GetMapping("/top-performers")
    public ResponseEntity<TopPerformersResponse> getTopPerformers(
            @JwtUser JwtClaims claims,
            @RequestParam(defaultValue = "7d") String range,
            @RequestParam(defaultValue = "5") int limit
    ) {
        return ResponseEntity.ok(sellerDashboardService.getTopPerformers(claims.getUserId(), range, limit));
    }
}
