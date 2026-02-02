package com.dathq.swd302.listingservice.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event DTO for Kafka messages
 * Represents listing events sent through Kafka topics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingEventDto {
    private String eventId;
    private String eventType; // CREATED, UPDATED, DELETED
    private Long listingId;
    private String title;
    private String description;
    private Double price;
    private String location;
    private String status; // ACTIVE, INACTIVE, SOLD
    private String userId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventTimestamp;

    private String metadata; // Additional information as JSON string
}
