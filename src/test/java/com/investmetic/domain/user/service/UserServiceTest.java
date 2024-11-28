package com.investmetic.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.investmetic.domain.user.dto.object.ImageMetadata;
import com.investmetic.domain.user.dto.request.UserSignUpDto;
import com.investmetic.domain.user.dto.response.UserProfileDto;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
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


    private User createOneUser() {
        User user = User.builder()
                .userName("testUser")
                .nickname("testNickname")
                .phone("01012345678")
                .birthDate("19900101")
                .password("password")
                .email("test@example.com")
                .role(Role.INVESTOR)
                .infoAgreement(true)
                .build();

        userRepository.save(user);
        return user;
    }

    @Test
    @DisplayName("회원가입")
    void signUpTest1() {

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


    @Test
    @DisplayName("회원 가입 이미지 변경")
    void signUpTest2() {

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
                .imageMetadata(new ImageMetadata("test.jpg", 10000))
                .build();

        // when
        String presignedUrl = userService.signUp(userSignUpDto);

        // then - DB에 사용자 정보가 저장되었는지 확인
        assertThat(presignedUrl).contains(userSignUpDto.getImageMetadata().getImageName());

        UserProfileDto savedUser = userRepository.findByEmailUserInfo(userSignUpDto.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
        assertEquals(userSignUpDto.getUsername(), savedUser.getUserName());
        assertEquals(userSignUpDto.getNickname(), savedUser.getNickname());
        assertEquals(userSignUpDto.getEmail(), savedUser.getEmail());
        assertEquals(userSignUpDto.getPhone(), savedUser.getPhone());
        assertEquals(userSignUpDto.getInfoAgreement(), savedUser.getInfoAgreement());
    }


    @Test
    @DisplayName("닉네임 중복 있을때")
    void checkNicknameDuplicateTest1() {

        //given
        User user = createOneUser();

        // when, then
        assertThatThrownBy(() -> userService.checkNicknameDuplicate(user.getNickname()))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_NICKNAME.getMessage());
    }

    @Test
    @DisplayName("닉네임 중복 없을때")
    void checkNicknameDuplicateTest2() {

        //given
        String nickname = "없는닉넴";

        // when, then
        assertThatCode(() -> userService.checkNicknameDuplicate(nickname))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("핸드폰 번호 중복 있을때")
    void checkPheonDuplicateTest1() {

        //given
        User user = createOneUser();

        // when, then
        assertThatThrownBy(() -> userService.checkPhoneDuplicate(user.getPhone()))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_PHONE.getMessage());
    }

    @Test
    @DisplayName("핸드폰 번호 중복 없을때")
    void checkPhoneDuplicateTest2() {

        //given
        String nickname = "01030913501";

        // when, then
        assertThatCode(() -> userService.checkPhoneDuplicate(nickname))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("이메일 중복이다")
    void checkEmailDuplicateTest1() {
        //given
        User user = createOneUser();

        // when, then
        assertThatThrownBy(() -> userService.checkEmailDuplicate(user.getEmail()))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_EMAIL.getMessage());
    }

    @Test
    @DisplayName("이메일 중복 아니에요")
    void checkEmailDuplicateTest2() {
        //given
        String email = "없는 이메일";

        // when, then
        assertThatCode(() -> userService.checkEmailDuplicate(email))
                .doesNotThrowAnyException();
    }
}