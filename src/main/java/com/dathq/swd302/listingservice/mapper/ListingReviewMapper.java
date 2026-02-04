package com.dathq.swd302.listingservice.mapper;
import com.dathq.swd302.listingservice.dto.response.ListingReviewResponse;
import com.dathq.swd302.listingservice.model.ListingReview;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public abstract  class ListingReviewMapper {
    @Autowired
    protected ObjectMapper objectMapper;

    @Mapping(target = "requiredChanges", expression = "java(parseRequiredChanges(review.getRequiredChangesJson()))")
    @Mapping(target = "checklistResults", expression = "java(parseChecklistResults(review.getChecklistResultsJson()))")
    public abstract ListingReviewResponse toResponse(ListingReview review);


    public ListingReviewResponse mapToResponse(ListingReview review) {
        ListingReviewResponse.ListingReviewResponseBuilder listingReviewResponse = ListingReviewResponse.builder();

        // Pass the JsonNodes directly to the updated parse methods
        listingReviewResponse.requiredChanges(parseRequiredChanges(review.getRequiredChangesJson()));
        listingReviewResponse.checklistResults(parseChecklistResults(review.getChecklistResultsJson()));

        return listingReviewResponse.build();
    }
    protected List<ListingReviewResponse.RequiredChangeResponse> parseRequiredChanges(JsonNode jsonNode) {
        if (jsonNode == null || jsonNode.isNull()) {
            return null;
        }
        try {
            // convertValue handles the transition from JsonNode to your List automatically
            return objectMapper.convertValue(
                    jsonNode,
                    new TypeReference<List<ListingReviewResponse.RequiredChangeResponse>>() {}
            );
        } catch (Exception e) {
            return null;
        }
    }

    protected Map<String, Boolean> parseChecklistResults(JsonNode jsonNode) {
        if (jsonNode == null || jsonNode.isNull()) {
            return null;
        }
        try {
            return objectMapper.convertValue(
                    jsonNode,
                    new TypeReference<Map<String, Boolean>>() {}
            );
        } catch (Exception e) {
            return null;
        }
    }
}
