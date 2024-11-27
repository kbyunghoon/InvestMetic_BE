package com.investmetic.domain.strategy.controller;

import com.investmetic.domain.strategy.model.IsApproved;
import com.investmetic.domain.strategy.service.AdminStrategyService;
import com.investmetic.domain.strategy.service.StrategyService;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
