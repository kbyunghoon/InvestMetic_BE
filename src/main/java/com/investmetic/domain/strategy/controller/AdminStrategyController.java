package com.investmetic.domain.strategy.controller;

import com.investmetic.domain.strategy.dto.response.AdminStrategyResponseDto;
import com.investmetic.domain.strategy.model.IsApproved;
import com.investmetic.domain.strategy.service.AdminStrategyService;
import com.investmetic.domain.strategy.service.StrategyService;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.SuccessCode;
import com.querydsl.core.annotations.QueryProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/admin/strategies")
@RequiredArgsConstructor
public class AdminStrategyController {
    private final AdminStrategyService adminStrategyService;
    @PatchMapping("/{strategyId}")
    public ResponseEntity<BaseResponse<Void>> updateStrategy(@PathVariable("strategyId") Long strategyId, IsApproved isApproved) {
        adminStrategyService.manageAproveState(strategyId, isApproved);
        return BaseResponse.success(SuccessCode.UPDATED);
    }
    @GetMapping("/")
    public ResponseEntity<BaseResponse<PageResponseDto<AdminStrategyResponseDto>>> getStrategies(
            @PageableDefault(size=10, page=1) Pageable pageable,
            @RequestParam(required = false) String searchWord,
            @RequestParam IsApproved isApproved
    ) {
        return BaseResponse.success(adminStrategyService.getManageStrategies(pageable, searchWord, isApproved));
    }
}
