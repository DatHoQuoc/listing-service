package com.dathq.swd302.listingservice.controller;
import com.dathq.swd302.listingservice.dto.request.UpdateCaptionRequest;
import com.dathq.swd302.listingservice.dto.response.ImageUploadResponse;
import com.dathq.swd302.listingservice.service.ImageService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/listings/{listingId}/images")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUploadResponse> uploadImage(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID listingId,
            @RequestParam("file") MultipartFile file) {

        ImageUploadResponse response = imageService.uploadImage(userId, listingId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ImageUploadResponse>> uploadImages(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID listingId,
            @RequestParam("files") List<MultipartFile> files) {

        List<ImageUploadResponse> response = imageService.uploadImages(userId, listingId, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping
    public ResponseEntity<List<ImageUploadResponse>> getListingImages(@PathVariable UUID listingId) {
        List<ImageUploadResponse> images = imageService.getListingImages(listingId);
        return ResponseEntity.ok(images);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> countListingImages(@PathVariable UUID listingId) {
        Integer count = imageService.countListingImages(listingId);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/featured")
    public ResponseEntity<Void> setFeaturedImage(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID listingId,
            @RequestParam("imageUrl") String imageUrl) {

        imageService.setFeaturedImage(userId, listingId, imageUrl);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteImage(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID listingId,
            @RequestParam("imageUrl") String imageUrl) {

        imageService.deleteImage(userId, listingId, imageUrl);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reorder")
    public ResponseEntity<Void> reorderImages(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID listingId,
            @RequestBody List<String> orderedImageUrls) {

        imageService.reorderImages(userId, listingId, orderedImageUrls);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/caption")
    public ResponseEntity<Void> updateImageCaption(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID listingId,
            @RequestBody UpdateCaptionRequest request) {

        imageService.updateImageCaption(userId, listingId, request.getImageUrl(), request.getCaption());
        return ResponseEntity.ok().build();
    }
}
