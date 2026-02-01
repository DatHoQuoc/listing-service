package com.dathq.swd302.listingservice.mapper;
import com.dathq.swd302.listingservice.dto.response.POIResponse;
import com.dathq.swd302.listingservice.model.PointOfInterest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface POIMapper {
    @Mapping(target = "latitude", expression = "java(poi.getGeolocation() != null ? poi.getGeolocation().getY() : null)")
    @Mapping(target = "longitude", expression = "java(poi.getGeolocation() != null ? poi.getGeolocation().getX() : null)")
    POIResponse toResponse(PointOfInterest poi);

    List<POIResponse> toResponseList(List<PointOfInterest> pois);
}
