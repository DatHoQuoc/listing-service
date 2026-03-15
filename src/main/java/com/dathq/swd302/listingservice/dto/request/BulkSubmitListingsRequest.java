package com.dathq.swd302.listingservice.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkSubmitListingsRequest {

    @NotEmpty(message = "listingIds must not be empty")
    private List<@NotNull(message = "listingId must not be null") UUID> listingIds;
}
