package com.dathq.swd302.listingservice.dto;

import com.dathq.swd302.listingservice.model.enums.ListingStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class SellerListingFeedDtos {

    public record NeedsAttentionItem(
            UUID listingId,
            String title,
            ListingStatus status,
            String reason,
            OffsetDateTime updatedAt
    ) {
    }

    public record NeedsAttentionResponse(
            List<NeedsAttentionItem> items
    ) {
    }

    public record RecentListingItem(
            UUID listingId,
            String title,
            ListingStatus status,
            int views,
            OffsetDateTime updatedAt
    ) {
    }

    public record RecentListingActivityResponse(
            List<RecentListingItem> items
    ) {
    }

    public record BulkSubmitFailureItem(
            UUID listingId,
            String error
    ) {
    }

    public record BulkSubmitResponse(
            List<UUID> submitted,
            List<BulkSubmitFailureItem> failed
    ) {
    }
}
