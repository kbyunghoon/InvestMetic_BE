package com.investmetic.domain.strategy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.investmetic.domain.TestEntity.TestEntityFactory;
import com.investmetic.domain.strategy.dto.StockTypeDto;
import com.investmetic.domain.strategy.dto.request.StrategyModifyRequestDto;
import com.investmetic.domain.strategy.dto.request.StrategyRegisterRequestDto;
import com.investmetic.domain.strategy.dto.TradeTypeDto;
import com.investmetic.domain.strategy.dto.response.RegisterInfoResponseDto;
import com.investmetic.domain.strategy.model.MinimumInvestmentAmount;
import com.investmetic.domain.strategy.model.OperationCycle;
import com.investmetic.domain.strategy.model.entity.StockType;
import com.investmetic.domain.strategy.model.entity.StockTypeGroup;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.model.entity.TradeType;
import com.investmetic.domain.strategy.repository.StockTypeGroupRepository;
import com.investmetic.domain.strategy.repository.StockTypeRepository;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.strategy.repository.TradeTypeRepository;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.dto.PresignedUrlResponseDto;
import com.investmetic.global.dto.ProposalFileDto;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.util.s3.FilePath;
import com.investmetic.global.util.s3.S3FileService;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
class StrategyInfoServiceTest {

    @Mock
    private TradeTypeRepository tradeTypeRepository;

    @Mock
    private StockTypeRepository stockTypeRepository;

    @Mock
    private StockTypeGroupRepository stockTypeGroupRepository;

    @Mock
    private UserRepository userRepository;

    private StrategyRegisterRequestDto requestDto;
    private User user;
    private Strategy strategy;

    private List<TradeType> tradeTypeList;
    private List<StockType> stockTypeList;

    @Mock
    private S3FileService s3FileService;

    @Mock
    private StrategyRepository strategyRepository;

    @BeforeEach
    void setUp() {

        tradeTypeList = List.of(
                TradeType.builder().tradeTypeId(1L).tradeTypeName("TradeType1").activateState(true)
                        .tradeTypeIconURL("https://example.com/TradeType1.png").build(),
                TradeType.builder().tradeTypeId(2L).tradeTypeName("TradeType2").activateState(true)
                        .tradeTypeIconURL("https://example.com/TradeType2.png").build()
        );

        stockTypeList = List.of(
                StockType.builder().stockTypeId(1L).stockTypeName("StockType1").activateState(true)
                        .stockTypeIconURL("https://example.com/StockType1.png").build(),
                StockType.builder().stockTypeId(2L).stockTypeName("StockType2").activateState(true)
                        .stockTypeIconURL("https://example.com/StockType2.png").build()
        );

        user = User.builder()
                .userName("testUser")
                .nickname("Tester")
                .email("test@example.com")
                .build();

        requestDto = StrategyRegisterRequestDto.builder()
                .strategyName("Test Strategy")
                .tradeTypeId(1L)
                .operationCycle(OperationCycle.DAY)
                .stockTypeIds(List.of(1L, 2L))
                .minimumInvestmentAmount(MinimumInvestmentAmount.ABOVE_100M)
                .proposalFile(ProposalFileDto.builder()
                        .proposalFileName("test.xls")
                        .proposalFileSize(1024)
                        .build())
                .build();

        TradeType tradeType = TestEntityFactory.createTestTradeType();

        strategy = TestEntityFactory.createTestStrategy(user, tradeType);
    }


}