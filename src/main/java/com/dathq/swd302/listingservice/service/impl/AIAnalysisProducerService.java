package com.dathq.swd302.listingservice.service.impl;
import com.dathq.swd302.listingservice.config.KafkaConfig;
import com.dathq.swd302.listingservice.dto.AIAnalysisEventDto;
import com.dathq.swd302.listingservice.dto.response.DocumentResponse;
import com.dathq.swd302.listingservice.exception.KafkaEventException;
import com.dathq.swd302.listingservice.model.Listing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIAnalysisProducerService {
    private final KafkaTemplate<String, AIAnalysisEventDto> aiAnalysisKafkaTemplate;
    private final MinIOStorageService minIOStorageService;
    private final DocumentServiceImpl documentServiceImpl;
    /**
     * Send AI analysis request for content moderation
     */
//    public void sendContentModerationRequest(Listing listing) {
//        AIAnalysisEventDto event = buildAnalysisEvent(listing, "CONTENT_MODERATION", 1);
//        sendAnalysisRequest(event);
//    }

    /**
     * Send AI analysis request for fraud detection
     */
//    public void sendFraudDetectionRequest(Listing listing) {
//        AIAnalysisEventDto event = buildAnalysisEvent(listing, "FRAUD_DETECTION", 1);
//        sendAnalysisRequest(event);
//    }

    /**
     * Send AI analysis request for category suggestion
     */
//    public void sendCategorySuggestionRequest(Listing listing) {
//        AIAnalysisEventDto event = buildAnalysisEvent(listing, "CATEGORY_SUGGESTION", 3);
//        sendAnalysisRequest(event);
//    }

    /**
     * Send comprehensive analysis (all types)
     */
    public void sendComprehensiveAnalysisRequest(UUID userId,Listing listing) {
        AIAnalysisEventDto event = buildAnalysisEvent(userId,listing, "COMPREHENSIVE", 1);
        sendAnalysisRequest(event);
    }

    /**
     * Build AI analysis event from listing
     */
    private AIAnalysisEventDto buildAnalysisEvent(UUID userID, Listing listing, String analysisType, Integer priority) {
        UUID listingId = listing.getListingId();

        // Fetch and map document URLs
        List<DocumentResponse> documents = documentServiceImpl.getListingDocuments(userID, listingId);

        List<String> downloadUrls = documents.stream()
                .map(doc -> documentServiceImpl.generateDownloadUrl(userID, listingId, doc.getDocumentId()))
                .collect(Collectors.toList());
        List<String> documentTypes = documents.stream()
                .map(doc -> doc.getDocumentType().toString())
                .collect(Collectors.toList());

        return AIAnalysisEventDto.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("AI_ANALYSIS_REQUEST")
                .eventTimestamp(LocalDateTime.now())
                .listingId(listingId)
                .userId(listing.getUserId())
                .title(listing.getTitle())
                .description(listing.getDescription())
                .category(listing.getListingType() != null ? listing.getListingType().name() : null)
                .fileUrl(downloadUrls)
                .documentType(documentTypes)
                .analysisType(analysisType)
                .priority(priority)
                .build();
    }

    /**
     * Send analysis request to Kafka
     */
    private void sendAnalysisRequest(AIAnalysisEventDto event) {
        CompletableFuture<SendResult<String, AIAnalysisEventDto>> future =
                aiAnalysisKafkaTemplate.send(
                        KafkaConfig.AI_ANALYSIS_REQUEST_TOPIC,
                        event.getListingId().toString(),
                        event
                );

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent AI analysis request: eventId={}, listingId={}, type={}, offset={}",
                        event.getEventId(),
                        event.getListingId(),
                        event.getAnalysisType(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send AI analysis request: eventId={}, listingId={}, type={}, error={}",
                        event.getEventId(),
                        event.getListingId(),
                        event.getAnalysisType(),
                        ex.getMessage());
                throw new KafkaEventException("Failed to send AI analysis request", ex);
            }
        });
    }
}
