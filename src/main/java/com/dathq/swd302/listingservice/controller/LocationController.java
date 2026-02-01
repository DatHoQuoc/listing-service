package com.dathq.swd302.listingservice.controller;

import com.dathq.swd302.listingservice.dto.response.CountryResponse;
import com.dathq.swd302.listingservice.dto.response.ProvinceResponse;
import com.dathq.swd302.listingservice.dto.response.WardResponse;
import com.dathq.swd302.listingservice.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@Tag(name = "Locations", description = "Public API for location lookups")
public class LocationController {
    private final LocationService locationService;

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
}
