package com.investmetic.domain.strategy.dto.request;

import com.investmetic.domain.strategy.dto.RangeDto;
import com.investmetic.domain.strategy.model.DurationRange;
import com.investmetic.domain.strategy.model.OperationCycle;
import com.investmetic.domain.strategy.model.ProfitRange;
import java.util.List;
import lombok.Getter;

@Getter
public class FilterSearchRequest {
    private String searchWord; // 검색어
    private List<String> tradeType; // 운용방식
    private List<OperationCycle> operationCycle; // 운용주기
    private List<String> stockTypes; // 운용 종목 리스트
    private List<DurationRange> duration; // 기간 (1년 이하, 1~2년 등)
    private List<ProfitRange> profitRange; // 수익률 범위
    private RangeDto principalRange; // 원금 범위
    private RangeDto mddRange; // MDD 범위
    private RangeDto smScoreRange; // SM SCORE 범위
}
