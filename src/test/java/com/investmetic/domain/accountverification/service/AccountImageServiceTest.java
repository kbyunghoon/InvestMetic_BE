package com.investmetic.domain.accountverification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.investmetic.domain.accountverification.model.entity.AccountVerification;
import com.investmetic.domain.accountverification.repository.AccountVerficationRepository;
import com.investmetic.domain.strategy.dto.request.AccountImageRequestDto;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.global.dto.MultiPresignedUrlResponseDto;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.util.s3.FilePath;
import com.investmetic.global.util.s3.S3FileService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountImageServiceTest {

    @Mock
    private S3FileService s3FileService;

    @Mock
    private StrategyRepository strategyRepository;

    @Mock
    private AccountVerficationRepository accountVerficationRepository;

    @InjectMocks
    private AccountImageService accountImageService;

    private Strategy strategy;
    private List<AccountImageRequestDto> requestDtoList;

    @BeforeEach
    void setUp() {
        strategy = Strategy.builder().strategyId(1L).build();

        AccountImageRequestDto requestDto = AccountImageRequestDto.builder()
                .fileName("test-image")
                .date("2024-11-15")
                .fileSize(1024)
                .build();

        requestDtoList = new ArrayList<>();
        requestDtoList.add(requestDto);
    }

    @Test
    void 실계좌_인증_이미지_등록_성공_테스트() {
        // Given: 전략이 데이터베이스에 존재하는 경우
        when(strategyRepository.findById(1L)).thenReturn(Optional.of(strategy));
        when(s3FileService.getS3Path(eq(FilePath.STRATEGY_IMAGE), anyString(), anyInt()))
                .thenReturn("s3://bucket/path/to/image");
        when(s3FileService.getPreSignedUrl("s3://bucket/path/to/image"))
                .thenReturn("https://presigned.url");

        // When: 메서드를 호출할 때
        MultiPresignedUrlResponseDto response = accountImageService.registerStrategyAccountImages(1L, requestDtoList);

        // Then: 결과 검증
        assertThat(response.getPresignedUrls()).hasSize(1);
        assertThat(response.getPresignedUrls().get(0).getPresignedUrl()).isEqualTo("https://presigned.url");

        // 전략 조회 및 저장 호출 여부 검증
        verify(strategyRepository, times(1)).findById(1L);
        verify(accountVerficationRepository, times(1)).save(any(AccountVerification.class));
    }

    @Test
    void 실계좌_인증_이미지_등록_전략_없을_경우() {
        // Given: 전략이 데이터베이스에 존재하지 않는 경우
        when(strategyRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then: 예외가 발생해야 함
        assertThatThrownBy(() -> accountImageService.registerStrategyAccountImages(1L, requestDtoList))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.STRATEGY_NOT_FOUND.getMessage());

        // 전략 조회 실패 시 저장이 호출되지 않았는지 검증
        verify(accountVerficationRepository, never()).save(any(AccountVerification.class));
    }
}