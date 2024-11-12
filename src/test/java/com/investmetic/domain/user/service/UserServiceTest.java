package com.investmetic.domain.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.investmetic.domain.user.dto.request.UserSignUpDto;
import com.investmetic.domain.user.dto.response.UserProfileDto;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.exception.BaseResponse;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private UserSignUpDto userSignUpDto;

    @BeforeEach
    void setUp() {
        userSignUpDto = UserSignUpDto.builder()
                .username("testUser")
                .nickname("testNickname")
                .phone("01012345678")
                .birthdate("2000-11-11")
                .password("testPassword123")
                .email("test@example.com")
                .role(Role.INVESTOR)
                .infoAgreement(true)
                .build();
    }

    @Test
    @Rollback(value = false)
    void signUp_Success() {
        // 회원가입 호출
        BaseResponse<?> response = userService.signUp(userSignUpDto);

        // 결과 확인
        assertTrue(response.getIsSuccess());
        assertEquals("요청이 성공했습니다.", response.getMessage());

        // UserSignUpDto에서 각 필드가 올바르게 설정되었는지 검증
        assertEquals("testUser", userSignUpDto.getUsername());
        assertEquals("testNickname", userSignUpDto.getNickname());
        assertEquals("01012345678", userSignUpDto.getPhone());
        assertEquals("2000-11-11", userSignUpDto.getBirthdate());
        assertEquals("test@example.com", userSignUpDto.getEmail());
        assertEquals(Role.INVESTOR, userSignUpDto.getRole());
        assertTrue(userSignUpDto.getInfoAgreement());
        // 데이터베이스에서 저장된 User 엔티티 가져오기
        Optional<UserProfileDto> optionalUser = userRepository.findByEmailUserInfo(userSignUpDto.getEmail());
        assertTrue(optionalUser.isPresent());

        UserProfileDto savedUser = optionalUser.get();

    }
}