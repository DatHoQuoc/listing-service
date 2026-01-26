package com.dathq.swd302.listingservice.service.impl;

import com.dathq.swd302.listingservice.dto.request.UploadDocumentRequest;
import com.dathq.swd302.listingservice.dto.response.DocumentResponse;
import com.dathq.swd302.listingservice.exception.DocumentNotFoundException;
import com.dathq.swd302.listingservice.exception.ListingNotFoundException;
import com.dathq.swd302.listingservice.exception.UnauthorizedException;
import com.dathq.swd302.listingservice.mapper.DocumentMapper;
import com.dathq.swd302.listingservice.model.LegalDocument;
import com.dathq.swd302.listingservice.model.Listing;
import com.dathq.swd302.listingservice.model.enums.DocumentType;
import com.dathq.swd302.listingservice.repository.LegalDocumentRepository;
import com.dathq.swd302.listingservice.repository.ListingRepository;
import com.dathq.swd302.listingservice.service.DocumentService;
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

import static com.dathq.swd302.listingservice.common.Common.generateDocumentFileName;
import static com.dathq.swd302.listingservice.common.util.ValidationUtil.validateDocumentFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private final ListingRepository listingRepository;
    private final MinIOStorageService minIOStorageService;
    private final LegalDocumentRepository legalDocumentRepository;
    private final DocumentMapper documentMapper;
    @Override
    public DocumentResponse uploadDocument(UUID userId, UUID listingId, MultipartFile file, UploadDocumentRequest request) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if(!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }
        validateDocumentFile(file);
        String fileName = generateDocumentFileName(listingId, file.getOriginalFilename());

        String fileUrl = minIOStorageService.uploadFile(file, fileName, "documents");

        LegalDocument document = new LegalDocument();
        document.setListing(listing);
        document.setDocumentType(request.getDocumentType());
        document.setFileUrl(fileUrl);
        document.setFileName(file.getOriginalFilename());
        document.setFileSize(file.getSize());
        document.setMimeType(file.getContentType());
        document.setDocumentNumber(request.getDocumentNumber());
        document.setIssueDate(request.getIssueDate());
        document.setIssuingAuthority(request.getIssuingAuthority());
        document.setExpiryDate(request.getExpiryDate());
        document.setVerified(false);
        document.setUploadedAt(OffsetDateTime.now());
        LegalDocument savedDocument = legalDocumentRepository.save(document);
        log.info("Document uploaded successfully: {}", savedDocument.getDocumentId());

        return documentMapper.toResponse(savedDocument);
    }

    @Override
    public List<DocumentResponse> uploadDocuments(UUID userId, UUID listingId, List<MultipartFile> files, List<UploadDocumentRequest> requests) {
        log.info("Uploading {} documents for listing: {}", files.size(), listingId);

        if (files.size() != requests.size()) {
            throw new IllegalArgumentException("Number of files must match number of requests");
        }

        return java.util.stream.IntStream.range(0, files.size())
                .mapToObj(i -> uploadDocument(userId, listingId, files.get(i), requests.get(i)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getListingDocuments(UUID userId, UUID listingId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }

        List<LegalDocument> documents = legalDocumentRepository.findByListing_ListingIdOrderByUploadedAtDesc(listingId);
        return documentMapper.toResponseList(documents);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getListingDocumentsByType(UUID userId, UUID listingId, DocumentType documentType) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }

        List<LegalDocument> documents = legalDocumentRepository.findByListing_ListingIdAndDocumentType(listingId, documentType);
        return documentMapper.toResponseList(documents);
    }

    @Override
    public void deleteDocument(UUID userId, UUID listingId, UUID documentId) {
        log.info("Deleting document: {} from listing: {}", documentId, listingId);

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }

        LegalDocument document = legalDocumentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found with id: " + documentId));

        if (!document.getListing().getListingId().equals(listingId)) {
            throw new IllegalArgumentException("Document does not belong to this listing");
        }

        minIOStorageService.deleteFile(document.getFileUrl());
        legalDocumentRepository.delete(document);

        log.info("Document deleted successfully: {}", documentId);
    }

    @Override
    @Transactional(readOnly = true)
    public String generateDownloadUrl(UUID userId, UUID listingId, UUID documentId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }

        LegalDocument document = legalDocumentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found with id: " + documentId));

        if (!document.getListing().getListingId().equals(listingId)) {
            throw new IllegalArgumentException("Document does not belong to this listing");
        }

        return minIOStorageService.generatePresignedUrl(document.getFileUrl(), 3600);
    }

    @Override
    public DocumentResponse updateDocumentMetadata(UUID userId, UUID listingId, UUID documentId, UploadDocumentRequest request) {
        log.info("Updating document metadata: {} for listing: {}", documentId, listingId);

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found with id: " + listingId));

        if (!listing.getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not own this listing");
        }

        LegalDocument document = legalDocumentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found with id: " + documentId));

        if (!document.getListing().getListingId().equals(listingId)) {
            throw new IllegalArgumentException("Document does not belong to this listing");
        }

        document.setDocumentType(request.getDocumentType());
        document.setDocumentNumber(request.getDocumentNumber());
        document.setIssueDate(request.getIssueDate());
        document.setIssuingAuthority(request.getIssuingAuthority());
        document.setExpiryDate(request.getExpiryDate());
        document.setUpdatedAt(OffsetDateTime.now());

        LegalDocument updatedDocument = legalDocumentRepository.save(document);

        return documentMapper.toResponse(updatedDocument);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer countListingDocuments(UUID listingId) {
        return legalDocumentRepository.countByListing_ListingId(listingId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasRequiredDocuments(UUID listingId) {
        Integer ownershipDocCount = legalDocumentRepository.countByListing_ListingIdAndDocumentType(
                listingId,
                DocumentType.OWNERSHIP_CERTIFICATE
        );
        return ownershipDocCount > 0;
    }
}
