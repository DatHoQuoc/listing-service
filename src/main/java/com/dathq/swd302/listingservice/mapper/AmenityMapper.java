package com.dathq.swd302.listingservice.mapper;

import com.dathq.swd302.listingservice.dto.response.AmenityResponse;
import com.dathq.swd302.listingservice.model.Amenity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AmenityMapper {
    AmenityResponse toResponse(Amenity amenity);

    List<AmenityResponse> toResponseList(List<Amenity> amenities);
}
