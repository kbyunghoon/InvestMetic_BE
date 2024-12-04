package com.investmetic.domain.strategy.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TotalStrategyMetricsResponseDto {
    private List<String> dates; // x축 데이터 ( YYYY-mm-dd)
    private Map<String,List<Double>> data; // 조건 항목별 리스트

    @Builder
    public TotalStrategyMetricsResponseDto(List<String> dates, Map<String,List<Double>> data) {
        this.dates = dates;
        this.data = data;
    }
}
