package com.dathq.swd302.listingservice.model;
import com.dathq.swd302.listingservice.model.enums.DocumentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "legal_documents")
@Data
@NoArgsConstructor
public class LegalDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID documentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id")
    private Listing listing;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType documentType;

    @Column(nullable = false)
    private String fileUrl;

    @Column(nullable = false)
    private String fileName;

    private Long fileSize;
    private String mimeType;

    // Legal Info
    private String documentNumber;
    private LocalDate issueDate;
    private String issuingAuthority;
    private LocalDate expiryDate;

    // Verification
    private boolean verified = false;
    private UUID verifiedBy; // Admin ID
    private Instant verifiedAt;
    private String verificationNotes;

    @CreationTimestamp
    private Instant uploadedAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
