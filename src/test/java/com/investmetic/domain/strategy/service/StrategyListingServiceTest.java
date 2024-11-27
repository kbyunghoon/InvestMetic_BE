package com.investmetic.domain.strategy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.investmetic.domain.strategy.dto.object.StockTypeInfo;
import com.investmetic.domain.strategy.dto.request.SearchRequest;
import com.investmetic.domain.strategy.dto.response.common.StrategySimpleResponse;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.global.common.PageResponseDto;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
class StrategyListingServiceTest {
    @Mock
    private StrategyRepository strategyRepository;

    @InjectMocks
    private StrategyListingService strategyListingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("필터 기반 전략 목록 조회 테스트")
    void testSearchByFilters() {
        // Given: 필터 검색 요청과 Mock 데이터 준비
        SearchRequest request = new SearchRequest();
        Long userId = 1L; // 로그인한 사용자 ID
        PageRequest pageable = PageRequest.of(0, 10); // 페이징 요청

        // Mock 데이터 (전략 목록)
        List<StrategySimpleResponse> mockContent = List.of(
                new StrategySimpleResponse(1L, "전략 1", "user1Img",
                        "user1", "icon1", "매매유형1", 10L, 5.0, 15.0, 10.0, 100, 4.5, 20),
                new StrategySimpleResponse(2L, "전략 2", "user2Img",
                        "user2", "icon2", "매매유형2", 20L, 6.0, 25.0, 15.0, 200, 4.0, 30)
        );
        Page<StrategySimpleResponse> mockPage = new PageImpl<>(mockContent);

        // StockTypeInfo Mock 데이터
        Map<Long, StockTypeInfo> stockTypeInfoMap = Map.of(
                1L, new StockTypeInfo(List.of("전략목록 아이콘1", "전략목록 아이콘2"), List.of("종목 이름1", "종목 이름2")),
                2L, new StockTypeInfo(List.of("전략목록 아이콘2"), List.of("종목 이름3"))
        );

        when(strategyRepository.searchBy(request, pageable)).thenReturn(mockPage);
        when(strategyRepository.findBySubscriptionMap(eq(userId), anyList()))
                .thenReturn(Map.of(1L, true, 2L, false));
        when(strategyRepository.findStockTypeInfoMap(anyList())).thenReturn(stockTypeInfoMap);

        // When
        PageResponseDto<StrategySimpleResponse> response = strategyListingService.search(request, userId, pageable);

        // Then
        assertThat(response).isNotNull(); // 응답이 null이 아님
        assertThat(response.getContent()).hasSize(2); // 두 개의 전략 반환
        assertThat(response.getContent().get(0).getStrategyName()).isEqualTo("전략 1"); // 첫 번째 전략 이름 확인
        assertThat(response.getContent().get(1).getStrategyName()).isEqualTo("전략 2"); // 두 번째 전략 이름 확인

        // StockTypeInfo 검증
        assertThat(response.getContent().get(0).getStockTypeInfo().getStockTypeIconUrls())
                .containsExactly("전략목록 아이콘1", "전략목록 아이콘2");
        assertThat(response.getContent().get(0).getStockTypeInfo().getStockTypeNames())
                .containsExactly("종목 이름1", "종목 이름2");

        assertThat(response.getContent().get(1).getStockTypeInfo().getStockTypeIconUrls())
                .containsExactly("전략목록 아이콘2");
        assertThat(response.getContent().get(1).getStockTypeInfo().getStockTypeNames())
                .containsExactly("종목 이름3");

        // 구독 여부 확인
        assertThat(response.getContent().get(0).getIsSubscribed()).isTrue(); // 첫 번째 전략 구독 여부 확인
        assertThat(response.getContent().get(1).getIsSubscribed()).isFalse(); // 두 번째 전략 구독 여부 확인
    }

    @Test
    @DisplayName("구독한 전략 목록 조회 테스트")
    void testGetSubscribedStrategies() {
        // Given: 사용자 ID와 Mock 데이터 준비
        Long userId = 1L;
        PageRequest pageable = PageRequest.of(0, 10);

        // Mock 데이터 (구독한 전략 목록)
        List<StrategySimpleResponse> mockContent = List.of(
                new StrategySimpleResponse(1L, "전략 1", "user1Img",
                        "user1", "icon1", "매매유형1", 10L, 5.0, 15.0, 10.0, 100, 4.5, 20),
                new StrategySimpleResponse(2L, "전략 2", "user2Img",
                        "user2", "icon2", "매매유형2", 20L, 6.0, 25.0, 15.0, 200, 4.0, 30)
        );
        Page<StrategySimpleResponse> mockPage = new PageImpl<>(mockContent);

        // StockTypeInfo Mock 데이터
        Map<Long, StockTypeInfo> stockTypeInfoMap = Map.of(
                1L, new StockTypeInfo(List.of("icon1"), List.of("종목 이름1")),
                2L, new StockTypeInfo(List.of("icon2"), List.of("종목 이름2"))
        );

        when(strategyRepository.findSubscribedStrategies(userId, pageable)).thenReturn(mockPage);
        when(strategyRepository.findStockTypeInfoMap(anyList())).thenReturn(stockTypeInfoMap);

        // When
        PageResponseDto<StrategySimpleResponse> response = strategyListingService.getSubscribedStrategies(userId, pageable);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(2);

        // StockTypeInfo 검증
        assertThat(response.getContent().get(0).getStockTypeInfo().getStockTypeIconUrls())
                .containsExactly("icon1");
        assertThat(response.getContent().get(0).getStockTypeInfo().getStockTypeNames())
                .containsExactly("종목 이름1");

        assertThat(response.getContent().get(1).getStockTypeInfo().getStockTypeIconUrls())
                .containsExactly("icon2");
        assertThat(response.getContent().get(1).getStockTypeInfo().getStockTypeNames())
                .containsExactly("종목 이름2");

        // 구독 여부 확인
        assertThat(response.getContent().get(0).getIsSubscribed()).isTrue(); // 구독된 상태
        assertThat(response.getContent().get(1).getIsSubscribed()).isTrue(); // 구독된 상태
    }
}
