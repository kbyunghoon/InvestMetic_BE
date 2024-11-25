package com.investmetic.domain.strategy.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({"dates", "data"}) // dates가 먼저, data가 나중에 출력
public class StrategyAnalysisResponse {

    private List<String> dates; // x축 데이터 ( YYYY-mm-dd)
    private Map<String,List<Double>> data; // 조건 항목별 리스트

    @Builder
    public StrategyAnalysisResponse(List<String> dates, Map<String, List<Double>> data) {
        this.dates = dates;
        this.data = data;
    }
}
