package com.investmetic.global.common;

import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class PageResponseDto<T> {
    private List<T> content;        // 실제 데이터
    private int page;               // 현재 페이지 (1부터 시작)
    private int size;               // 페이지 크기
    private long totalElements;     // 전체 요소 수
    private int totalPages;         // 전체 페이지 수
    private boolean first;          // 첫 페이지 여부
    private boolean last;           // 마지막 페이지 여부

    public PageResponseDto(Page<T> page) {
        this.content = page.getContent();
        this.page = page.getNumber() + 1;  // 0부터 시작하므로 1을 더해 1부터 시작하게 함
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.first = page.isFirst();
        this.last = page.isLast();
    }
}