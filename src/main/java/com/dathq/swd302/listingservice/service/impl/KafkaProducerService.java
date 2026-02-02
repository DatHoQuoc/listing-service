package com.dathq.swd302.listingservice.service.impl;
import com.dathq.swd302.listingservice.config.KafkaConfig;
import com.dathq.swd302.listingservice.dto.ListingEventDto;
import com.dathq.swd302.listingservice.exception.KafkaEventException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Kafka Producer Service
 * Handles sending events to Kafka topics
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, ListingEventDto> kafkaTemplate;

    /**
     * Send listing created event
     */
    public void sendListingCreatedEvent(ListingEventDto event) {
        enrichEvent(event, "CREATED");
        sendEvent(KafkaConfig.LISTING_CREATED_TOPIC, event.getListingId().toString(), event);
    }

    /**
     * Send listing updated event
     */
    public void sendListingUpdatedEvent(ListingEventDto event) {
        enrichEvent(event, "UPDATED");
        sendEvent(KafkaConfig.LISTING_UPDATED_TOPIC, event.getListingId().toString(), event);
    }

    /**
     * Send listing deleted event
     */
    public void sendListingDeletedEvent(ListingEventDto event) {
        enrichEvent(event, "DELETED");
        sendEvent(KafkaConfig.LISTING_DELETED_TOPIC, event.getListingId().toString(), event);
    }

    /**
     * Send notification event
     */
    public void sendNotificationEvent(ListingEventDto event) {
        enrichEvent(event, event.getEventType());
        sendEvent(KafkaConfig.LISTING_NOTIFICATION_TOPIC, event.getListingId().toString(), event);
    }

    /**
     * Enrich event with metadata
     */
    private void enrichEvent(ListingEventDto event, String eventType) {
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType(eventType);
        event.setEventTimestamp(LocalDateTime.now());
    }

    /**
     * Generic method to send events to Kafka
     * Uses async sending with callbacks
     */
    private void sendEvent(String topic, String key, ListingEventDto event) {
        CompletableFuture<SendResult<String, ListingEventDto>> future =
                kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent event=[{}] to topic=[{}] with offset=[{}]",
                        event.getEventId(),
                        topic,
                        result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send event=[{}] to topic=[{}] due to: {}",
                        event.getEventId(),
                        topic,
                        ex.getMessage());
                throw new KafkaEventException("Failed to send event to Kafka topic: " + topic, ex);
            }
        });
    }

    /**
     * Send event synchronously (use with caution - blocks thread)
     */
    public void sendEventSync(String topic, String key, ListingEventDto event) {
        SendResult<String, ListingEventDto> result = kafkaTemplate.send(topic, key, event)
                .exceptionally(ex -> {
                    log.error("Error sending event synchronously: {}", ex.getMessage(), ex);
                    throw new KafkaEventException("Failed to send event synchronously to Kafka", ex);
                })
                .join();

        log.info("Synchronously sent event=[{}] to topic=[{}] with offset=[{}]",
                event.getEventId(),
                topic,
                result.getRecordMetadata().offset());
    }
}
