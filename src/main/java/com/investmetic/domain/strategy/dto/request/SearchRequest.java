package com.investmetic.domain.strategy.dto.request;

import com.investmetic.domain.strategy.dto.RangeDto;
import com.investmetic.domain.strategy.model.AlgorithmType;
import com.investmetic.domain.strategy.model.DurationRange;
import com.investmetic.domain.strategy.model.OperationCycle;
import com.investmetic.domain.strategy.model.ProfitRange;
import java.util.List;
import lombok.Getter;

@Getter
public class SearchRequest {
    private String searchWord; // 검색어
    private List<String> tradeTypeNames; // 매매방식 이름
    private List<OperationCycle> operationCycles; // 운용주기
    private List<String> stockTypeNames; // 운용 종목 이름
    private List<DurationRange> durations; // 기간 (1년 이하, 1~2년 등)
    private List<ProfitRange> profitRanges; // 수익률 범위
    private RangeDto principalRange; // 원금 범위
    private RangeDto mddRange; // MDD 범위
    private RangeDto smScoreRange; // SM SCORE 범위
    private AlgorithmType algorithmType; // 알고리즘 타입
}
