package com.dathq.swd302.listingservice.service;


import com.dathq.swd302.listingservice.dto.response.ImageUploadResponse;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;
public interface ImageService {
    /**
     * Upload a single property image
     * - Validates file type (jpg, png, webp, heic)
     * - Validates file size (max 10MB)
     * - Uploads to MinIO storage
     * - Updates listing's images_json field
     * - Generates thumbnail (optional, for future)
     *
     * @param userId user ID (for authorization)
     * @param listingId listing to attach image to
     * @param file image file
     * @return upload response with URL and metadata
     * @throws //InvalidFileTypeException if not an image
     * @throws //FileSizeExceededException if file too large
     * @throws //ListingNotFoundException if listing not found
     * @throws //UnauthorizedException if user doesn't own listing
     */
    ImageUploadResponse uploadImage(UUID userId, UUID listingId, MultipartFile file);

    /**
     * Upload multiple images at once
     * - Batch upload for efficiency
     * - Each image validated individually
     * - Returns list of successful uploads (skips invalid files)
     *
     * @param userId user ID
     * @param listingId listing ID
     * @param files list of image files
     * @return list of upload responses
     */
    List<ImageUploadResponse> uploadImages(UUID userId, UUID listingId, List<MultipartFile> files);

    /**
     * Set featured image for listing
     * - Updates listing's featured_image_url
     * - Image must already be uploaded to the listing
     *
     * @param userId user ID
     * @param listingId listing ID
     * @param imageUrl URL of existing image to set as featured
     * @throws //ImageNotFoundException if image not found in listing
     */
    void setFeaturedImage(UUID userId, UUID listingId, String imageUrl);

    /**
     * Delete an image from listing
     * - Removes from MinIO storage
     * - Updates listing's images_json
     * - If featured image is deleted, clears featured_image_url
     *
     * @param userId user ID
     * @param listingId listing ID
     * @param imageUrl image URL to delete
     * @throws //ImageNotFoundException if image not found
     */
    void deleteImage(UUID userId, UUID listingId, String imageUrl);

    /**
     * Get all images for a listing
     * - Returns images sorted by order field
     *
     * @param listingId listing ID
     * @return list of image metadata (URL, order, caption)
     */
    List<ImageUploadResponse> getListingImages(UUID listingId);

    /**
     * Reorder images
     * - Updates order field in images_json
     * - Accepts list of image URLs in desired order
     *
     * @param userId user ID
     * @param listingId listing ID
     * @param orderedImageUrls list of URLs in desired order
     */
    void reorderImages(UUID userId, UUID listingId, List<String> orderedImageUrls);

    /**
     * Update image caption
     *
     * @param userId user ID
     * @param listingId listing ID
     * @param imageUrl image URL
     * @param caption new caption
     */
    void updateImageCaption(UUID userId, UUID listingId, String imageUrl, String caption);

    /**
     * Count images for a listing
     *
     * @param listingId listing ID
     * @return number of images
     */
    Integer countListingImages(UUID listingId);
}
