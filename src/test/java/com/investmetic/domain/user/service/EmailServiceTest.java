package com.investmetic.domain.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.util.RedisUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootTest
@AutoConfigureMockMvc
class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Autowired
    private RedisUtil redisUtil;

    @MockBean
    private JavaMailSender javaMailSender; // 실제 메일 발송을 막기 위해 MockBean으로 사용

    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void testSendEmail() throws MessagingException {

        String toEmail = "test@example.com";

        if (redisUtil.existData(toEmail)) {
            redisUtil.deleteData(toEmail);
        }

        emailService.sendEmail(toEmail);

        assertTrue(redisUtil.existData(toEmail)); // Redis에 데이터가 저장되었는지 확인
        String code = redisUtil.getData(toEmail);
        assertNotNull(code); // 코드가 Redis에 저장되었는지 확인
        verify(javaMailSender, times(1)).send(mimeMessage); // 메일이 한 번 발송되었는지 확인
    }

    @Test
    void testSendEmail_WithExistingCode() throws MessagingException {

        String toEmail = "test@example.com";
        String existingCode = "existingCode";
        redisUtil.setDataExpire(toEmail, existingCode, 60 * 30L); // 기존 코드 설정

        emailService.sendEmail(toEmail);

        String newCode = redisUtil.getData(toEmail);
        assertNotNull(newCode);
        assertNotEquals(existingCode, newCode); // 기존 코드와 다른 새 코드가 저장되었는지 확인
    }

    @Test
    void testVerifyEmailCode_Success() {

        String email = "test@example.com";
        String code = "123456";
        redisUtil.setDataExpire(email, code, 60 * 30L);

        boolean result = emailService.verifyEmailCode(email, code);

        assertTrue(result); // 코드가 올바를 경우
    }

    @Test
    void testVerifyEmailCode_Fail() {

        String email = "test@example.com";
        String wrongCode = "654321";
        redisUtil.setDataExpire(email, "123456", 60 * 30L);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            emailService.verifyEmailCode(email, wrongCode);
        });
        assertEquals(ErrorCode.VERIFICATION_FAILED, exception.getErrorCode()); // 예외 메시지가 예상대로 발생했는지 확인
    }
}