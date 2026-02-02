package com.dathq.swd302.listingservice.dto;
import com.dathq.swd302.listingservice.model.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIAnalysisEventDto {

    private String eventId;
    private String eventType;
    private LocalDateTime eventTimestamp;

    private UUID listingId;
    private UUID userId;
    private String title;
    private String description;
    private String category;
    private String[] images;
    private Double price;
    private List<String> documentType;
    private List<String> fileUrl;

    // AI Analysis specific fields
    private String analysisType; // "CONTENT_MODERATION", "FRAUD_DETECTION", "CATEGORY_SUGGESTION"
    private Integer priority; // 1 (high) to 5 (low)
}
