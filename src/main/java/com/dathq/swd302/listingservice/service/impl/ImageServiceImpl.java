package com.dathq.swd302.listingservice.service.impl;
import com.dathq.swd302.listingservice.dto.response.ImageUploadResponse;
import com.dathq.swd302.listingservice.exception.ImageNotFoundException;
import com.dathq.swd302.listingservice.exception.ListingNotFoundException;
import com.dathq.swd302.listingservice.exception.UnauthorizedException;
import com.dathq.swd302.listingservice.model.Listing;
import static com.dathq.swd302.listingservice.model.Listing.ImageMetadata;
import com.dathq.swd302.listingservice.repository.ListingRepository;
import com.dathq.swd302.listingservice.service.ImageService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.dathq.swd302.listingservice.common.Common.generateImageFileName;
import static com.dathq.swd302.listingservice.common.util.ValidationUtil.validateImageFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ImageServiceImpl implements ImageService {

    private final ListingRepository listingRepository;
    private final MinIOStorageService minIOStorageService;
    private final ObjectMapper objectMapper;

    @Override
    public ImageUploadResponse uploadImage(UUID userId, UUID listingId, MultipartFile file) {
        log.info("Uploading image for listing: {} by user: {}", listingId, userId);
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }
        validateImageFile(file);

        String fileName = generateImageFileName(listingId, file.getOriginalFilename());
        String fileUrl = minIOStorageService.uploadFile(file, fileName, "images");
        List<ImageMetadata> existingImages = getImagesMetadata(listing);
        int nextOrder = existingImages.size() + 1;
        ImageMetadata newImage = ImageMetadata.builder()
                .url(fileUrl)
                .order(nextOrder)
                .caption("")
                .uploadedAt(OffsetDateTime.now())
                .build();
        existingImages.add(newImage);
        updateListingImagesJson(listing, existingImages);
        if (listing.getFeaturedImageUrl() == null) {
            listing.setFeaturedImageUrl(fileUrl);
        }

        listingRepository.save(listing);
        log.info("Image uploaded successfully: {}", fileUrl);

        return ImageUploadResponse.builder()
                .url(fileUrl)
                .order(nextOrder)
                .caption("")
                .uploadedAt(OffsetDateTime.now())
                .build();
    }

    @Override
    public List<ImageUploadResponse> uploadImages(UUID userId, UUID listingId, List<MultipartFile> files) {
        log.info("Uploading {} images for listing: {}", files.size(), listingId);

        return files.stream()
                .map(file -> uploadImage(userId, listingId, file))
                .collect(Collectors.toList());
    }

    @Override
    public void setFeaturedImage(UUID userId, UUID listingId, String imageUrl) {
        log.info("Setting featured image for listing: {}", listingId);

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }

        List<ImageMetadata> images = getImagesMetadata(listing);
        boolean imageExists = images.stream().anyMatch(img -> img.getUrl().equals(imageUrl));

        if (!imageExists) {
            throw new ImageNotFoundException("Image not found in listing");
        }

        listing.setFeaturedImageUrl(imageUrl);
        listingRepository.save(listing);

        log.info("Featured image set successfully: {}", imageUrl);
    }

    @Override
    public void deleteImage(UUID userId, UUID listingId, String imageUrl) {
        log.info("Deleting image from listing: {}", listingId);

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }

        List<ImageMetadata> images = getImagesMetadata(listing);
        boolean removed = images.removeIf(img -> img.getUrl().equals(imageUrl));

        if (!removed) {
            throw new ImageNotFoundException("Image not found in listing");
        }

        minIOStorageService.deleteFile(imageUrl);

        for (int i = 0; i < images.size(); i++) {
            images.get(i).setOrder(i + 1);
        }

        updateListingImagesJson(listing, images);

        if (imageUrl.equals(listing.getFeaturedImageUrl())) {
            listing.setFeaturedImageUrl(images.isEmpty() ? null : images.get(0).getUrl());
        }

        listingRepository.save(listing);

        log.info("Image deleted successfully: {}", imageUrl);
    }

    @Override
    public List<ImageUploadResponse> getListingImages(UUID listingId) {

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        List<ImageMetadata> images = getImagesMetadata(listing);

        return images.stream()
                .map(img -> ImageUploadResponse.builder()
                        .url(minIOStorageService.generatePresignedUrl(img.getUrl(), 3600))
                        .order(img.getOrder())
                        .caption(img.getCaption())
                        .uploadedAt(img.getUploadedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void reorderImages(UUID userId, UUID listingId, List<String> orderedImageUrls) {
        log.info("Reordering images for listing: {}", listingId);

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }

        List<ImageMetadata> images = getImagesMetadata(listing);
        Map<String, ImageMetadata> imageMap = images.stream()
                .collect(Collectors.toMap(ImageMetadata::getUrl, img -> img));

        List<ImageMetadata> reorderedImages = new ArrayList<>();
        for (int i = 0; i < orderedImageUrls.size(); i++) {
            String url = orderedImageUrls.get(i);
            ImageMetadata img = imageMap.get(url);
            if (img != null) {
                img.setOrder(i + 1);
                reorderedImages.add(img);
            }
        }

        updateListingImagesJson(listing, reorderedImages);
        listingRepository.save(listing);

        log.info("Images reordered successfully");
    }

    @Override
    public void updateImageCaption(UUID userId, UUID listingId, String imageUrl, String caption) {
        log.info("Updating image caption for listing: {}", listingId);

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }

        List<ImageMetadata> images = getImagesMetadata(listing);
        boolean found = false;

        for (ImageMetadata img : images) {
            if (img.getUrl().equals(imageUrl)) {
                img.setCaption(caption);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new ImageNotFoundException("Image not found in listing");
        }

        updateListingImagesJson(listing, images);
        listingRepository.save(listing);

        log.info("Image caption updated successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public Integer countListingImages(UUID listingId) {

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        return getImagesMetadata(listing).size();
    }


    //Helper method
    private List<ImageMetadata> getImagesMetadata(Listing listing) {
        if (listing.getImagesJson() == null || listing.getImagesJson().isEmpty()) {
            return new ArrayList<>();
        }

        return listing.getImagesJson();
    }

    private void updateListingImagesJson(Listing listing, List<ImageMetadata> images) {
        listing.setImagesJson(images);
    }

}
