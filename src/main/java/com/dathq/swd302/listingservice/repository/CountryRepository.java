package com.dathq.swd302.listingservice.repository;
import java.util.Optional;
import java.util.UUID;

import com.dathq.swd302.listingservice.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CountryRepository extends JpaRepository<Country, UUID>{
    Optional<Country> findByName(String name);

    Optional<Country> findByCode(String code);
}
