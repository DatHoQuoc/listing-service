package com.dathq.swd302.listingservice.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "listing_views",
        indexes = {
                @Index(name = "idx_listing_views_lookup", columnList = "listing_id, viewed_at DESC")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListingView {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "listing_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_listing_views_listing",
                    foreignKeyDefinition = "FOREIGN KEY (listing_id) REFERENCES listings(listing_id) ON DELETE CASCADE"
            )
    )
    private Listing listing;

    @CreationTimestamp
    @Column(name = "viewed_at", nullable = false, updatable = false)
    private Instant viewedAt;
}
