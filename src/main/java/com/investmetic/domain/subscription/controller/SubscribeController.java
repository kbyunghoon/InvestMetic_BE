package com.investmetic.domain.subscription.controller;

import com.investmetic.domain.subscription.service.SubscriptionService;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/strategies/")
@Tag(name = "전략 구독 관리 기능", description = "전략 구독 관리 기능 관련 API")
public class SubscribeController {
    private final SubscriptionService subscriptionService;

    @Operation(summary = "전략 구독, 구독 취소 상태 관리 기능",
            description = "<a href='https://www.notion.so/dfae7b994dd94761a77f70869bf9d479' target='_blank'>API 명세서</a>")
    @GetMapping("/{strategyId}/subscribe")
    @PreAuthorize("hasRole('ROLE_TRADER') or hasRole('ROLE_INVESTOR')")
    public ResponseEntity<BaseResponse<Void>> subscribe(
            @PathVariable Long strategyId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        subscriptionService.subscribe(strategyId, customUserDetails.getUserId());
        return BaseResponse.success();
    }
}
