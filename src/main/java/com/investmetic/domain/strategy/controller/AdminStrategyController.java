package com.investmetic.domain.strategy.controller;

import com.investmetic.domain.strategy.dto.response.AdminStrategyResponseDto;
import com.investmetic.domain.strategy.model.IsApproved;
import com.investmetic.domain.strategy.service.AdminStrategyService;

import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/admin/strategies")
@RequiredArgsConstructor
@Tag(name = "전략 관리 페이지 API", description = "전략 관리 페이지 관련 API")
public class AdminStrategyController {
    private final AdminStrategyService adminStrategyService;

    @Operation(summary = "전략 승인, 승인 거부 상태 관리 기능",
            description = "<a href='https://www.notion.so/a0c8e8de1b264278a44dedda4b4d4ca0' target='_blank'>API 명세서</a>")
    @PatchMapping("/{strategyId}")
    @PreAuthorize("hasAnyRole('ROLE_TRADER_ADMIN', 'ROLE_INVESTOR_ADMIN')")
    public ResponseEntity<BaseResponse<Void>> updateStrategy(@PathVariable("strategyId") Long strategyId,
                                                             IsApproved isApproved) {
        adminStrategyService.manageAproveState(strategyId, isApproved);
        return BaseResponse.success(SuccessCode.UPDATED);
    }
    @Operation(summary = "관리자 페이지 전략 목록 조회 기능",
    description = "<a href='https://www.notion.so/3cf42fd2349d4a0488b0dde773058ac9' target='_blank'>API 명세서</a>")
    @GetMapping("")
    public ResponseEntity<BaseResponse<PageResponseDto<AdminStrategyResponseDto>>> getStrategies(
            @PageableDefault(size=10, page=0) Pageable pageable,
            @RequestParam(required = false) String searchWord,
            @RequestParam(required = false) IsApproved isApproved
    ) {
        return BaseResponse.success(adminStrategyService.getManageStrategies(pageable, searchWord, isApproved));
    }
}
