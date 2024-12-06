package com.investmetic.domain.strategy.controller;

import com.investmetic.domain.strategy.dto.request.SearchRequest;
import com.investmetic.domain.strategy.dto.response.SearchInfoResponseDto;
import com.investmetic.domain.strategy.dto.response.common.StrategySimpleResponse;
import com.investmetic.domain.strategy.service.StrategyListingService;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @Operation(summary = "전략 랭킹페이지 진입시 요청(매매유형, 종목 조회) ",
            description = "<a href='https://www.notion.so/6737234016814bd2bbee79e49cc652c8' target='_blank'>API 명세서</a>")
    @GetMapping
    public ResponseEntity<BaseResponse<SearchInfoResponseDto>> loadSearchInfo() {
        return BaseResponse.success(strategyListingService.loadSearchInfo());
    }

    @Operation(summary = "항목 및 알고리즘별 복합 검색(전략 랭킹페이지) ",
            description = "<a href='https://www.notion.so/e40465111e1b4ab2af76849ac76b04b9' target='_blank'>API 명세서</a>")
    @PostMapping
    public ResponseEntity<BaseResponse<PageResponseDto<StrategySimpleResponse>>> search(
            @RequestBody SearchRequest searchRequest,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PageableDefault(size = 8, sort = "cumulativeProfitRate", direction = Direction.DESC) Pageable pageable) {
        Long userId = customUserDetails == null ? null : customUserDetails.getUserId();

        return BaseResponse.success(strategyListingService
                .search(searchRequest, userId, pageable));
    }


    @Operation(summary = "트레이더 전략 목록 조회",
            description = "<a href='https://www.notion.so/3c8846654055444ebc05705357f42528' target='_blank'>API 명세서</a>")
    @GetMapping("/trader/{traderId}")
    public ResponseEntity<BaseResponse<PageResponseDto<StrategySimpleResponse>>> getTraderStrategy(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam("traderId") Long traderId,
            @PageableDefault(size = 4) Pageable pageable) {

        Long userId = customUserDetails == null ? null : customUserDetails.getUserId();

        return BaseResponse.success(strategyListingService.getTraderStrategies(traderId ,pageable, userId));
    }
}
