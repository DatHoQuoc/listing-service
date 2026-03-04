package com.dathq.swd302.listingservice.dto.response;

public record CreditLockResponse(boolean success,
                                 boolean freePost,
                                 int creditCost,
                                 String referenceId,
                                 String message) {
}
