package com.dathq.swd302.listingservice.dto.request;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestChangesRequest {
    @NotBlank(message = "Feedback to seller is required")
    private String feedbackToSeller;

    private String staffNotesInternal;

    @NotEmpty(message = "Required changes cannot be empty")
    @Valid
    private List<RequiredChange> requiredChanges;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequiredChange {
        @NotBlank(message = "Field is required")
        private String field;

        @NotBlank(message = "Issue is required")
        private String issue;

        private String suggestion;
    }
}
