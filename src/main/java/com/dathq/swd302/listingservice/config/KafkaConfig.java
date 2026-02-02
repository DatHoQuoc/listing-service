package com.dathq.swd302.listingservice.config;
import com.dathq.swd302.listingservice.dto.AIAnalysisEventDto;
import com.dathq.swd302.listingservice.dto.ListingEventDto;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import java.util.HashMap;
import java.util.Map;

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
    public static final String AI_ANALYSIS_REQUEST_TOPIC = "new_listing";
    public static final String AI_ANALYSIS_RESPONSE_TOPIC = "ai-analysis-response";


    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

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

    @Bean
    public NewTopic aiAnalysisRequestTopic() {
        return TopicBuilder.name(AI_ANALYSIS_REQUEST_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * Create AI analysis response topic
     */
    @Bean
    public NewTopic aiAnalysisResponseTopic() {
        return TopicBuilder.name(AI_ANALYSIS_RESPONSE_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public ProducerFactory<String, ListingEventDto> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);


        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, ListingEventDto> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<String, AIAnalysisEventDto> aiAnalysisProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, AIAnalysisEventDto> aiAnalysisKafkaTemplate() {
        return new KafkaTemplate<>(aiAnalysisProducerFactory());
    }
}
