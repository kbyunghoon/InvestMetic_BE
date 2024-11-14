package com.investmetic.domain.strategy.service;

import com.investmetic.domain.strategy.dto.StockTypeDto;
import com.investmetic.domain.strategy.dto.StrategyRegisterRequestDto;
import com.investmetic.domain.strategy.dto.TradeTypeDto;
import com.investmetic.domain.strategy.dto.response.RegisterInfoResponseDto;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.model.entity.TradeType;
import com.investmetic.domain.strategy.repository.StockTypeRepository;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.strategy.repository.TradeTypeRepository;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.dto.PresignedUrlResponseDto;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.util.s3.FilePath;
import com.investmetic.global.util.s3.S3FileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StrategyRegisterService {
    private final S3FileService s3FileService;
    private final StrategyRepository strategyRepository;
    private final TradeTypeRepository tradeTypeRepository;
    private final StockTypeRepository stockTypeRepository;
    private final UserRepository userRepository;

    public PresignedUrlResponseDto registerStrategy(
            StrategyRegisterRequestDto requestDto) {
//        TODO: 추후 삭제

        User user = userRepository.findById(327L)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        // 1. TradeType 조회 (예제용 코드로 실제 구현 시 TradeTypeService를 사용하여 조회)
        TradeType tradeType = tradeTypeRepository.findByTradeTypeId(requestDto.getTradeTypeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        // 2. 제안서 파일 경로 생성 및 Presigned URL 생성
        String proposalFilePath = s3FileService.getS3Path(
                FilePath.STRATEGY_PROPOSAL,
                requestDto.getProposalFile().getProposalFileName(),
                requestDto.getProposalFile().getProposalFileSize()
        );
        String presignedUrl = s3FileService.getPreSignedUrl(proposalFilePath);

        // 3. Strategy 생성 및 저장
        Strategy strategy = Strategy.builder()
                .user(user)
                .strategyName(requestDto.getStrategyName())
                .tradeType(tradeType)
                .operationCycle(requestDto.getOperationCycle())
                .minimumInvestmentAmount(requestDto.getMinimumInvestmentAmount())
                .proposalFilePath(proposalFilePath)
                .build();

        strategyRepository.save(strategy);
        return PresignedUrlResponseDto.builder().presignedUrl(presignedUrl).build();
    }

    public RegisterInfoResponseDto loadStrategyRegistrationInfo() {
        List<TradeTypeDto> tradeTypesDto = getActiveTradeTypes();
        List<StockTypeDto> stockTypesDto = getAllStockTypes();
        return buildRegisterInfoResponse(tradeTypesDto, stockTypesDto);
    }

    private List<TradeTypeDto> getActiveTradeTypes() {
        return tradeTypeRepository.findByActivateStateTrue().stream()
                .map(TradeTypeDto::fromEntity)
                .toList();
    }

    private List<StockTypeDto> getAllStockTypes() {
        return stockTypeRepository.findAll().stream()
                .map(StockTypeDto::from)
                .toList();
    }

    private RegisterInfoResponseDto buildRegisterInfoResponse(
            List<TradeTypeDto> tradeTypes,
            List<StockTypeDto> stockTypes
    ) {
        return RegisterInfoResponseDto.builder()
                .tradeTypes(tradeTypes)
                .stockTypes(stockTypes)
                .build();
    }
}
