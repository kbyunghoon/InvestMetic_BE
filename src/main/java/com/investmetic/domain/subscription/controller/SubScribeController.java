package com.investmetic.domain.subscription.controller;

import com.investmetic.domain.subscription.service.SubscriptionService;
import com.investmetic.global.exception.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/strategies/")
public class SubScribeController {
    private final SubscriptionService subscriptionService;
    @GetMapping("/{strategyId}/subscribe")
    public ResponseEntity<BaseResponse<Void>> subscribe(@RequestBody Long userId, @PathVariable Long strategyId) {
        subscriptionService.SubScribe(userId, strategyId);
        return BaseResponse.success();
    }
}
