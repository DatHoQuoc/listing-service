package com.dathq.swd302.listingservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RejectListingRequest {
    @NotBlank(message = "Rejection reason is required")
    private String rejectionReason;

    @NotBlank(message = "Feedback to seller is required")
    private String feedbackToSeller;

    private String staffNotesInternal;
}
