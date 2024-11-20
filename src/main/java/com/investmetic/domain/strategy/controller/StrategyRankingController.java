package com.investmetic.domain.strategy.controller;

import com.investmetic.domain.strategy.dto.request.AlgorithmSearchRequest;
import com.investmetic.domain.strategy.dto.request.FilterSearchRequest;
import com.investmetic.domain.strategy.dto.response.RegisterInfoResponseDto;
import com.investmetic.domain.strategy.dto.response.common.StrategySimpleResponse;
import com.investmetic.domain.strategy.service.StrategyRegisterService;
import com.investmetic.domain.strategy.service.StrategySearchService;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.exception.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


//TODO : 스프링시큐리티 적용시 리팩토링
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/strategies/search")
public class StrategyRankingController {
    private final StrategySearchService strategySearchService;
    private final StrategyRegisterService strategyRegisterService;

    @PostMapping("/filters")
    public ResponseEntity<BaseResponse<PageResponseDto<StrategySimpleResponse>>> searchByFilters(
            @RequestBody FilterSearchRequest filterSearchRequest,
            @RequestParam Long userId,
            @PageableDefault(size = 8, sort = "cumulativeProfitRate", direction = Direction.DESC) Pageable pageable) {
        PageResponseDto<StrategySimpleResponse> result = strategySearchService.searchByFilters(
                filterSearchRequest, userId, pageable);

        return BaseResponse.success(result);
    }

    @PostMapping("/algorithm")
    public ResponseEntity<BaseResponse<PageResponseDto<StrategySimpleResponse>>> searchByAlgorithm(
            @RequestBody AlgorithmSearchRequest algorithmSearchRequest,
            @RequestParam Long userId,
            @PageableDefault(size = 8, sort = "cumulativeProfitRate", direction = Direction.DESC) Pageable pageable) {
        PageResponseDto<StrategySimpleResponse> result = strategySearchService.searchByAlgorithm(
                algorithmSearchRequest, userId, pageable);

        return BaseResponse.success(result);
    }

    // 랭킹 페이지 진입시 요청
    @GetMapping
    public ResponseEntity<BaseResponse<RegisterInfoResponseDto>> loadStrategyRegistrationInfo() {
        return BaseResponse.success(strategyRegisterService.loadStrategyRegistrationInfo());
    }

}
