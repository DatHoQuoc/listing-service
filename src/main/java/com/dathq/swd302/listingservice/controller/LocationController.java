package com.dathq.swd302.listingservice.controller;

import com.dathq.swd302.listingservice.dto.LocationDtos;
import com.dathq.swd302.listingservice.dto.response.CountryResponse;
import com.dathq.swd302.listingservice.dto.response.ProvinceResponse;
import com.dathq.swd302.listingservice.dto.response.WardResponse;
import com.dathq.swd302.listingservice.model.enums.LocationType;
import com.dathq.swd302.listingservice.service.LocationService;
import com.dathq.swd302.listingservice.service.impl.LocationServiceSpecific;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.dathq.swd302.listingservice.dto.LocationDtos.*;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@Tag(name = "Locations", description = "Public API for location lookups")
public class LocationController {
    private final LocationService locationService;
    private final LocationServiceSpecific locationServiceSpecific;

    @GetMapping("/countries")
    @Operation(summary = "Get all countries")
    public ResponseEntity<List<CountryResponse>> getCountries() {
        return ResponseEntity.ok(locationService.getAllCountries());
    }

    @GetMapping("/provinces")
    @Operation(summary = "Get provinces by country")
    public ResponseEntity<List<ProvinceResponse>> getProvinces(
            @RequestParam UUID countryId) {
        return ResponseEntity.ok(locationService.getProvincesByCountry(countryId));
    }

    @GetMapping("/wards")
    @Operation(summary = "Get wards by province")
    public ResponseEntity<List<WardResponse>> getWards(
            @RequestParam UUID provinceId) {
        return ResponseEntity.ok(locationService.getWardsByProvince(provinceId));
    }

    /**
     * GET /api/v1/locations/reverse?lat=&lng=&includePois=&poiLimit=
     */
    @GetMapping("/reverse")
    public ResponseEntity<ReverseGeocodeResponse> reverse(
            @RequestParam @DecimalMin("-90")  @DecimalMax("90")  double  lat,
            @RequestParam @DecimalMin("-180") @DecimalMax("180") double  lng,
            @RequestParam(defaultValue = "false")                boolean includePois,
            @RequestParam(defaultValue = "5")  @Min(1) @Max(50) int     poiLimit
    ) {
        return ResponseEntity.ok(locationServiceSpecific.reverse(lat, lng, includePois, poiLimit));
    }

    /**
     * GET /api/v1/locations/nearby?lat=&lng=&radiusMeters=&limit=&types=
     */
    @GetMapping("/nearby")
    public ResponseEntity<NearbyResponse> nearby(
            @RequestParam @DecimalMin("-90")  @DecimalMax("90")  double lat,
            @RequestParam @DecimalMin("-180") @DecimalMax("180") double lng,
            @RequestParam(defaultValue = "3000") @Min(50) @Max(20000) int radiusMeters,
            @RequestParam(defaultValue = "20")   @Min(1)  @Max(100)   int limit,
            @RequestParam(defaultValue = "WARD,POI") String types     // CSV: WARD,STREET,POI
    ) {
        Set<LocationType> typeSet = Arrays.stream(types.split(","))
                .map(String::trim)
                .map(LocationType::valueOf)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(locationServiceSpecific.nearby(lat, lng, radiusMeters, limit, typeSet));
    }

    /**
     * GET /api/v1/locations/search?q=&countryId=&provinceId=&wardId=&lat=&lng=&limit=
     */
    @GetMapping("/search")
    public ResponseEntity<SearchResponse> search(
            @RequestParam @NotBlank @Size(min = 2, max = 120) String q,
            @RequestParam(required = false) UUID   countryId,
            @RequestParam(required = false) UUID   provinceId,
            @RequestParam(required = false) UUID   wardId,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int limit
    ) {
        return ResponseEntity.ok(
                locationServiceSpecific.search(q, countryId, provinceId, wardId, lat, lng, limit)
        );
    }
}
