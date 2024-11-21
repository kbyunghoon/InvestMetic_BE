package com.investmetic.domain.subscription.controller;

import com.investmetic.domain.subscription.service.SubscriptionService;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/strategies/")
public class SubScribeController {
    private final SubscriptionService subscriptionService;
    @GetMapping("/{strategyId}/subscribe")
    public ResponseEntity<BaseResponse<Void>> subscribe(
            @PathVariable Long strategyId,
            @RequestParam Long userId
    ) {
        subscriptionService.SubScribe(strategyId, userId);
        return BaseResponse.success();
    }
}
