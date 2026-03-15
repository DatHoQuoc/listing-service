package com.dathq.swd302.listingservice.service.impl;

import com.dathq.swd302.listingservice.exception.InvalidCoordinateException;
import com.dathq.swd302.listingservice.exception.LocationNotFoundException;
import com.dathq.swd302.listingservice.model.enums.LocationType;
import com.dathq.swd302.listingservice.model.enums.PoiCategory;
import com.dathq.swd302.listingservice.repository.PoiRepository;
import com.dathq.swd302.listingservice.repository.ProvinceRepository;
import com.dathq.swd302.listingservice.repository.WardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dathq.swd302.listingservice.dto.LocationDtos.*;

import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationServiceSpecific {

    private final WardRepository wardRepository;
    private final PoiRepository poiRepository;
    private final ProvinceRepository provinceRepository;

    // Max distance (meters) to still return a result for reverse geocoding
    private static final double MAX_REVERSE_DISTANCE = 5000.0;
    // Confidence drops linearly from 1.0 at 0m to 0.0 at MAX_REVERSE_DISTANCE
    private static final int DEFAULT_POI_RADIUS = 500;

    // ── 1. Reverse Geocoding ──────────────────────────────────────────────────

    public ReverseGeocodeResponse reverse(
            double lat,
            double lng,
            boolean includePois,
            int poiLimit) {
        validateCoordinates(lat, lng);

        Object[] wardRow = wardRepository.findNearestWard(lat, lng).stream()
            .findFirst()
            .orElseThrow(() -> new LocationNotFoundException("No nearby address found for coordinates"));

        double distanceMeters = ((Number) wardRow[wardRow.length - 1]).doubleValue();

        if (distanceMeters > MAX_REVERSE_DISTANCE) {
            throw new LocationNotFoundException("No address found within " + MAX_REVERSE_DISTANCE + "m");
        }

        UUID wardId = UUID.fromString(wardRow[0].toString());
        String wardCode = (String) wardRow[1];
        String wardName = (String) wardRow[3];
        UUID provinceId = UUID.fromString(wardRow[5].toString());

        var province = provinceRepository.findById(provinceId)
            .orElseThrow(() -> new LocationNotFoundException("Province not found for ward " + wardId));
        String provinceName = province.getName();
        String provinceCode = province.getCode();

        var country = province.getCountry();
        UUID countryId = country.getCountryId();
        String countryName = country.getName();
        String countryCode = country.getCode();

        String normalizedAddress = String.format("%s, %s, %s", wardName, provinceName, countryName);
        double confidence = Math.max(0.0, 1.0 - distanceMeters / MAX_REVERSE_DISTANCE);

        List<PoiRef> pois = includePois
                ? buildPoiRefs(lat, lng, DEFAULT_POI_RADIUS, poiLimit)
                : List.of();

        return new ReverseGeocodeResponse(
                new CoordinateDto(lat, lng),
                normalizedAddress,
                null, // streetAddress — requires external geocoding API
                new WardRef(wardId, wardName, wardCode),
                new ProvinceRef(provinceId, provinceName, provinceCode),
                new CountryRef(countryId, countryName, countryCode),
                new CoordinateDto(lat, lng),
                distanceMeters,
                Math.round(confidence * 100.0) / 100.0,
                pois);
    }

    // ── 2. Nearby Lookup ──────────────────────────────────────────────────────

    public NearbyResponse nearby(
            double lat,
            double lng,
            int radiusMeters,
            int limit,
            Set<LocationType> types) {
        validateCoordinates(lat, lng);

        List<NearbyItem> items = new ArrayList<>();

        if (types.contains(LocationType.WARD)) {
            wardRepository.findWardsNearby(lat, lng, radiusMeters, limit)
                    .stream()
                    .map(row -> toWardNearbyItem(row))
                    .forEach(items::add);
        }

        if (types.contains(LocationType.POI)) {
            poiRepository.findPoisNearby(lat, lng, radiusMeters, limit)
                    .stream()
                    .map(row -> toPoiNearbyItem(row))
                    .forEach(items::add);
        }

        // Sort merged list by distance, apply global limit
        List<NearbyItem> sorted = items.stream()
                .sorted(Comparator.comparingInt(NearbyItem::distanceMeters))
                .limit(limit)
                .toList();

        if (sorted.isEmpty()) {
            throw new LocationNotFoundException("No candidates found within " + radiusMeters + "m");
        }

        return new NearbyResponse(new CoordinateDto(lat, lng), radiusMeters, sorted.size(), sorted);
    }

    // ── 3. Text Search / Autocomplete ─────────────────────────────────────────

    public SearchResponse search(
            String q,
            UUID countryId,
            UUID provinceId,
            UUID wardId,
            Double lat,
            Double lng,
            int limit) {
        if (q == null || q.trim().length() < 2) {
            throw new IllegalArgumentException("q must be at least 2 characters");
        }
        if (q.length() > 120) {
            throw new IllegalArgumentException("q must be at most 120 characters");
        }

        String provinceIdStr = provinceId != null ? provinceId.toString() : null;
        String countryIdStr = countryId != null ? countryId.toString() : null;

        // Fan-out search across Ward and POI, then merge by score
        List<SearchItem> wardItems = wardRepository
                .searchWards(q, provinceIdStr, countryIdStr, lat, lng, limit)
                .stream().map(row -> toWardSearchItem(row, q)).toList();

        List<SearchItem> poiItems = poiRepository
                .searchPois(q, lat, lng, limit)
                .stream().map(row -> toPoiSearchItem(row, q)).toList();

        List<SearchItem> merged = Stream.concat(wardItems.stream(), poiItems.stream())
                .sorted(Comparator.comparingDouble(SearchItem::score).reversed())
                .limit(limit)
                .toList();

        return new SearchResponse(
                q,
                new SearchFilters(countryId, provinceId, wardId),
                merged.size(),
                merged);
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

    private NearbyItem toWardNearbyItem(Object[] row) {
        // row: ward_id, code, created_at, name, updated_at, province_id, ..., distance
        UUID id = UUID.fromString(row[0].toString());
        String wardName = (String) row[3];
        UUID provinceId = UUID.fromString(row[5].toString());
        String provinceName = provinceRepository.findById(provinceId)
            .map(province -> province.getName())
            .orElseThrow(() -> new LocationNotFoundException("Province not found for ward " + id));
        int distance = ((Number) row[row.length - 1]).intValue();
        return new NearbyItem(
            LocationType.WARD,
            id,
            wardName,
            null,
            wardName + ", " + provinceName,
            0,
            0,
            distance);
    }

    private NearbyItem toPoiNearbyItem(Object[] row) {
        // row: poi_id, listing_id, name, category, geolocation, distance_meters,
        // created_at, distance
        UUID id = UUID.fromString(row[0].toString());
        String name = (String) row[2];
        PoiCategory category = PoiCategory.valueOf((String) row[3]);
        int distance = ((Number) row[row.length - 1]).intValue();
        return new NearbyItem(LocationType.POI, id, name, category, name, 0, 0, distance);
    }

    private SearchItem toWardSearchItem(Object[] row, String q) {
        // row: ward_id, code, created_at, name, updated_at, province_id, ..., score, lng, lat
        UUID id = UUID.fromString(row[0].toString());
        String name = (String) row[3];
        UUID provinceId = UUID.fromString(row[5].toString());
        String provinceName = provinceRepository.findById(provinceId)
            .map(province -> province.getName())
            .orElseThrow(() -> new LocationNotFoundException("Province not found for ward " + id));
        double score = row[row.length - 3] != null ? ((Number) row[row.length - 3]).doubleValue() : 0.5;
        double lng = row[row.length - 2] != null ? ((Number) row[row.length - 2]).doubleValue() : 0;
        double lat = row[row.length - 1] != null ? ((Number) row[row.length - 1]).doubleValue() : 0;
        return new SearchItem(LocationType.WARD, id, name, name + ", " + provinceName,
                lat, lng, Math.round(score * 100.0) / 100.0, highlight(name, q));
    }

    private SearchItem toPoiSearchItem(Object[] row, String q) {
        UUID id = UUID.fromString(row[0].toString());
        String name = (String) row[2];
        double score = row[row.length - 3] != null ? ((Number) row[row.length - 3]).doubleValue() : 0.5;
        double lngVal = row[row.length - 2] != null ? ((Number) row[row.length - 2]).doubleValue() : 0;
        double latVal = row[row.length - 1] != null ? ((Number) row[row.length - 1]).doubleValue() : 0;
        return new SearchItem(LocationType.POI, id, name, name,
                latVal, lngVal, Math.round(score * 100.0) / 100.0, highlight(name, q));
    }

    private List<PoiRef> buildPoiRefs(double lat, double lng, int radius, int limit) {
        return poiRepository.findPoisNearby(lat, lng, radius, limit).stream()
                .map(row -> new PoiRef(
                        UUID.fromString(row[0].toString()),
                        (String) row[2],
                        PoiCategory.valueOf((String) row[3]),
                        0, 0,
                        ((Number) row[row.length - 1]).intValue()))
                .toList();
    }

    // Simple highlight: find matched substrings in name
    private List<String> highlight(String name, String q) {
        if (name.toLowerCase().contains(q.toLowerCase()))
            return List.of(q);
        return List.of();
    }

    // ── Validation ────────────────────────────────────────────────────────────

    private void validateCoordinates(double lat, double lng) {
        if (lat < -90 || lat > 90) {
            throw new InvalidCoordinateException("lat must be between -90 and 90");
        }
        if (lng < -180 || lng > 180) {
            throw new InvalidCoordinateException("lng must be between -180 and 180");
        }
    }
}
