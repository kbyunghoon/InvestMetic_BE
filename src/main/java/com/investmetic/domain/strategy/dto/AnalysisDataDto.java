package com.investmetic.domain.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnalysisDataDto {
    private final String date;     // X축 데이터 (날짜)
    private final Double yAxis;   // Y축 데이터 (단일 옵션)
}