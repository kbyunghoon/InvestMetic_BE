package com.investmetic.domain.strategy.service;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.investmetic.domain.accountverification.model.entity.AccountVerification;
import com.investmetic.domain.accountverification.repository.AccountVerificationRepository;
import com.investmetic.domain.review.repository.ReviewRepository;
import com.investmetic.domain.strategy.dto.StockTypeDto;
import com.investmetic.domain.strategy.dto.TradeTypeDto;
import com.investmetic.domain.strategy.dto.request.StrategyModifyRequestDto;
import com.investmetic.domain.strategy.dto.request.StrategyRegisterRequestDto;
import com.investmetic.domain.strategy.dto.response.RegisterInfoResponseDto;
import com.investmetic.domain.strategy.dto.response.StrategyModifyInfoResponseDto;
import com.investmetic.domain.strategy.model.IsPublic;
import com.investmetic.domain.strategy.model.entity.StockType;
import com.investmetic.domain.strategy.model.entity.StockTypeGroup;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.model.entity.TradeType;
import com.investmetic.domain.strategy.repository.DailyAnalysisRepository;
import com.investmetic.domain.strategy.repository.MonthlyAnalysisRepository;
import com.investmetic.domain.strategy.repository.StockTypeGroupRepository;
import com.investmetic.domain.strategy.repository.StockTypeRepository;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.strategy.repository.StrategyStatisticsRepository;
import com.investmetic.domain.strategy.repository.TradeTypeRepository;
import com.investmetic.domain.subscription.repository.SubscriptionRepository;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.dto.FileDownloadResponseDto;
import com.investmetic.global.dto.PresignedUrlResponseDto;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.util.s3.FilePath;
import com.investmetic.global.util.s3.S3FileService;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StrategyService {
    private final StrategyRepository strategyRepository;
    private final S3FileService s3FileService;
    private final TradeTypeRepository tradeTypeRepository;
    private final StockTypeRepository stockTypeRepository;
    private final UserRepository userRepository;
    private final StockTypeGroupRepository stockTypeGroupRepository;
    private final DailyAnalysisRepository dailyAnalysisRepository;
    private final MonthlyAnalysisRepository monthlyAnalysisRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ReviewRepository reviewRepository;
    private final StrategyStatisticsRepository strategyStatisticsRepository;
    private final AccountVerificationRepository accountVerificationRepository;


    @Transactional
    public void updateVisibility(Long strategyId, Long userId) {
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));

        verifyUserPermission(strategy, userId);

        strategy.setIsPublic(strategy.getIsPublic() == IsPublic.PUBLIC ? IsPublic.PRIVATE : IsPublic.PUBLIC);
    }

    @Transactional(readOnly = true)
    public FileDownloadResponseDto downloadFileFromUrl(Long strategyId, Long userId) {
        // 전략 조회 및 유효성 검사
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));

        if (strategy.getIsPublic() == IsPublic.PRIVATE && !Objects.equals(strategy.getUser().getUserId(), userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

        String proposalFilePath = strategy.getProposalFilePath();

        if (proposalFilePath == null || proposalFilePath.isBlank()) {
            throw new BusinessException(ErrorCode.PROPOSAL_NOT_FOUND);
        }

        // S3 파일 키 추출 및 파일 가져오기
        try (
                S3Object s3Object = s3FileService.extractFileKeyFromUrl(proposalFilePath);
                S3ObjectInputStream inputStream = s3Object.getObjectContent()
        ) {
            String fileExtension = extractFileExtension(proposalFilePath);

            // 다운로드 시 파일명 변경
            String newFileName = strategy.getStrategyName() + "_제안서" + fileExtension;

            // InputStreamResource 생성
            InputStreamResource resource = new InputStreamResource(inputStream);

            return FileDownloadResponseDto.builder()
                    .downloadFileName(newFileName)
                    .resource(resource)
                    .build();

        } catch (URISyntaxException | IOException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 파일 확장자 추출 메서드
    private String extractFileExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == filePath.length() - 1) {
            return ""; // 확장자가 없거나 잘못된 경우 빈 문자열 반환
        }
        return filePath.substring(lastDotIndex); // "." 포함하여 반환
    }

    @Transactional
    public void deleteStrategy(Long strategyId, Long userId) {
        // 전략 조회 및 권한 확인
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));
        verifyUserPermission(strategy, userId);

        // 종속 데이터 및 관련 파일 삭제
        deleteAssociatedData(strategy);

        // 전략 통계 삭제 (존재 여부 확인)
        if (strategy.getStrategyStatistics() != null) {
            strategyStatisticsRepository.deleteById(strategy.getStrategyStatistics().getStrategyStatisticsId());
        }

        // 전략 삭제
        strategyRepository.deleteById(strategyId);
    }

    private void deleteAssociatedData(Strategy strategy) {
        // S3 파일 삭제
        deleteS3Files(strategy);

        // 종속된 데이터 삭제
        stockTypeGroupRepository.deleteAllByStrategy(strategy);
        dailyAnalysisRepository.deleteAllByStrategy(strategy);
        monthlyAnalysisRepository.deleteAllByStrategy(strategy);
        subscriptionRepository.deleteAllByStrategy(strategy);
        reviewRepository.deleteAllByStrategy(strategy);
    }

    private void deleteS3Files(Strategy strategy) {
        // 전략 폴더 전부 삭제
        s3FileService.deleteStrategyFolder(strategy.getStrategyId());

        // 계좌 인증 파일 삭제
        List<AccountVerification> accountVerifications = accountVerificationRepository.findByStrategy(strategy);
        for (AccountVerification accountVerification : accountVerifications) {
            s3FileService.deleteFromS3(accountVerification.getAccountVerificationUrl());
        }
    }

    @Transactional
    public PresignedUrlResponseDto registerStrategy(
            StrategyRegisterRequestDto requestDto, Long userId) {
        User user = verifyUser(userId);

        TradeType tradeType = tradeTypeRepository.findByTradeTypeIdAndActivateStateTrue(requestDto.getTradeTypeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TRADETYPE_NOT_FOUND));

        Strategy strategy = Strategy.builder()
                .user(user)
                .strategyName(requestDto.getStrategyName())
                .tradeType(tradeType)
                .operationCycle(requestDto.getOperationCycle())
                .minimumInvestmentAmount(requestDto.getMinimumInvestmentAmount())
                .strategyDescription(requestDto.getDescription())
                .build();

        Long strategyId = strategyRepository.save(strategy).getStrategyId();

        String proposalFilePath = s3FileService.getS3StrategyPath(
                FilePath.STRATEGY_PROPOSAL,
                strategyId,
                requestDto.getProposalFile().getProposalFileName(),
                requestDto.getProposalFile().getProposalFileSize()
        );

        strategy.modifyStrategyProposalFilePath(proposalFilePath);

        String presignedUrl = s3FileService.getPreSignedUrl(proposalFilePath);

        requestDto.getStockTypeIds().forEach(stockTypeId -> {
            StockType stockType = stockTypeRepository.findById(stockTypeId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.STOCKTYPE_NOT_FOUND));

            StockTypeGroup stockTypeGroup = StockTypeGroup.builder()
                    .strategy(strategy)
                    .stockType(stockType)
                    .build();

            stockTypeGroupRepository.save(stockTypeGroup);
        });

        return PresignedUrlResponseDto.builder().presignedUrl(presignedUrl).build();
    }


    @Transactional
    public PresignedUrlResponseDto modifyStrategy(
            Long strategyId,
            StrategyModifyRequestDto requestDto,
            Long userId) {
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));

        verifyUserPermission(strategy, userId);

        if (Boolean.TRUE.equals(requestDto.getProposalModified())) {
            String proposalFilePath = s3FileService.getS3StrategyPath(
                    FilePath.STRATEGY_PROPOSAL,
                    strategyId,
                    requestDto.getProposalFile().getProposalFileName(),
                    requestDto.getProposalFile().getProposalFileSize()
            );

            String presignedUrl = s3FileService.getPreSignedUrl(proposalFilePath);

            s3FileService.deleteFromS3(strategy.getProposalFilePath());
            strategy.modifyStrategyWithProposalFilePath(requestDto.getStrategyName(), requestDto.getDescription(),
                    proposalFilePath);

            return PresignedUrlResponseDto.builder().presignedUrl(presignedUrl).build();
        } else {
            strategy.modifyStrategyWithoutProposalFilePath(requestDto.getStrategyName(), requestDto.getDescription());

            return null;
        }
    }

    public RegisterInfoResponseDto loadStrategyRegistrationInfo() {
        List<TradeTypeDto> tradeTypesDto = getActiveTradeTypes();
        List<StockTypeDto> stockTypesDto = getAllStockTypes();

        return buildRegisterInfoResponse(tradeTypesDto, stockTypesDto);
    }

    public StrategyModifyInfoResponseDto loadStrategyModifyInfo(Long strategyId, Long userId) {
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));

        verifyUserPermission(strategy, userId);

        TradeTypeDto tradeTypeDto = TradeTypeDto.fromEntity(strategy.getTradeType());

        return StrategyModifyInfoResponseDto.builder()
                .strategy(strategy)
                .stockTypes(getStrategyStockTypes(strategy))
                .tradeType(tradeTypeDto)
                .build();
    }

    private List<TradeTypeDto> getActiveTradeTypes() {
        return tradeTypeRepository.findByActivateStateTrue().stream()
                .map(TradeTypeDto::fromEntity)
                .toList();
    }

    private List<StockTypeDto> getStrategyStockTypes(Strategy strategy) {
        List<StockType> stockTypes = stockTypeGroupRepository.findStockTypeIdsByStrategy(strategy);

        return stockTypes.stream()
                .map(StockTypeDto::from)
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

    private User verifyUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
    }

    private void verifyUserPermission(Strategy strategy, Long userId) {
        if (!strategy.getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }
    }
}