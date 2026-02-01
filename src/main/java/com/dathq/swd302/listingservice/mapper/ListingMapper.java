package com.dathq.swd302.listingservice.mapper;


import com.dathq.swd302.listingservice.dto.request.CreateListingRequest;
import com.dathq.swd302.listingservice.dto.request.UpdateListingRequest;
import com.dathq.swd302.listingservice.dto.response.ListingDetailResponse;
import com.dathq.swd302.listingservice.dto.response.ListingResponse;
import com.dathq.swd302.listingservice.model.Listing;
import com.dathq.swd302.listingservice.model.enums.ListingType;
import com.dathq.swd302.listingservice.model.enums.PropertyType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ListingMapper {
    @Mapping(target = "listingId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "freePost", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "saveCount", ignore = true)
    @Mapping(target = "contactCount", ignore = true)
    @Mapping(target = "creditsLocked", ignore = true)
    @Mapping(target = "creditsCharged", ignore = true)
    @Mapping(target = "creditsRefunded", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "adminNotes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "submittedAt", ignore = true)
    @Mapping(target = "reviewedAt", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "expiredAt", ignore = true)
    @Mapping(target = "listingType", source = "listingType")
    @Mapping(target = "propertyType", source = "propertyType")
    @Mapping(target = "ward.wardId", source = "wardId")
    @Mapping(target = "province.provinceId", source = "provinceId")
    @Mapping(target = "country.countryId", source = "countryId")
    Listing toEntity(CreateListingRequest request);

    ListingResponse toResponse(Listing listing);

    ListingDetailResponse toDetailResponse(Listing listing);

    List<ListingResponse> toResponseList(List<Listing> listings);

    @Mapping(target = "listingId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "freePost", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "saveCount", ignore = true)
    @Mapping(target = "contactCount", ignore = true)
    @Mapping(target = "creditsLocked", ignore = true)
    @Mapping(target = "creditsCharged", ignore = true)
    @Mapping(target = "creditsRefunded", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "adminNotes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "submittedAt", ignore = true)
    @Mapping(target = "reviewedAt", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "expiredAt", ignore = true)
    void updateEntityFromRequest(UpdateListingRequest request, @MappingTarget Listing listing);

    default ListingType mapListingType(String type) {
        return type != null ? ListingType.valueOf(type.toUpperCase()) : null;
    }

    default PropertyType mapPropertyType(String type) {
        return type != null ? PropertyType.valueOf(type.toUpperCase()) : null;
    }
}
