package com.dathq.swd302.listingservice.repository;
import com.dathq.swd302.listingservice.model.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, UUID> {
    List<Province> findByCountryCountryId(UUID countryId);

    List<Province> findByCountryCountryIdOrderByName(UUID countryId);

    List<Province> findByNameContainingIgnoreCase(String name);
}
