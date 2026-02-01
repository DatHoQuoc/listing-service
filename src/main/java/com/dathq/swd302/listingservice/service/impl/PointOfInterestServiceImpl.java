package com.dathq.swd302.listingservice.service.impl;
import com.dathq.swd302.listingservice.dto.request.CreatePOIRequest;
import com.dathq.swd302.listingservice.dto.response.POIResponse;
import com.dathq.swd302.listingservice.exception.ListingNotFoundException;
import com.dathq.swd302.listingservice.exception.UnauthorizedException;
import com.dathq.swd302.listingservice.mapper.POIMapper;
import com.dathq.swd302.listingservice.model.Listing;
import com.dathq.swd302.listingservice.model.PointOfInterest;
import com.dathq.swd302.listingservice.repository.ListingRepository;
import com.dathq.swd302.listingservice.repository.PointOfInterestRepository;
import com.dathq.swd302.listingservice.service.PointOfInterestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PointOfInterestServiceImpl implements PointOfInterestService {
    private final PointOfInterestRepository poiRepository;
    private final ListingRepository listingRepository;
    private final POIMapper poiMapper;

    @Override
    public POIResponse addPointOfInterest(UUID userId, UUID listingId, CreatePOIRequest request) {
        log.info("Adding POI to listing: {}", listingId);

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }

        PointOfInterest poi = new PointOfInterest();
        poi.setListing(listing);
        poi.setName(request.getName());
        poi.setCategory(request.getCategory());
        poi.setDistanceMeters(request.getDistanceMeters());

        if (request.getLatitude() != null && request.getLongitude() != null) {
            GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
            Coordinate coordinate = new Coordinate(request.getLatitude(), request.getLongitude());
            Point point = geometryFactory.createPoint(coordinate);
            listing.setGeolocation(point);
        }

        poi.setCreatedAt(OffsetDateTime.now());

        PointOfInterest savedPOI = poiRepository.save(poi);

        log.info("POI added: {}", savedPOI.getPoiId());

        return poiMapper.toResponse(savedPOI);
    }

    @Override
    public List<POIResponse> addPointsOfInterest(UUID userId, UUID listingId, List<CreatePOIRequest> requests) {
        log.info("Adding {} POIs to listing: {}", requests.size(), listingId);

        return requests.stream()
                .map(request -> addPointOfInterest(userId, listingId, request))
                .collect(Collectors.toList());
    }

    @Override
    public List<POIResponse> getListingPOIs(UUID listingId) {
        log.info("Fetching POIs for listing: {}", listingId);

        List<PointOfInterest> pois = poiRepository.findByListing_ListingIdOrderByDistanceMeters(listingId);
        return poiMapper.toResponseList(pois);
    }

    @Override
    public List<POIResponse> getListingPOIsByCategory(UUID listingId, String category) {
        log.info("Fetching POIs for listing: {} with category: {}", listingId, category);

        List<PointOfInterest> pois = poiRepository.findByListing_ListingIdAndCategoryOrderByDistanceMeters(listingId, category);
        return poiMapper.toResponseList(pois);
    }

    @Override
    public POIResponse updatePointOfInterest(UUID userId, UUID listingId, UUID poiId, CreatePOIRequest request) {
        log.info("Updating POI: {}", poiId);

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }

        PointOfInterest poi = poiRepository.findById(poiId)
                .orElseThrow(() -> new RuntimeException("POI not found with id: " + poiId));

        if (!poi.getListing().getListingId().equals(listingId)) {
            throw new IllegalArgumentException("POI does not belong to this listing");
        }

        poi.setName(request.getName());
        poi.setCategory(request.getCategory());
        poi.setDistanceMeters(request.getDistanceMeters());
        Double latitude = request.getLatitude();
        Double longitude = request.getLongitude();
        if (latitude != null && longitude != null) {
            GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
            Coordinate coordinate = new Coordinate(latitude, longitude);
            Point point = geometryFactory.createPoint(coordinate);
            listing.setGeolocation(point);
        }

        PointOfInterest updatedPOI = poiRepository.save(poi);

        log.info("POI updated: {}", poiId);

        return poiMapper.toResponse(updatedPOI);
    }

    @Override
    public void deletePointOfInterest(UUID userId, UUID listingId, UUID poiId) {
        log.info("Deleting POI: {}", poiId);

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }

        PointOfInterest poi = poiRepository.findById(poiId)
                .orElseThrow(() -> new RuntimeException("POI not found with id: " + poiId));

        if (!poi.getListing().getListingId().equals(listingId)) {
            throw new IllegalArgumentException("POI does not belong to this listing");
        }

        poiRepository.delete(poi);

        log.info("POI deleted: {}", poiId);
    }

    @Override
    public void deleteAllPOIs(UUID userId, UUID listingId) {
        log.info("Deleting all POIs for listing: {}", listingId);

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }

        poiRepository.deleteByListing_ListingId(listingId);

        log.info("All POIs deleted for listing: {}", listingId);
    }
}
