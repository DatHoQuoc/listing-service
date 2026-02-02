package com.dathq.swd302.listingservice.service.impl;
import com.dathq.swd302.listingservice.config.KafkaConfig;
import com.dathq.swd302.listingservice.dto.ListingEventDto;
import com.dathq.swd302.listingservice.exception.KafkaEventProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * Kafka Consumer Service
 * Listens to Kafka topics and processes events
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    /**
     * Listen to listing-created events
     */
    @KafkaListener(
            topics = KafkaConfig.LISTING_CREATED_TOPIC,
            groupId = "listing-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeListingCreatedEvent(
            @Payload ListingEventDto event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("Received CREATED event: eventId={}, listingId={}, topic={}, partition={}, offset={}",
                event.getEventId(), event.getListingId(), topic, partition, offset);

        processListingCreated(event);
    }

    /**
     * Listen to listing-updated events
     */
    @KafkaListener(
            topics = KafkaConfig.LISTING_UPDATED_TOPIC,
            groupId = "listing-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeListingUpdatedEvent(
            @Payload ListingEventDto event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("Received UPDATED event: eventId={}, listingId={}, topic={}, partition={}, offset={}",
                event.getEventId(), event.getListingId(), topic, partition, offset);

        processListingUpdated(event);
    }

    /**
     * Listen to listing-deleted events
     */
    @KafkaListener(
            topics = KafkaConfig.LISTING_DELETED_TOPIC,
            groupId = "listing-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeListingDeletedEvent(
            @Payload ListingEventDto event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("Received DELETED event: eventId={}, listingId={}, topic={}, partition={}, offset={}",
                event.getEventId(), event.getListingId(), topic, partition, offset);

        processListingDeleted(event);
    }

    /**
     * Listen to notification events
     */
    @KafkaListener(
            topics = KafkaConfig.LISTING_NOTIFICATION_TOPIC,
            groupId = "notification-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeNotificationEvent(
            @Payload ListingEventDto event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("Received NOTIFICATION event: eventId={}, listingId={}, topic={}, partition={}, offset={}",
                event.getEventId(), event.getListingId(), topic, partition, offset);

        processNotification(event);
    }

    /**
     * Process listing created event
     */
    private void processListingCreated(ListingEventDto event) {
        validateEvent(event);

        // Implement your business logic here
        // Examples:
        // - Update search index (Elasticsearch)
        // - Send welcome email to listing owner
        // - Update analytics
        // - Trigger notifications to interested users

        log.info("Successfully processed created listing: title={}, listingId={}",
                event.getTitle(), event.getListingId());
    }

    /**
     * Process listing updated event
     */
    private void processListingUpdated(ListingEventDto event) {
        validateEvent(event);

        // Implement your business logic here
        // Examples:
        // - Update search index
        // - Notify watchers of changes
        // - Update cached data

        log.info("Successfully processed updated listing: title={}, listingId={}",
                event.getTitle(), event.getListingId());
    }

    /**
     * Process listing deleted event
     */
    private void processListingDeleted(ListingEventDto event) {
        validateEvent(event);

        // Implement your business logic here
        // Examples:
        // - Remove from search index
        // - Archive data
        // - Notify interested parties

        log.info("Successfully processed deleted listing: listingId={}", event.getListingId());
    }

    /**
     * Process notification event
     */
    private void processNotification(ListingEventDto event) {
        validateEvent(event);

        // Implement your business logic here
        // Examples:
        // - Send email notifications
        // - Send push notifications
        // - Send SMS

        log.info("Successfully processed notification for listing: title={}, listingId={}",
                event.getTitle(), event.getListingId());
    }

    /**
     * Validate event data
     */
    private void validateEvent(ListingEventDto event) {
        if (event == null) {
            throw new KafkaEventProcessingException("Event cannot be null");
        }

        if (event.getEventId() == null || event.getEventId().isEmpty()) {
            throw new KafkaEventProcessingException("Event ID cannot be null or empty");
        }

        if (event.getListingId() == null) {
            throw new KafkaEventProcessingException("Listing ID cannot be null");
        }

        if (event.getEventType() == null || event.getEventType().isEmpty()) {
            throw new KafkaEventProcessingException("Event type cannot be null or empty");
        }
    }
}
