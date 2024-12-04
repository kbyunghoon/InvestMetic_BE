package com.investmetic.domain.qna.dto;

public enum SearchCondition {
    TITLE,                 // 제목
    CONTENT,               // 내용
    TITLE_OR_CONTENT,      // 제목 + 내용
    TRADER_NAME,           // 트레이더명
    INVESTOR_NAME,         // 투자자명
    STRATEGY_NAME          // 전략명
}
