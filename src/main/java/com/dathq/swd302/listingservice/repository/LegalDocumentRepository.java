package com.dathq.swd302.listingservice.repository;

import com.dathq.swd302.listingservice.model.LegalDocument;
import com.dathq.swd302.listingservice.model.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LegalDocumentRepository extends JpaRepository<LegalDocument, UUID> {
    List<LegalDocument> findByListing_ListingIdOrderByUploadedAtDesc(UUID listingId);;

    List<LegalDocument> findByListing_ListingIdAndDocumentType(UUID listingId, DocumentType documentType);
    Integer countByListing_ListingId(UUID listingId);
    Integer countByListing_ListingIdAndDocumentType(UUID listingId, DocumentType documentType);}
