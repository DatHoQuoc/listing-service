package com.dathq.swd302.listingservice.service;

public interface DocumentService {
    /**
     * Upload a property document
     * - Validates file type (pdf, doc, docx)
     * - Validates file size (max 25MB)
     * - Uploads to MinIO
     * - Updates listing's documents_json field
     * - Scans for viruses (optional)
     *
     * @param listingId the listing to attach document to
     * @param file the document file
     * @param documentType type of document (ownership, permit, contract, etc.)
     * @return upload response with URL and metadata
     */
    DocumentUploadResponse uploadDocument(UUID listingId, MultipartFile file, String documentType);

    /**
     * Upload multiple documents at once
     *
     * @param listingId the listing to attach documents to
     * @param files array of document files
     * @param documentTypes corresponding document types
     * @return list of upload responses
     */
    List<DocumentUploadResponse> uploadDocuments(UUID listingId, List<MultipartFile> files, List<String> documentTypes);

    /**
     * Delete a document from a listing
     * - Removes from MinIO
     * - Updates listing's documents_json
     *
     * @param listingId the listing ID
     * @param documentUrl the document URL to delete
     */
    void deleteDocument(UUID listingId, String documentUrl);

    /**
     * Get all documents for a listing
     *
     * @param listingId the listing ID
     * @return list of document metadata (URL, type, filename, uploadedAt)
     */
    List<DocumentUploadResponse> getListingDocuments(UUID listingId);

    /**
     * Generate a presigned download URL for a document
     * - Temporary URL valid for 1 hour
     * - For secure document access
     *
     * @param listingId the listing ID
     * @param documentUrl the document URL
     * @return presigned download URL
     */
    String generateDownloadUrl(UUID listingId, String documentUrl);
}
