package com.investmetic.domain.accountverification.service;

import com.investmetic.domain.accountverification.dto.response.AccountImagesResponseDto;
import com.investmetic.domain.accountverification.model.entity.AccountVerification;
import com.investmetic.domain.accountverification.repository.AccountVerificationRepository;
import com.investmetic.domain.strategy.dto.request.AccountImageRequestDto;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.global.dto.MultiPresignedUrlResponseDto;
import com.investmetic.global.dto.PresignedUrlResponseDto;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.util.s3.FilePath;
import com.investmetic.global.util.s3.S3FileService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountVerificationService {
    private final S3FileService s3FileService;
    private final StrategyRepository strategyRepository;
    private final AccountVerificationRepository accountVerificationRepository;

    public MultiPresignedUrlResponseDto registerStrategyAccountImages(
            Long strategyId,
            List<AccountImageRequestDto> requestDtoList) {
        // 트레이더 정보 확인
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STRATEGY_NOT_FOUND));

        // 결과를 담을 리스트 생성
        List<PresignedUrlResponseDto> presignedUrlList = new ArrayList<>();

        // 요청된 각 파일에 대해 Presigned URL 생성 및 추가
        for (AccountImageRequestDto accountImageRequestDto : requestDtoList) {
            String filePath = s3FileService.getS3Path(
                    FilePath.STRATEGY_IMAGE,
                    accountImageRequestDto.getFileName() + "_" + accountImageRequestDto.getTitle(),
                    accountImageRequestDto.getFileSize()
            );

            // Presigned URL 생성 (예제 URL 생성)
            String presignedUrl = s3FileService.getPreSignedUrl(filePath);

            // PresignedUrlResponseDto 객체 생성 및 리스트에 추가
            PresignedUrlResponseDto presignedUrlResponseDto = PresignedUrlResponseDto.builder()
                    .presignedUrl(presignedUrl)
                    .build();

            AccountVerification accountVerification = AccountVerification.builder()
                    .strategy(strategy)
                    .title(accountImageRequestDto.getTitle())
                    .accountVerificationUrl(filePath)
                    .build();

            accountVerificationRepository.save(accountVerification);

            presignedUrlList.add(presignedUrlResponseDto);
        }

        return MultiPresignedUrlResponseDto.builder()
                .presignedUrls(presignedUrlList)
                .build();
    }

    @Transactional(readOnly = true)
    public List<AccountImagesResponseDto> getAccountImagesByStrategyId(Long strategyId) {
        return accountVerificationRepository.findByStrategy_StrategyId(strategyId)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    private AccountImagesResponseDto convertToDto(AccountVerification accountVerification) {
        return AccountImagesResponseDto.builder()
                .id(accountVerification.getAccountVerificationId())
                .title(accountVerification.getTitle())
                .imageUrl(accountVerification.getAccountVerificationUrl())
                .build();
    }
}
