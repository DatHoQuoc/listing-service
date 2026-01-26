package com.dathq.swd302.listingservice.controller;
import java.util.List;
import java.util.UUID;

import com.dathq.swd302.listingservice.dto.request.UploadDocumentRequest;
import com.dathq.swd302.listingservice.dto.response.DocumentResponse;
import com.dathq.swd302.listingservice.model.enums.DocumentType;
import com.dathq.swd302.listingservice.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequestMapping("/api/v1/listings/{listingId}/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse> uploadDocument(
            @PathVariable UUID listingId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestPart("file") MultipartFile file,
            @RequestPart("data") @Valid UploadDocumentRequest request) {

        //UUID userId = SecurityUtils.getCurrentUserId();
        DocumentResponse response = documentService.uploadDocument(userId, listingId, file, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<DocumentResponse>> uploadDocuments(
            @PathVariable UUID listingId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestPart("files") List<MultipartFile> files,
            @RequestPart("data") @Valid List<UploadDocumentRequest> requests) {

        //UUID userId = SecurityUtils.getCurrentUserId();
        List<DocumentResponse> responses = documentService.uploadDocuments(userId, listingId, files, requests);
        return new ResponseEntity<>(responses, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getListingDocuments(
            @PathVariable UUID listingId,
            @RequestHeader("X-User-Id") UUID userId) {

        //UUID userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(documentService.getListingDocuments(userId, listingId));
    }

    @GetMapping("/type/{documentType}")
    public ResponseEntity<List<DocumentResponse>> getDocumentsByType(
            @PathVariable UUID listingId,
            @PathVariable DocumentType documentType,
            @RequestHeader("X-User-Id") UUID userId) {

        //UUID userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(documentService.getListingDocumentsByType(userId, listingId, documentType));
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable UUID listingId,
            @PathVariable UUID documentId,
            @RequestHeader("X-User-Id") UUID userId) {

        //UUID userId = SecurityUtils.getCurrentUserId();
        documentService.deleteDocument(userId, listingId, documentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{documentId}/download")
    public ResponseEntity<String> getDownloadUrl(
            @PathVariable UUID listingId,
            @PathVariable UUID documentId,
            @RequestHeader("X-User-Id") UUID userId) {

        //UUID userId = SecurityUtils.getCurrentUserId();
        String url = documentService.generateDownloadUrl(userId, listingId, documentId);
        return ResponseEntity.ok(url);
    }

    @PutMapping("/{documentId}")
    public ResponseEntity<DocumentResponse> updateDocumentMetadata(
            @PathVariable UUID listingId,
            @PathVariable UUID documentId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody @Valid UploadDocumentRequest request) {

        //UUID userId = SecurityUtils.getCurrentUserId();
        DocumentResponse response = documentService.updateDocumentMetadata(userId, listingId, documentId, request);
        return ResponseEntity.ok(response);
    }
}
