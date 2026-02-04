package com.dathq.swd302.listingservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproveListingRequest {
    @NotBlank(message = "Staff notes are required")
    private String staffNotesInternal;

    private String feedbackToSeller;

    @NotNull(message = "Checklist results are required")
    private Map<String, Boolean> checklist;
}
