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

import com.investmetic.domain.accountverification.model.entity.AccountVerification;
import com.investmetic.domain.accountverification.repository.AccountVerficationRepository;
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
    private AccountVerficationRepository accountVerficationRepository;

    @InjectMocks
    private AccountVerificationService accountVerificationService;

    private Strategy strategy;
    private AccountVerification accountVerification1;
    private AccountVerification accountVerification2;
    private List<AccountImageRequestDto> requestDtoList;

    @BeforeEach
    void setUp() {
        strategy = Strategy.builder()
                .strategyId(1L)
                .strategyName("Test Strategy")
                .build();

        // 테스트용 AccountVerification 엔티티 생성
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

    @Test
    void 실계좌_인증_이미지_등록_성공_테스트() {
        // Given: 전략이 데이터베이스에 존재하는 경우
        when(strategyRepository.findById(1L)).thenReturn(Optional.of(strategy));
        when(s3FileService.getS3Path(eq(FilePath.STRATEGY_IMAGE), anyString(), anyInt()))
                .thenReturn("s3://bucket/path/to/image");
        when(s3FileService.getPreSignedUrl("s3://bucket/path/to/image"))
                .thenReturn("https://presigned.url");

        // When: 메서드를 호출할 때
        MultiPresignedUrlResponseDto response = accountVerificationService.registerStrategyAccountImages(1L,
                requestDtoList);

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
        assertThatThrownBy(() -> accountVerificationService.registerStrategyAccountImages(1L, requestDtoList))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.STRATEGY_NOT_FOUND.getMessage());

        // 전략 조회 실패 시 저장이 호출되지 않았는지 검증
        verify(accountVerficationRepository, never()).save(any(AccountVerification.class));
    }

    @Test
    void 실계좌_인증_조회() {
        // Given: accountVerificationRepository에서 특정 strategyId로 조회 시 데이터 반환 설정
        when(accountVerficationRepository.findByStrategy_StrategyId(1L))
                .thenReturn(List.of(accountVerification1, accountVerification2));

        // When: fetchAccountImages 메서드 호출
        List<AccountVerification> result = accountVerificationService.fetchAccountImages(1L);

        // Then: 체이닝을 사용하여 결과 검증
        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .extracting(AccountVerification::getTitle)
                .containsExactly("Verification 1", "Verification 2");

        verify(accountVerficationRepository).findByStrategy_StrategyId(1L);
    }

    @Test
    void 실계좌_인증_내역_없을_경우() {
        when(accountVerficationRepository.findByStrategy_StrategyId(anyLong()))
                .thenReturn(List.of());

        List<AccountVerification> result = accountVerificationService.fetchAccountImages(2L);

        assertThat(result).isNotNull().isEmpty();

        verify(accountVerficationRepository).findByStrategy_StrategyId(2L);
    }
}