package com.investmetic.domain.strategy.controller;

import com.investmetic.domain.strategy.dto.request.FilterSearchRequest;
import com.investmetic.domain.strategy.dto.response.RegisterInfoResponseDto;
import com.investmetic.domain.strategy.dto.response.common.StrategySimpleResponse;
import com.investmetic.domain.strategy.model.AlgorithmType;
import com.investmetic.domain.strategy.service.StrategyListingService;
import com.investmetic.domain.strategy.service.StrategyRegisterService;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "전략 랭킹페이지 API", description = "전략 랭킹페이지 관련 API")
public class StrategyRankingController {
    private final StrategyListingService strategyListingService;
    private final StrategyRegisterService strategyRegisterService;

    @Operation(summary = "전략 항목별 검색(전략 랭킹페이지) ",
            description = "<a href='https://www.notion.so/d547c3ca558d42239f45ddf4e40e113d' target='_blank'>API 명세서</a>")
    @PostMapping("/filters")
    public ResponseEntity<BaseResponse<PageResponseDto<StrategySimpleResponse>>> searchByFilters(
            @RequestBody FilterSearchRequest filterSearchRequest,
            @RequestParam Long userId,
            @PageableDefault(size = 8, sort = "cumulativeProfitRate", direction = Direction.DESC) Pageable pageable) {
        PageResponseDto<StrategySimpleResponse> result = strategyListingService.searchByFilters(
                filterSearchRequest, userId, pageable);

        return BaseResponse.success(result);
    }

    @Operation(summary = "전략 알고리즘별 검색(전략 랭킹페이지) ",
            description = "<a href='https://www.notion.so/10dad66378804aedb83e9672ea419329' target='_blank'>API 명세서</a>")
    @GetMapping("/algorithm")
    public ResponseEntity<BaseResponse<PageResponseDto<StrategySimpleResponse>>> searchByAlgorithm(
            @RequestParam(required = false) String searchWord,
            @RequestParam(required = false) AlgorithmType algorithmType,
            @RequestParam Long userId,
            @PageableDefault(size = 8, sort = "cumulativeProfitRate", direction = Direction.DESC) Pageable pageable) {
        PageResponseDto<StrategySimpleResponse> result = strategyListingService
                .searchByAlgorithm(searchWord, algorithmType, userId, pageable);

        return BaseResponse.success(result);
    }


    // 랭킹 페이지 진입시 요청
    @Operation(summary = "전략 랭킹페이지 진입시 요청(매매유형, 종목 조회) ",
            description = "<a href='https://www.notion.so/6737234016814bd2bbee79e49cc652c8' target='_blank'>API 명세서</a>")
    @GetMapping
    public ResponseEntity<BaseResponse<RegisterInfoResponseDto>> loadStrategyRegistrationInfo() {
        return BaseResponse.success(strategyRegisterService.loadStrategyRegistrationInfo());
    }

}
