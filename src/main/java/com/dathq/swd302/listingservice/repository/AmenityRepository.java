package com.dathq.swd302.listingservice.repository;

import com.dathq.swd302.listingservice.model.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, UUID> {
    List<Amenity> findAllByOrderByAmenityCategoryAscAmenityNameAsc();

    List<Amenity> findByAmenityCategoryOrderByAmenityName(String category);

    List<Amenity> findByAmenityNameContainingIgnoreCase(String keyword);
}
