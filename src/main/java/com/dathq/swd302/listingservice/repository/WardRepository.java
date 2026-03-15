package com.dathq.swd302.listingservice.repository;

import com.dathq.swd302.listingservice.model.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WardRepository extends JpaRepository<Ward, UUID> {
    List<Ward> findByProvinceProvinceId(UUID provinceId);

    List<Ward> findByProvinceProvinceIdOrderByName(UUID provinceId);

    List<Ward> findByNameContainingIgnoreCase(String name);


    // Nearest ward to a point (for reverse geocoding)
    @Query(value = """
        SELECT w.*, ST_Distance(CAST(w.geolocation AS geography), CAST(ST_MakePoint(:lng, :lat) AS geography)) AS distance
        FROM wards w
        WHERE w.geolocation IS NOT NULL
        ORDER BY CAST(w.geolocation AS geography) <-> CAST(ST_MakePoint(:lng, :lat) AS geography)
        LIMIT 1
        """, nativeQuery = true)
    Optional<Object[]> findNearestWard(@Param("lat") double lat, @Param("lng") double lng);

    // Wards within radius (for nearby lookup)
    @Query(value = """
                SELECT w.*, ST_Distance(CAST(w.geolocation AS geography), CAST(ST_MakePoint(:lng, :lat) AS geography)) AS distance
        FROM wards w
        WHERE w.geolocation IS NOT NULL
                    AND ST_DWithin(CAST(w.geolocation AS geography), CAST(ST_MakePoint(:lng, :lat) AS geography), :radiusMeters)
                ORDER BY CAST(w.geolocation AS geography) <-> CAST(ST_MakePoint(:lng, :lat) AS geography)
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findWardsNearby(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusMeters") int radiusMeters,
            @Param("limit") int limit
    );

    // Text search with unaccent (for autocomplete)
    @Query(value = """
        SELECT w.*,
               similarity(unaccent(lower(w.name)), unaccent(lower(:q))) AS score,
               ST_X(w.geolocation) AS lng,
               ST_Y(w.geolocation) AS lat
        FROM wards w
        JOIN provinces p ON w.province_id = p.province_id
        WHERE unaccent(lower(w.name)) LIKE '%' || unaccent(lower(:q)) || '%'
          AND (:provinceId IS NULL OR w.province_id = CAST(:provinceId AS uuid))
          AND (:countryId  IS NULL OR p.country_id  = CAST(:countryId  AS uuid))
        ORDER BY
            similarity(unaccent(lower(w.name)), unaccent(lower(:q))) DESC,
            CASE WHEN :lat IS NOT NULL AND w.geolocation IS NOT NULL
                  THEN ST_Distance(CAST(w.geolocation AS geography), CAST(ST_MakePoint(:lng, :lat) AS geography))
                 ELSE 0 END ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> searchWards(
            @Param("q")          String q,
            @Param("provinceId") String provinceId,
            @Param("countryId")  String countryId,
            @Param("lat")        Double lat,
            @Param("lng")        Double lng,
            @Param("limit")      int limit
    );
}
