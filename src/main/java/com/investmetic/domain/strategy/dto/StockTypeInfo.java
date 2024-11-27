package com.investmetic.domain.strategy.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StockTypeInfo {
    private List<String> stockTypeIconUrls;         // 종목 아이콘 이미지 경로 리스트
    private List<String> stockTypeNames;            // 종목 이름 리스트
}
