package com.dathq.swd302.listingservice.controller;

import java.util.List;
import java.util.UUID;

import com.dathq.swd302.listingservice.dto.request.UploadDocumentRequest;
import com.dathq.swd302.listingservice.dto.response.DocumentResponse;
import com.dathq.swd302.listingservice.model.enums.DocumentType;
import com.dathq.swd302.listingservice.security.JwtClaims;
import com.dathq.swd302.listingservice.security.JwtUser;
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
            @JwtUser JwtClaims claims,
            @PathVariable UUID listingId,
            @RequestParam("file") MultipartFile file,
            @RequestBody UploadDocumentRequest uploadDocumentRequest) {

        DocumentResponse response = documentService.uploadDocument(claims.getUserId(), listingId, file, uploadDocumentRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<DocumentResponse>> uploadDocuments(
            @JwtUser JwtClaims claims,
            @PathVariable UUID listingId,
            @RequestParam("files") List<MultipartFile> files,
            @RequestBody List<UploadDocumentRequest> uploadDocumentRequests) {

        List<DocumentResponse> response = documentService.uploadDocuments(claims.getUserId(), listingId, files,
                uploadDocumentRequests);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getListingDocuments(
            @PathVariable UUID listingId,
            @JwtUser JwtClaims claims) {

        return ResponseEntity.ok(documentService.getListingDocuments(claims.getUserId(), listingId));
    }

    @GetMapping("/type/{documentType}")
    public ResponseEntity<List<DocumentResponse>> getDocumentsByType(
            @PathVariable UUID listingId,
            @PathVariable DocumentType documentType,
            @JwtUser JwtClaims claims) {

        return ResponseEntity
                .ok(documentService.getListingDocumentsByType(claims.getUserId(), listingId, documentType));
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteDocument(
            @JwtUser JwtClaims claims,
            @PathVariable UUID listingId,
            @PathVariable("documentId") UUID documentId) {

        documentService.deleteDocument(claims.getUserId(), listingId, documentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{documentId}/download")
    public ResponseEntity<String> getDownloadUrl(
            @PathVariable UUID listingId,
            @PathVariable UUID documentId,
            @RequestHeader("X-User-Id") UUID userId) {

        // UUID userId = SecurityUtils.getCurrentUserId();
        String url = documentService.generateDownloadUrl(userId, listingId, documentId);
        return ResponseEntity.ok(url);
    }

    @PutMapping("/{documentId}")
    public ResponseEntity<DocumentResponse> updateDocumentMetadata(
            @PathVariable UUID listingId,
            @PathVariable UUID documentId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody @Valid UploadDocumentRequest request) {

        // UUID userId = SecurityUtils.getCurrentUserId();
        DocumentResponse response = documentService.updateDocumentMetadata(userId, listingId, documentId, request);
        return ResponseEntity.ok(response);
    }
}
