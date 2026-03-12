package com.dathq.swd302.listingservice.service.impl;

import com.dathq.swd302.listingservice.config.KafkaConfig;
import com.dathq.swd302.listingservice.dto.ListingKafkaEvent;
import com.dathq.swd302.listingservice.mapper.ListingEventMapper;
import com.dathq.swd302.listingservice.model.Listing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListingUpdatedProducerService {
    private final KafkaTemplate<String, ListingKafkaEvent> kafkaTemplate;

    public void sendListingUpdated(Listing listing) {

        ListingKafkaEvent event = ListingEventMapper.toKafkaEvent(listing);

        kafkaTemplate.send(KafkaConfig.LISTING_PUBLISH_TOPIC, listing.getListingId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish ListingUpdatedEvent for listingId: {}",
                                listing.getListingId(), ex);
                    } else {
                        log.info("Published ListingUpdatedEvent for listingId: {}",
                                listing.getListingId());
                    }
                });
    }
}
