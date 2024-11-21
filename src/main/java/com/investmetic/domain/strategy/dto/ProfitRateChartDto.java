package com.investmetic.domain.strategy.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({"xAxis", "yAxis"})
public class ProfitRateChartDto {

    private List<String> xAxis; // x축 데이터 ( YYYY-mm-dd)
    private List<Double> yAxis; // 수익률 리스트

    @Builder
    public ProfitRateChartDto(List<String> xAxis, List<Double> yAxis) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
    }
}
