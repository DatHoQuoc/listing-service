package com.dathq.swd302.listingservice.dto.response;

import com.dathq.swd302.listingservice.model.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
    private UUID documentId;
    private UUID listingId;
    private DocumentType documentType;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private String mimeType;
    private String documentNumber;
    private OffsetDateTime issueDate;
    private String issuingAuthority;
    private OffsetDateTime expiryDate;
    private boolean verified;
    private String verificationNotes;
    private OffsetDateTime uploadedAt;
    private OffsetDateTime updatedAt;
}
