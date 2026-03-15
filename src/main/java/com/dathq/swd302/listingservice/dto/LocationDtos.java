package com.dathq.swd302.listingservice.dto;

import com.dathq.swd302.listingservice.model.enums.LocationType;
import com.dathq.swd302.listingservice.model.enums.PoiCategory;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.UUID;
public class LocationDtos {
    // ── Shared ────────────────────────────────────────────────────────────────

    public record CoordinateDto(double lat, double lng) {}

    public record WardRef(UUID wardId, String name, String code) {}
    public record ProvinceRef(UUID provinceId, String name, String code) {}
    public record CountryRef(UUID countryId, String name, String code) {}

    public record PoiRef(
            UUID    poiId,
            String  name,
            PoiCategory category,
            double  lat,
            double  lng,
            int     distanceMeters
    ) {}

    public record NearbyItem(
            LocationType type,
            UUID         id,
            String       name,
            PoiCategory  category,   // null for WARD/STREET
            String       fullAddress,
            double       lat,
            double       lng,
            int          distanceMeters
    ) {}

    public record SearchItem(
            LocationType  type,
            UUID          id,
            String        name,
            String        fullAddress,
            double        lat,
            double        lng,
            double        score,
            List<String>  highlights
    ) {}

    // ── /reverse response ─────────────────────────────────────────────────────

    public record ReverseGeocodeResponse(
            CoordinateDto   input,
            String          normalizedAddress,
            String          streetAddress,
            WardRef         ward,
            ProvinceRef     province,
            CountryRef      country,
            CoordinateDto   coordinate,
            double          distanceMeters,
            double          confidence,
            List<PoiRef>    pois           // empty list when includePois=false
    ) {}

    // ── /nearby response ──────────────────────────────────────────────────────

    public record NearbyResponse(
            CoordinateDto    center,
            int              radiusMeters,
            int              total,
            List<NearbyItem> items
    ) {}

    // ── /search response ──────────────────────────────────────────────────────

    public record SearchFilters(UUID countryId, UUID provinceId, UUID wardId) {}

    public record SearchResponse(
            String            query,
            SearchFilters     filters,
            int               total,
            List<SearchItem>  items
    ) {}
}
