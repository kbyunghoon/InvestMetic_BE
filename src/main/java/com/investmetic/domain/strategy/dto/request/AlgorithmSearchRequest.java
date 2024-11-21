package com.investmetic.domain.strategy.dto.request;

import com.investmetic.domain.strategy.model.AlgorithmType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlgorithmSearchRequest {
    private String searchWord; // 검색어
    private AlgorithmType algorithmType; // 알고리즘 타입
}
