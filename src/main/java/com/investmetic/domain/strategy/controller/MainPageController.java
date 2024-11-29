package com.investmetic.domain.strategy.controller;

import com.investmetic.domain.strategy.dto.response.TopRankingStrategyResponseDto;
import com.investmetic.domain.strategy.service.MainPageService;
import com.investmetic.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main/")
@Tag(name = "메인 페이지 데이터 조회 API", description = "메인 페이지 데이터 조회 관련 API")
public class MainPageController {
    private final MainPageService mainPageService;

    @Operation(summary = "구독수 상위 전략 3개 조회(메인 페이지)",
            description = "<a href='https://www.notion.so/52efe819a5c24a899aa5a0b310c450b1' target='_blank'>API 명세서</a>")
    @GetMapping("/top-ranking")
    public ResponseEntity<BaseResponse<List<TopRankingStrategyResponseDto>>> getTopSubscribe() {

        return BaseResponse.success(mainPageService.getTopSubscriberStrategy());
    }

    @Operation(summary = "smScore 상위 전략 5개 조회(메인 페이지)",
            description = "<a href='https://www.notion.so/smscore-11cb5822050d4f6dbb5c678732c59b50' target='_blank'>API 명세서</a>")
    @GetMapping("/top-ranking-smscore")
    public ResponseEntity<BaseResponse<List<TopRankingStrategyResponseDto>>> getTopSmScore() {

        return BaseResponse.success(mainPageService.getTopSmscoreStrategy());
    }
}
