package com.dathq.swd302.listingservice.repository;

import com.dathq.swd302.listingservice.model.TourScene;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TourSceneRepository extends JpaRepository<TourScene, UUID> {
    List<TourScene> findByVirtualTour_TourIdOrderBySceneOrder(UUID tourId);

    List<TourScene> findByVirtualTour_TourId(UUID tourId);
}
