package com.dathq.swd302.listingservice.service.impl;
import com.dathq.swd302.listingservice.dto.request.AddTourSceneRequest;
import com.dathq.swd302.listingservice.dto.request.CreateVirtualTourRequest;
import com.dathq.swd302.listingservice.dto.response.TourSceneResponse;
import com.dathq.swd302.listingservice.dto.response.VirtualTourResponse;
import com.dathq.swd302.listingservice.exception.ListingNotFoundException;
import com.dathq.swd302.listingservice.exception.UnauthorizedException;
import com.dathq.swd302.listingservice.exception.VirtualTourAlreadyExistsException;
import com.dathq.swd302.listingservice.exception.VirtualTourNotFoundException;
import com.dathq.swd302.listingservice.mapper.VirtualTourMapper;
import com.dathq.swd302.listingservice.model.Listing;
import com.dathq.swd302.listingservice.model.TourScene;
import com.dathq.swd302.listingservice.model.VirtualTour;
import com.dathq.swd302.listingservice.repository.ListingRepository;
import com.dathq.swd302.listingservice.repository.TourSceneRepository;
import com.dathq.swd302.listingservice.repository.VirtualTourRepository;
import com.dathq.swd302.listingservice.service.VirtualTourService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.dathq.swd302.listingservice.common.Common.generate360FileName;
import static com.dathq.swd302.listingservice.common.util.ValidationUtil.validate360Image;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VirtualTourServiceImpl implements VirtualTourService {


    private final VirtualTourRepository virtualTourRepository;
    private final TourSceneRepository tourSceneRepository;
    private final ListingRepository listingRepository;
    private final MinIOStorageService minIOStorageService;
    private final VirtualTourMapper virtualTourMapper;

    @Override
    public VirtualTourResponse createVirtualTour(UUID userId, UUID listingId, CreateVirtualTourRequest request) {
        log.info("Creating virtual tour for listing: {}", listingId);

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }

        if (virtualTourRepository.existsByListing_ListingId(listingId)) {
            throw new VirtualTourAlreadyExistsException("Virtual tour already exists for this listing");
        }

        VirtualTour virtualTour = new VirtualTour();
        virtualTour.setListing(listing);
        virtualTour.setTourUrl(request.getTourUrl());
        virtualTour.setTourProvider(request.getTourProvider());
        virtualTour.setTotalScenes(0);
        virtualTour.setPublished(false);
        virtualTour.setCreatedAt(OffsetDateTime.now());
        virtualTour.setUpdatedAt(OffsetDateTime.now());

        VirtualTour savedTour = virtualTourRepository.save(virtualTour);

        log.info("Virtual tour created: {}", savedTour.getTourId());

        return virtualTourMapper.toResponse(savedTour);
    }

    @Override
    @Transactional(readOnly = true)
    public VirtualTourResponse getVirtualTour(UUID userId, UUID listingId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }

        VirtualTour virtualTour = virtualTourRepository.findByListing_ListingId(listingId)
                .orElseThrow(() -> new VirtualTourNotFoundException("Virtual tour not found for listing: " + listingId));

        VirtualTourResponse response = virtualTourMapper.toResponse(virtualTour);

        List<TourScene> scenes = tourSceneRepository.findByVirtualTour_TourIdOrderBySceneOrder(virtualTour.getTourId());
        response.setScenes(virtualTourMapper.toSceneResponseList(scenes));

        return response;
    }

    @Override
    public void deleteVirtualTour(UUID userId, UUID listingId) {
        log.info("Deleting virtual tour for listing: {}", listingId);

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }

        VirtualTour virtualTour = virtualTourRepository.findByListing_ListingId(listingId)
                .orElseThrow(() -> new VirtualTourNotFoundException("Virtual tour not found for listing: " + listingId));

        List<TourScene> scenes = tourSceneRepository.findByVirtualTour_TourId(virtualTour.getTourId());
        for (TourScene scene : scenes) {
            minIOStorageService.deleteFile(scene.getPanoramaUrl());
        }

        tourSceneRepository.deleteAll(scenes);
        virtualTourRepository.delete(virtualTour);

        log.info("Virtual tour deleted successfully");
    }

    @Override
    public TourSceneResponse addTourScene(UUID userId, UUID listingId, MultipartFile file, AddTourSceneRequest request) {
        log.info("Adding tour scene to listing: {}", listingId);

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }

        VirtualTour virtualTour = virtualTourRepository.findByListing_ListingId(listingId)
                .orElseThrow(() -> new VirtualTourNotFoundException("Virtual tour not found. Create tour first."));

        validate360Image(file);

        String fileName = generate360FileName(listingId, file.getOriginalFilename());
        String fileUrl = minIOStorageService.uploadFile(file, fileName, "360-images");

        int nextOrder = virtualTour.getTotalScenes() + 1;

        TourScene tourScene = new TourScene();
        tourScene.setSceneId(virtualTour.getTourId());
        tourScene.setSceneName(request.getSceneName());
        tourScene.setPanoramaUrl(fileUrl);
        tourScene.setSceneOrder(nextOrder);
        tourScene.setPositionX(request.getPositionX());
        tourScene.setPositionY(request.getPositionY());
        tourScene.setPositionZ(request.getPositionZ());
        tourScene.setHotspotsJson(request.getHotspotsJson());
        tourScene.setCreatedAt(OffsetDateTime.now());

        TourScene savedScene = tourSceneRepository.save(tourScene);

        virtualTour.setTotalScenes(virtualTour.getTotalScenes() + 1);
        virtualTour.setUpdatedAt(OffsetDateTime.now());
        virtualTourRepository.save(virtualTour);

        log.info("Tour scene added: {}", savedScene.getSceneId());

        return virtualTourMapper.toSceneResponse(savedScene);
    }

    @Override
    public TourSceneResponse updateTourScene(UUID userId, UUID listingId, UUID sceneId, AddTourSceneRequest request) {
        log.info("Updating tour scene: {}", sceneId);

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }

        TourScene tourScene = tourSceneRepository.findById(sceneId)
                .orElseThrow(() -> new RuntimeException("Tour scene not found"));

        tourScene.setSceneName(request.getSceneName());
        tourScene.setPositionX(request.getPositionX());
        tourScene.setPositionY(request.getPositionY());
        tourScene.setPositionZ(request.getPositionZ());
        tourScene.setHotspotsJson(request.getHotspotsJson());

        TourScene updatedScene = tourSceneRepository.save(tourScene);

        log.info("Tour scene updated: {}", sceneId);

        return virtualTourMapper.toSceneResponse(updatedScene);
    }

    @Override
    public void deleteTourScene(UUID userId, UUID listingId, UUID sceneId) {
        log.info("Deleting tour scene: {}", sceneId);

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }

        TourScene tourScene = tourSceneRepository.findById(sceneId)
                .orElseThrow(() -> new RuntimeException("Tour scene not found"));

        VirtualTour virtualTour = virtualTourRepository.findById(tourScene.getSceneId())
                .orElseThrow(() -> new VirtualTourNotFoundException("Virtual tour not found"));

        minIOStorageService.deleteFile(tourScene.getPanoramaUrl());
        tourSceneRepository.delete(tourScene);

        virtualTour.setTotalScenes(Math.max(0, virtualTour.getTotalScenes() - 1));
        virtualTour.setUpdatedAt(OffsetDateTime.now());
        virtualTourRepository.save(virtualTour);

        List<TourScene> remainingScenes = tourSceneRepository.findByVirtualTour_TourIdOrderBySceneOrder(virtualTour.getTourId());
        for (int i = 0; i < remainingScenes.size(); i++) {
            remainingScenes.get(i).setSceneOrder(i + 1);
        }
        tourSceneRepository.saveAll(remainingScenes);

        log.info("Tour scene deleted: {}", sceneId);
    }

    @Override
    public void reorderTourScenes(UUID userId, UUID listingId, List<UUID> orderedSceneIds) {
        log.info("Reordering tour scenes for listing: {}", listingId);

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }

        for (int i = 0; i < orderedSceneIds.size(); i++) {
            UUID sceneId = orderedSceneIds.get(i);
            TourScene scene = tourSceneRepository.findById(sceneId)
                    .orElseThrow(() -> new RuntimeException("Tour scene not found: " + sceneId));
            scene.setSceneOrder(i + 1);
            tourSceneRepository.save(scene);
        }

        log.info("Tour scenes reordered successfully");
    }

    @Override
    public void publishVirtualTour(UUID userId, UUID listingId) {
        log.info("Publishing virtual tour for listing: {}", listingId);

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }

        VirtualTour virtualTour = virtualTourRepository.findByListing_ListingId(listingId)
                .orElseThrow(() -> new VirtualTourNotFoundException("Virtual tour not found"));

        virtualTour.setPublished(true);
        virtualTour.setUpdatedAt(OffsetDateTime.now());
        virtualTourRepository.save(virtualTour);

        log.info("Virtual tour published");
    }

    @Override
    public void unpublishVirtualTour(UUID userId, UUID listingId) {
        log.info("Unpublishing virtual tour for listing: {}", listingId);

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }

        VirtualTour virtualTour = virtualTourRepository.findByListing_ListingId(listingId)
                .orElseThrow(() -> new VirtualTourNotFoundException("Virtual tour not found"));

        virtualTour.setPublished(false);
        virtualTour.setUpdatedAt(OffsetDateTime.now());
        virtualTourRepository.save(virtualTour);

        log.info("Virtual tour unpublished");
    }

    @Override
    @Transactional(readOnly = true)
    public List<TourSceneResponse> getTourScenes(UUID userId, UUID tourId) {
        List<TourScene> scenes = tourSceneRepository.findByVirtualTour_TourIdOrderBySceneOrder(tourId);
        return virtualTourMapper.toSceneResponseList(scenes);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasVirtualTour(UUID listingId) {
        return virtualTourRepository.existsByListing_ListingId(listingId);
    }
}
