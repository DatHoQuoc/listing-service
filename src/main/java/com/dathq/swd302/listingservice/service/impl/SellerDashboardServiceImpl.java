package com.dathq.swd302.listingservice.service.impl;

import com.dathq.swd302.listingservice.dto.SellerDashboardDtos.DashboardSummaryResponse;
import com.dathq.swd302.listingservice.dto.SellerDashboardDtos.PortfolioViewsSeriesItem;
import com.dathq.swd302.listingservice.dto.SellerDashboardDtos.PortfolioViewsSeriesResponse;
import com.dathq.swd302.listingservice.dto.SellerDashboardDtos.TopPerformerItem;
import com.dathq.swd302.listingservice.dto.SellerDashboardDtos.TopPerformersResponse;
import com.dathq.swd302.listingservice.model.enums.ListingStatus;
import com.dathq.swd302.listingservice.model.enums.ViewAccuracy;
import com.dathq.swd302.listingservice.repository.ListingRepository;
import com.dathq.swd302.listingservice.repository.ListingViewRepository;
import com.dathq.swd302.listingservice.service.SellerDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerDashboardServiceImpl implements SellerDashboardService {

    private static final int DEFAULT_TOP_LIMIT = 5;
    private static final int MAX_TOP_LIMIT = 50;
    private static final Pattern RANGE_PATTERN = Pattern.compile("^(\\d+)([dhDH])$");

    private static final Map<ViewAccuracy, String> TRUNC_MAP = Map.of(
            ViewAccuracy.MONTH, "month",
            ViewAccuracy.DAY, "day",
            ViewAccuracy.HOUR, "hour"
    );

    private final ListingRepository listingRepository;
    private final ListingViewRepository listingViewRepository;

    @Override
    public DashboardSummaryResponse getSummary(UUID userId) {
        long published = listingRepository.countByUserIdAndStatus(userId, ListingStatus.PUBLISHED);
        long pendingReview = listingRepository.countByUserIdAndStatus(userId, ListingStatus.PENDING_REVIEW);
        long rejected = listingRepository.countByUserIdAndStatus(userId, ListingStatus.REJECTED);
        long draft = listingRepository.countByUserIdAndStatus(userId, ListingStatus.DRAFT);
        long totalListings = published + pendingReview + rejected + draft;

        Instant to = Instant.now();
        Instant from = to.minusSeconds(7L * 24 * 60 * 60);
        long totalViews7d = listingViewRepository.countOwnerViewsBetween(userId, from, to);

        return new DashboardSummaryResponse(totalListings, published, pendingReview, rejected, draft, totalViews7d);
    }

    @Override
    public PortfolioViewsSeriesResponse getViewsSeries(UUID userId, Instant from, Instant to, ViewAccuracy accuracy) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("from and to are required");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("from must be less than or equal to to");
        }

        List<Object[]> rows = accuracy == ViewAccuracy.QUARTER_HOUR
                ? listingViewRepository.aggregateOwnerByQuarterHour(userId, from, to)
                : listingViewRepository.aggregateOwnerByTrunc(userId, TRUNC_MAP.get(accuracy), from, to);

        List<PortfolioViewsSeriesItem> data = rows.stream()
                .map(row -> new PortfolioViewsSeriesItem(
                        toInstant(row[0]),
                        ((Number) row[1]).longValue()
                ))
                .toList();

        return new PortfolioViewsSeriesResponse(from, to, accuracy, data);
    }

    @Override
    public TopPerformersResponse getTopPerformers(UUID userId, String range, int limit) {
        String effectiveRange = (range == null || range.isBlank()) ? "7d" : range.toLowerCase();
        int effectiveLimit = normalizeLimit(limit, DEFAULT_TOP_LIMIT, MAX_TOP_LIMIT);

        Instant to = Instant.now();
        Instant from = parseRangeStart(effectiveRange, to);

        List<TopPerformerItem> items = listingViewRepository.findTopPerformers(userId, from, to, effectiveLimit)
                .stream()
                .map(row -> new TopPerformerItem(
                        UUID.fromString(row[0].toString()),
                        row[1] == null ? "" : row[1].toString(),
                        ((Number) row[2]).longValue()
                ))
                .toList();

        return new TopPerformersResponse(effectiveRange, items);
    }

    private static int normalizeLimit(int limit, int defaultValue, int maxValue) {
        int requested = limit <= 0 ? defaultValue : limit;
        return Math.min(requested, maxValue);
    }

    private static Instant parseRangeStart(String range, Instant to) {
        Matcher matcher = RANGE_PATTERN.matcher(range);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("range must match Nd or Nh, e.g. 7d");
        }

        long amount = Long.parseLong(matcher.group(1));
        String unit = matcher.group(2).toLowerCase();
        if ("d".equals(unit)) {
            return to.minusSeconds(amount * 24 * 60 * 60);
        }
        return to.minusSeconds(amount * 60 * 60);
    }

    private static Instant toInstant(Object value) {
        if (value instanceof Instant instant) {
            return instant;
        }
        if (value instanceof Timestamp timestamp) {
            return timestamp.toInstant();
        }
        if (value instanceof OffsetDateTime offsetDateTime) {
            return offsetDateTime.toInstant();
        }
        throw new IllegalStateException("Unsupported time type: " + value.getClass().getName());
    }
}
