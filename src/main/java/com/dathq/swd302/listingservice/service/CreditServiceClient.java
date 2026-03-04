package com.dathq.swd302.listingservice.service;


import com.dathq.swd302.listingservice.dto.request.LockCreditRequest;
import com.dathq.swd302.listingservice.dto.request.ResolvePostRequest;
import com.dathq.swd302.listingservice.dto.response.CreditLockResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "credit-service", url = "${services.credit.url}")
public interface CreditServiceClient {
    @PostMapping("/api/v1/credits/usage/post/lock")
    CreditLockResponse lockCreditForPost(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody LockCreditRequest request
    );

    @PostMapping("/api/v1/credits/usage/post/resolve")
    void resolvePost(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody ResolvePostRequest request
    );
}
