package com.investmetic.domain.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.investmetic.domain.user.dto.request.UserSignUpDto;
import com.investmetic.domain.user.dto.response.UserProfileDto;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입")
    public void 회원가입() throws Exception {

        // given
        UserSignUpDto userSignUpDto = UserSignUpDto.builder()
                .username("testUser")
                .nickname("testNickname")
                .phone("01012345678")
                .birthdate("19900101")
                .password("password")
                .email("test@example.com")
                .role(Role.INVESTOR)
                .infoAgreement(true)
                .build();

        // when
        userService.signUp(userSignUpDto);

        // then - DB에 사용자 정보가 저장되었는지 확인
        UserProfileDto savedUser = userRepository.findByEmailUserInfo(userSignUpDto.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
        assertEquals(userSignUpDto.getUsername(), savedUser.getUserName());
        assertEquals(userSignUpDto.getNickname(), savedUser.getNickname());
        assertEquals(userSignUpDto.getEmail(), savedUser.getEmail());
        assertEquals(userSignUpDto.getPhone(), savedUser.getPhone());
        assertEquals(userSignUpDto.getInfoAgreement(), savedUser.getInfoAgreement());
    }
}