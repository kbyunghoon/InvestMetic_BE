package com.investmetic.domain.user.service;


import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.util.RedisUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;

    @Value("${spring.mail.username}")
    private String configEmail;

    private String createdCode() {

        int leftLimit = 48; // number '0'
        int rightLimit = 122; // alphabet 'z'
        int targetStringLength = 6;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private String generateEmailContent(String code) {

        return "<div style='padding: 20px; font-size: 16px;'>"
                + "<p>안녕하세요,</p>"
                + "<p>이메일 인증 코드입니다:</p>"
                + "<h2 style='color: #2e6c80;'>" + code + "</h2>"
                + "<p>인증 코드는 30분 동안 유효합니다.</p>"
                + "<p>감사합니다!</p>"
                + "</div>";
    }

    private MimeMessage createEmailForm(String email) throws MessagingException {

        String authCode = createdCode();

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        message.addRecipients(MimeMessage.RecipientType.TO, email);
        message.setSubject("안녕하세요 인증 코드입니다.");
        message.setFrom(configEmail);

        // HTML 형식의 이메일 본문 설정
        String emailContent = generateEmailContent(authCode);
        helper.setText(emailContent, true); // true로 설정하면 HTML 형식으로 보냄

        redisUtil.setDataExpire(email, authCode, 60 * 30L);

        return message;
    }


    // 메일 보내기
    public void sendEmail(String toEmail) throws MessagingException {

        if (redisUtil.existData(toEmail)) {
            redisUtil.deleteData(toEmail);
        }

        MimeMessage emailForm = createEmailForm(toEmail);
        javaMailSender.send(emailForm);
    }

    // 코드 검증
    public Boolean verifyEmailCode(String email, String code) {

        String codeFoundByEmail = redisUtil.getData(email);

        if (!codeFoundByEmail.equals(code)) {
            throw new BusinessException(ErrorCode.VERIFICATION_FAILED);
        }
        return true;
    }

}