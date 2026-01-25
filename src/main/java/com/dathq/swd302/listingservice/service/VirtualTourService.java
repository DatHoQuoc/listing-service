package com.dathq.swd302.listingservice.service;


import com.dathq.swd302.listingservice.dto.request.AddTourSceneRequest;
import com.dathq.swd302.listingservice.dto.request.CreateVirtualTourRequest;
import com.dathq.swd302.listingservice.dto.response.TourSceneResponse;
import com.dathq.swd302.listingservice.dto.response.VirtualTourResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
public interface VirtualTourService {
    /**
     * Create a virtual tour for a listing
     * - One listing can have only one virtual tour
     * - Creates tour record with is_published=false
     *
     * @param userId user ID
     * @param listingId listing ID
     * @param request tour configuration (provider, external URL if any)
     * @return virtual tour response
     * @throws //VirtualTourAlreadyExistsException if tour already exists
     */
    VirtualTourResponse createVirtualTour(UUID userId, UUID listingId,
                                          CreateVirtualTourRequest request);

    /**
     * Get virtual tour for a listing
     * - Includes all tour scenes
     *
     * @param userId user ID
     * @param listingId listing ID
     * @return virtual tour with scenes
     * @throws //VirtualTourNotFoundException if tour doesn't exist
     */
    VirtualTourResponse getVirtualTour(UUID userId, UUID listingId);

    /**
     * Delete virtual tour
     * - Deletes tour and all associated scenes
     * - Removes all 360° images from MinIO
     *
     * @param userId user ID
     * @param listingId listing ID
     */
    void deleteVirtualTour(UUID userId, UUID listingId);

    /**
     * Add a 360° panorama scene to tour
     * - Uploads 360° image to MinIO
     * - Creates tour_scene record
     * - Increments total_scenes count
     *
     * @param userId user ID
     * @param listingId listing ID
     * @param file 360° image file (equirectangular format)
     * @param request scene metadata (name, position, hotspots)
     * @return tour scene response
     * @throws //InvalidFileTypeException if not a valid image
     * @throws //FileSizeExceededException if file too large (max 20MB for 360° images)
     */
    TourSceneResponse addTourScene(UUID userId, UUID listingId, MultipartFile file,
                                   AddTourSceneRequest request);

    /**
     * Update tour scene metadata
     * - Updates scene name, position, hotspots
     * - Does not change the 360° image itself
     *
     * @param userId user ID
     * @param listingId listing ID
     * @param sceneId scene ID
     * @param request updated scene data
     * @return updated scene response
     */
    TourSceneResponse updateTourScene(UUID userId, UUID listingId, UUID sceneId,
                                      AddTourSceneRequest request);

    /**
     * Delete a tour scene
     * - Removes 360° image from MinIO
     * - Deletes scene record
     * - Decrements total_scenes count
     *
     * @param userId user ID
     * @param listingId listing ID
     * @param sceneId scene ID to delete
     */
    void deleteTourScene(UUID userId, UUID listingId, UUID sceneId);

    /**
     * Reorder tour scenes
     * - Updates scene_order for each scene
     *
     * @param userId user ID
     * @param listingId listing ID
     * @param orderedSceneIds list of scene IDs in desired order
     */
    void reorderTourScenes(UUID userId, UUID listingId, List<UUID> orderedSceneIds);

    /**
     * Publish virtual tour
     * - Sets is_published=true
     * - Makes tour visible to public
     *
     * @param userId user ID
     * @param listingId listing ID
     */
    void publishVirtualTour(UUID userId, UUID listingId);

    /**
     * Unpublish virtual tour
     * - Sets is_published=false
     *
     * @param userId user ID
     * @param listingId listing ID
     */
    void unpublishVirtualTour(UUID userId, UUID listingId);

    /**
     * Get all scenes for a tour
     *
     * @param userId user ID
     * @param tourId tour ID
     * @return list of scenes ordered by scene_order
     */
    List<TourSceneResponse> getTourScenes(UUID userId, UUID tourId);

    /**
     * Check if listing has virtual tour
     *
     * @param listingId listing ID
     * @return true if tour exists
     */
    boolean hasVirtualTour(UUID listingId);
}
