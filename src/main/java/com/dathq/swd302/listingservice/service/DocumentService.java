package com.dathq.swd302.listingservice.service;



import com.dathq.swd302.listingservice.dto.request.UploadDocumentRequest;
import com.dathq.swd302.listingservice.dto.response.DocumentResponse;
import com.dathq.swd302.listingservice.model.enums.DocumentType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
public interface DocumentService {
    /**
     * Upload a legal document
     * - Validates file type (pdf, doc, docx, jpg, png for scanned docs)
     * - Validates file size (max 25MB)
     * - Uploads to MinIO
     * - Creates record in legal_documents table
     * - Sets verified=false by default
     *
     * @param userId user ID
     * @param listingId listing ID
     * @param file document file
     * @param request document metadata (type, number, issue date, etc.)
     * @return document response with ID and URL
     * @throws //InvalidFileTypeException if file type not allowed
     * @throws //FileSizeExceededException if file too large
     */
    DocumentResponse uploadDocument(UUID userId, UUID listingId, MultipartFile file,
                                    UploadDocumentRequest request);

    /**
     * Upload multiple documents
     *
     * @param userId user ID
     * @param listingId listing ID
     * @param files document files
     * @param requests list of document metadata
     * @return list of document responses
     */
    List<DocumentResponse> uploadDocuments(UUID userId, UUID listingId,
                                           List<MultipartFile> files,
                                           List<UploadDocumentRequest> requests);

    /**
     * Get all documents for a listing
     * - Returns documents sorted by uploaded_at DESC
     *
     * @param userId user ID
     * @param listingId listing ID
     * @return list of documents
     */
    List<DocumentResponse> getListingDocuments(UUID userId, UUID listingId);

    /**
     * Get documents by type
     *
     * @param userId user ID
     * @param listingId listing ID
     * @param documentType document type filter
     * @return filtered documents
     */
    List<DocumentResponse> getListingDocumentsByType(UUID userId, UUID listingId,
                                                     DocumentType documentType);

    /**
     * Delete a document
     * - Removes from MinIO storage
     * - Deletes from legal_documents table
     *
     * @param userId user ID
     * @param listingId listing ID
     * @param documentId document ID to delete
     */
    void deleteDocument(UUID userId, UUID listingId, UUID documentId);

    /**
     * Generate presigned download URL
     * - Creates temporary download URL (valid 1 hour)
     * - For secure document downloads
     *
     * @param userId user ID
     * @param listingId listing ID
     * @param documentId document ID
     * @return presigned URL
     */
    String generateDownloadUrl(UUID userId, UUID listingId, UUID documentId);

    /**
     * Update document metadata
     * - Updates document number, issue date, issuing authority
     * - Does not change file itself
     *
     * @param userId user ID
     * @param listingId listing ID
     * @param documentId document ID
     * @param request updated metadata
     * @return updated document response
     */
    DocumentResponse updateDocumentMetadata(UUID userId, UUID listingId, UUID documentId,
                                            UploadDocumentRequest request);

    /**
     * Count documents for a listing
     *
     * @param listingId listing ID
     * @return number of documents
     */
    Integer countListingDocuments(UUID listingId);

    /**
     * Check if listing has required documents
     * - Checks if at least one ownership document exists
     * - Returns validation result
     *
     * @param listingId listing ID
     * @return true if has required documents
     */
    boolean hasRequiredDocuments(UUID listingId);
}
