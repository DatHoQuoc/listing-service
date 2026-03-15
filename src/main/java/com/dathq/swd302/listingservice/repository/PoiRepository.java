package com.dathq.swd302.listingservice.repository;

import com.dathq.swd302.listingservice.model.PointOfInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import java.util.UUID;
import java.util.List;
import java.util.UUID;

@Repository
public interface PoiRepository extends JpaRepository<PointOfInterest, UUID> {

    // Nearest POIs to a point (for reverse geocoding pois field)
    @Query(value = """
        SELECT p.*, ST_Distance(p.geolocation::geography, ST_MakePoint(:lng, :lat)::geography) AS distance
        FROM points_of_interest p
        WHERE p.geolocation IS NOT NULL
          AND ST_DWithin(p.geolocation::geography, ST_MakePoint(:lng, :lat)::geography, :radiusMeters)
        ORDER BY p.geolocation <-> ST_MakePoint(:lng, :lat)::geography
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findPoisNearby(
            @Param("lat")          double lat,
            @Param("lng")          double lng,
            @Param("radiusMeters") int radiusMeters,
            @Param("limit")        int limit
    );

    // Text search with unaccent
    @Query(value = """
        SELECT p.*,
               similarity(unaccent(lower(p.name)), unaccent(lower(:q))) AS score,
               ST_X(p.geolocation) AS lng_val,
               ST_Y(p.geolocation) AS lat_val
        FROM points_of_interest p
        WHERE unaccent(lower(p.name)) LIKE '%' || unaccent(lower(:q)) || '%'
        ORDER BY
            similarity(unaccent(lower(p.name)), unaccent(lower(:q))) DESC,
            CASE WHEN :lat IS NOT NULL AND p.geolocation IS NOT NULL
                 THEN ST_Distance(p.geolocation::geography, ST_MakePoint(:lng, :lat)::geography)
                 ELSE 0 END ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> searchPois(
            @Param("q")    String q,
            @Param("lat")  Double lat,
            @Param("lng")  Double lng,
            @Param("limit") int limit
    );
}
