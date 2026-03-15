package com.dathq.swd302.listingservice.controller;

import com.dathq.swd302.listingservice.dto.request.AddTourSceneRequest;
import com.dathq.swd302.listingservice.dto.request.CreateVirtualTourRequest;
import com.dathq.swd302.listingservice.dto.response.TourSceneResponse;
import com.dathq.swd302.listingservice.dto.response.VirtualTourResponse;
import com.dathq.swd302.listingservice.security.JwtClaims;
import com.dathq.swd302.listingservice.security.JwtUser;
import com.dathq.swd302.listingservice.service.VirtualTourService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/listings/{listingId}/tours")
@RequiredArgsConstructor
public class VirtualTourController {
    private final VirtualTourService virtualTourService;

    // --- Virtual Tour Lifecycle ---

    @PostMapping
    public ResponseEntity<VirtualTourResponse> createVirtualTour(
            @PathVariable UUID listingId,
            @JwtUser JwtClaims claims,
            @RequestBody @Valid CreateVirtualTourRequest request) {

        VirtualTourResponse response = virtualTourService.createVirtualTour(claims.getUserId(), listingId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<VirtualTourResponse> getVirtualTour(
            @PathVariable UUID listingId,
            @JwtUser JwtClaims claims) {

        return ResponseEntity.ok(virtualTourService.getVirtualTour(claims.getUserId(), listingId, claims.getRole()));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteVirtualTour(
            @PathVariable UUID listingId,
            @JwtUser JwtClaims claims) {

        virtualTourService.deleteVirtualTour(claims.getUserId(), listingId);
        return ResponseEntity.noContent().build();
    }

    // --- Publishing ---

    @PutMapping("/publish")
    public ResponseEntity<Void> publishVirtualTour(
            @PathVariable UUID listingId,
            @JwtUser JwtClaims claims) {

        virtualTourService.publishVirtualTour(claims.getUserId(), listingId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/unpublish")
    public ResponseEntity<Void> unpublishVirtualTour(
            @PathVariable UUID listingId,
            @JwtUser JwtClaims claims) {

        virtualTourService.unpublishVirtualTour(claims.getUserId(), listingId);
        return ResponseEntity.ok().build();
    }

    // --- Scene Management ---

    @PostMapping(value = "/scenes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TourSceneResponse> addTourScene(
            @PathVariable UUID listingId,
            @JwtUser JwtClaims claims,
            @RequestPart("file") MultipartFile file,
            @RequestPart("data") @Valid AddTourSceneRequest request) {

        TourSceneResponse response = virtualTourService.addTourScene(claims.getUserId(), listingId, file, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/scenes/{sceneId}")
    public ResponseEntity<TourSceneResponse> updateTourScene(
            @PathVariable UUID listingId,
            @PathVariable UUID sceneId,
            @JwtUser JwtClaims claims,
            @RequestBody @Valid AddTourSceneRequest request) {

        // Note: Updates often don't include the file, so this is a standard JSON Body
        // Put
        // If you need to update the image too, you'd need a separate endpoint or
        // Multipart PUT
        TourSceneResponse response = virtualTourService.updateTourScene(claims.getUserId(), listingId, sceneId,
                request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/scenes/{sceneId}")
    public ResponseEntity<Void> deleteTourScene(
            @PathVariable UUID listingId,
            @PathVariable UUID sceneId,
            @JwtUser JwtClaims claims) {

        virtualTourService.deleteTourScene(claims.getUserId(), listingId, sceneId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/scenes/reorder")
    public ResponseEntity<Void> reorderTourScenes(
            @PathVariable UUID listingId,
            @JwtUser JwtClaims claims,
            @RequestBody List<UUID> orderedSceneIds) {

        virtualTourService.reorderTourScenes(claims.getUserId(), listingId, orderedSceneIds);
        return ResponseEntity.ok().build();
    }
}
