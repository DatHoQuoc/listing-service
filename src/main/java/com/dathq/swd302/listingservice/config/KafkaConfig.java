package com.dathq.swd302.listingservice.config;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka Configuration Class
 * Defines Kafka topics and their configurations
 */
@Configuration
public class KafkaConfig {
    // Topic names as constants
    public static final String LISTING_CREATED_TOPIC = "listing-created";
    public static final String LISTING_UPDATED_TOPIC = "listing-updated";
    public static final String LISTING_DELETED_TOPIC = "listing-deleted";
    public static final String LISTING_NOTIFICATION_TOPIC = "listing-notification";

    /**
     * Create listing-created topic
     * Partitions: 3 for parallel processing
     * Replicas: 1 (increase in production with multiple brokers)
     */
    @Bean
    public NewTopic listingCreatedTopic() {
        return TopicBuilder.name(LISTING_CREATED_TOPIC)
                .partitions(3)
                .replicas(1)
                .compact()
                .build();
    }

    /**
     * Create listing-updated topic
     */
    @Bean
    public NewTopic listingUpdatedTopic() {
        return TopicBuilder.name(LISTING_UPDATED_TOPIC)
                .partitions(3)
                .replicas(1)
                .compact()
                .build();
    }

    /**
     * Create listing-deleted topic
     */
    @Bean
    public NewTopic listingDeletedTopic() {
        return TopicBuilder.name(LISTING_DELETED_TOPIC)
                .partitions(3)
                .replicas(1)
                .compact()
                .build();
    }

    /**
     * Create listing-notification topic
     */
    @Bean
    public NewTopic listingNotificationTopic() {
        return TopicBuilder.name(LISTING_NOTIFICATION_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
