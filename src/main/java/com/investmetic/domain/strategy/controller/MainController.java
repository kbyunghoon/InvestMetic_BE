package com.investmetic.domain.strategy.controller;

import com.investmetic.domain.strategy.dto.response.TopRankingStrategyResponseDto;
import com.investmetic.domain.strategy.service.MainService;
import com.investmetic.global.common.BaseEntity;
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
public class MainController {
    private final MainService mainService;

    @GetMapping("/top-ranking")
    public ResponseEntity<BaseResponse<List<TopRankingStrategyResponseDto>>> getTopSubscribe(){

        return BaseResponse.success(mainService.getTopSubscriberStrategy());
    }
    @GetMapping("/top-ranking-smscore")
    public ResponseEntity<BaseResponse<List<TopRankingStrategyResponseDto>>> getTopSmScore(){

        return BaseResponse.success(mainService.getTopSmscoreStrategy());
    }
}
