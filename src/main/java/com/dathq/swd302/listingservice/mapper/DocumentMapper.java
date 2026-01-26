package com.dathq.swd302.listingservice.mapper;

import com.dathq.swd302.listingservice.dto.response.DocumentResponse;
import com.dathq.swd302.listingservice.model.LegalDocument;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface DocumentMapper {
    @Mapping(source = "listing.listingId", target = "listingId")
    DocumentResponse toResponse(LegalDocument document);

    List<DocumentResponse> toResponseList(List<LegalDocument> documents);
}
