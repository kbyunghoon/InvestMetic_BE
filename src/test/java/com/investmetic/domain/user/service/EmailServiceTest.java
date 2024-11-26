package com.investmetic.domain.user.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.util.RedisUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtil redisUtil;

    // 이메일 발송은 Stibee, redis, secureRandom만 사용합니다. 성공과 에러 검출이 이미 끝난...


    //인증코드 검증 테스트
    @Test
    @DisplayName("인증코드 검사.")
    void sendAuthenticationCodeTest() {
        //given
        String toEmail = "dev.kbhoon@gmail.com";
        String code = "1234";
        long expired = 30 * 60L;

        redisUtil.setDataExpire(toEmail, code, expired);

        //when, then
        assertThatCode(() -> userService.verifyEmailCode(toEmail, code)).doesNotThrowAnyException();
    }


    @Test
    void testVerifyEmailCode_Fail() {

        String toEmail = "dev.kbhoon@gmail.com";
        String code = "1234";
        String notValid = "9999";
        long expired = 30 * 60L;

        redisUtil.setDataExpire(toEmail, code, expired);

        //when, then
        assertThatThrownBy(() -> userService.verifyEmailCode(toEmail, notValid))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.VERIFICATION_FAILED.getMessage());// 예외 메시지가 예상대로 발생했는지 확인
    }
}