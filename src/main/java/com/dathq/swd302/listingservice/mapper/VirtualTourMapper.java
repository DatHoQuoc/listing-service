package com.dathq.swd302.listingservice.mapper;

import com.dathq.swd302.listingservice.dto.response.VirtualTourResponse;
import com.dathq.swd302.listingservice.dto.response.TourSceneResponse;
import com.dathq.swd302.listingservice.model.VirtualTour;
import com.dathq.swd302.listingservice.model.TourScene;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface VirtualTourMapper {

    @Mapping(source = "listing.listingId", target = "listingId")
    @Mapping(target = "scenes", ignore = true)
    VirtualTourResponse toResponse(VirtualTour virtualTour);

    @Mapping(source = "virtualTour.tourId", target = "tourId")
    TourSceneResponse toSceneResponse(TourScene tourScene);

    List<TourSceneResponse> toSceneResponseList(List<TourScene> tourScenes);
}
