package com.dathq.swd302.listingservice.service;

public interface ImageService {
    /**
     * Upload a regular property image
     * - Validates file type (jpg, png, webp)
     * - Validates file size (max 10MB per image)
     * - Uploads to MinIO
     * - Updates listing's images_json field
     * - Generates thumbnail (optional)
     *
     * @param listingId the listing to attach image to
     * @param file the image file
     * @return upload response with URL
     * @throws InvalidFileTypeException if file is not an image
     * @throws FileSizeExceededException if file is too large
     */
    ImageUploadResponse uploadImage(UUID listingId, MultipartFile file);

    /**
     * Upload multiple regular images at once
     * - Batch upload for efficiency
     * - Each image validated individually
     *
     * @param listingId the listing to attach images to
     * @param files array of image files
     * @return list of upload responses
     */
    List<ImageUploadResponse> uploadImages(UUID listingId, List<MultipartFile> files);

    /**
     * Upload a 360° panorama image
     * - Validates file type (jpg, png)
     * - Validates file size (max 20MB)
     * - Uploads to MinIO in separate folder
     * - Updates listing's images_json with type='360'
     *
     * @param listingId the listing to attach 360 image to
     * @param file the 360° image file
     * @return upload response with URL
     */
    ImageUploadResponse upload360Image(UUID listingId, MultipartFile file);

    /**
     * Delete an image from a listing
     * - Removes from MinIO
     * - Updates listing's images_json
     *
     * @param listingId the listing ID
     * @param imageUrl the image URL to delete
     */
    void deleteImage(UUID listingId, String imageUrl);

    /**
     * Get all images for a listing
     *
     * @param listingId the listing ID
     * @return list of image URLs
     */
    List<String> getListingImages(UUID listingId);

    /**
     * Reorder images for a listing
     * - Updates the order field in images_json
     *
     * @param listingId the listing ID
     * @param imageUrls list of image URLs in desired order
     */
    void reorderImages(UUID listingId, List<String> imageUrls);
}
