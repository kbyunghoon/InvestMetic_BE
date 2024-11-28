package com.investmetic.domain.strategy.controller;

import com.investmetic.domain.strategy.dto.response.TopRankingStrategyResponseDto;
import com.investmetic.domain.strategy.service.MainPageService;
import com.investmetic.global.exception.BaseResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/strategies/")
public class MainPageController {
    private final MainPageService mainPageService;

    @GetMapping("/top-ranking")
    public ResponseEntity<BaseResponse<List<TopRankingStrategyResponseDto>>> getTopSubscribe(){

        return BaseResponse.success(mainPageService.getTopSubscriberStrategy());
    }
    @GetMapping("/top-ranking-smscore")
    public ResponseEntity<BaseResponse<List<TopRankingStrategyResponseDto>>> getTopSmScore(){

        return BaseResponse.success(mainPageService.getTopSmscoreStrategy());
    }
}
