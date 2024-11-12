package com.investmetic.domain.strategy.service;

import com.investmetic.domain.strategy.dto.StrategyRegisterRequestDto;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.model.entity.TradeType;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.strategy.repository.TradeTypeRepository;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.dto.PresignedUrlResponseDto;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.exception.SuccessCode;
import com.investmetic.global.util.s3.FilePath;
import com.investmetic.global.util.s3.S3FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StrategyRegisterService {
    private final S3FileService s3FileService;
    private final StrategyRepository strategyRepository;
    private final TradeTypeRepository tradeTypeRepository;
    private final UserRepository userRepository;

    public ResponseEntity<BaseResponse<PresignedUrlResponseDto>> registerStrategy(
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
        return BaseResponse.success(SuccessCode.CREATED, new PresignedUrlResponseDto(presignedUrl));
    }
}
