package com.dathq.swd302.listingservice.repository;

import com.dathq.swd302.listingservice.model.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WardRepository extends JpaRepository<Ward, UUID> {
}
