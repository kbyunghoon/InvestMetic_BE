package com.investmetic.domain.accountverification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.investmetic.domain.accountverification.dto.response.AccountImagesResponseDto;
import com.investmetic.domain.accountverification.model.entity.AccountVerification;
import com.investmetic.domain.accountverification.repository.AccountVerificationRepository;
import com.investmetic.domain.strategy.dto.request.AccountImageRequestDto;
import com.investmetic.domain.strategy.model.entity.Strategy;
import com.investmetic.domain.strategy.repository.StrategyRepository;
import com.investmetic.domain.strategy.repository.TradeTypeRepository;
import com.investmetic.domain.user.repository.UserRepository;
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
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
class AccountVerificationServiceTest {

    @Mock
    private S3FileService s3FileService;

    @Mock
    private StrategyRepository strategyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TradeTypeRepository tradeTypeRepository;

    @Mock
    private AccountVerificationRepository accountVerificationRepository;

    @InjectMocks
    private AccountVerificationService accountVerificationService;

    private Strategy strategy;
    private AccountVerification accountVerification1;
    private AccountVerification accountVerification2;
    private List<AccountImageRequestDto> requestDtoList;

    /**
     * 테스트용 Strategy와 AccountVerification 엔티티를 생성 및 요청 DTO 초기화
     */
    @BeforeEach
    void setUp() {
        strategy = Strategy.builder()
                .strategyId(1L)
                .strategyName("Test Strategy")
                .build();

        accountVerification1 = AccountVerification.builder()
                .strategy(strategy)
                .title("Verification 1")
                .accountVerificationUrl("http://example.com/image1")
                .build();

        accountVerification2 = AccountVerification.builder()
                .strategy(strategy)
                .title("Verification 2")
                .accountVerificationUrl("http://example.com/image2")
                .build();

        AccountImageRequestDto requestDto = AccountImageRequestDto.builder()
                .fileName("test-image")
                .title("테스트")
                .fileSize(1024)
                .build();

        requestDtoList = new ArrayList<>();
        requestDtoList.add(requestDto);
    }

    /**
     * 전략이 데이터베이스에 존재할 경우, 계좌 인증 이미지 등록 성공 테스트
     */
    @Test
    void 실계좌_인증_이미지_등록_성공_테스트() {
        when(strategyRepository.findById(1L)).thenReturn(Optional.of(strategy));
        when(s3FileService.getS3Path(eq(FilePath.STRATEGY_IMAGE), anyString(), anyInt()))
                .thenReturn("s3://bucket/path/to/image");
        when(s3FileService.getPreSignedUrl("s3://bucket/path/to/image"))
                .thenReturn("https://presigned.url");

        MultiPresignedUrlResponseDto response = accountVerificationService.registerStrategyAccountImages(1L,
                requestDtoList);

        assertThat(response.getPresignedUrls()).hasSize(1);
        assertThat(response.getPresignedUrls().get(0).getPresignedUrl()).isEqualTo("https://presigned.url");

        verify(strategyRepository, times(1)).findById(1L);
        verify(accountVerificationRepository, times(1)).save(any(AccountVerification.class));
    }

    /**
     * 계좌 인증 이미지 등록 시 전략이 존재하지 않을 경우 예외 발생 테스트. 전략이 데이터베이스에 존재하지 않을 경우, BusinessException 예외 발생해야 함.
     */
    @Test
    void 실계좌_인증_이미지_등록_전략_없을_경우() {
        when(strategyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountVerificationService.registerStrategyAccountImages(1L, requestDtoList))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.STRATEGY_NOT_FOUND.getMessage());

        verify(accountVerificationRepository, never()).save(any(AccountVerification.class));
    }

    /**
     * 특정 전략 ID로 계좌 인증 이미지를 조회하는 메서드 테스트. accountVerificationRepository가 데이터를 반환할 경우, 정상적 결과 반환 확인.
     */
    @Test
    void 실계좌_인증_조회() {
        when(accountVerificationRepository.findByStrategy_StrategyId(1L))
                .thenReturn(List.of(accountVerification1, accountVerification2));

        List<AccountImagesResponseDto> result = accountVerificationService.getAccountImagesByStrategyId(1L);

        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .extracting(AccountImagesResponseDto::getTitle)
                .containsExactly("Verification 1", "Verification 2");

        verify(accountVerificationRepository).findByStrategy_StrategyId(1L);
    }

    /**
     * 계좌 인증 내역이 없을 경우 빈 리스트 반환 테스트. 주어진 전략 ID로 데이터가 없을 경우, 빈 리스트인지 확인.
     */
    @Test
    void 실계좌_인증_내역_없을_경우() {
        when(accountVerificationRepository.findByStrategy_StrategyId(anyLong()))
                .thenReturn(List.of());

        List<AccountImagesResponseDto> result = accountVerificationService.getAccountImagesByStrategyId(2L);

        assertThat(result).isNotNull().isEmpty();

        verify(accountVerificationRepository).findByStrategy_StrategyId(2L);
    }
}