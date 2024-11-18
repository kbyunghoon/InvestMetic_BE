package com.investmetic.domain.strategy.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({"xAxis", "yAxis"}) // xAxis가 먼저, yAxis가 나중에 출력
public class StrategyAnalysisResponse {

    private List<String> xAxis; // x축 데이터 ( YYYY-mm-dd)
    private Map<String,List<Double>> yAxis; // 조건 항목별 리스트

    @Builder
    public StrategyAnalysisResponse(List<String> xAxis, Map<String, List<Double>> yAxis) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
    }
}
