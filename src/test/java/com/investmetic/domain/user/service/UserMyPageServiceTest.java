package com.investmetic.domain.user.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.investmetic.domain.user.dto.response.UserProfileDto;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.UserState;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.mypage.UserMyPageRepository;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserMyPageServiceTest {

    @Autowired
    private UserMyPageService userMyPageService;


    //지금은 userMyPageRepository 사용하고 나중에 회원가입 생기면 userService로만 Test해보기.
    @Autowired
    private UserMyPageRepository userMyPageRepository;

    private User createOneUser() {
        User user = User.builder()
                .userName("정룡우")
                .nickname("jeongRyongWoo")
                .email("jlwoo092513@gmail.com")
                .password("123456")
                .imageUrl("jrw_projectS3/profile/정룡우.img")
                .phone("01012345678")
                .birthDate("000925")
                .ipAddress("127.0.0.1")
                .infoAgreement(Boolean.FALSE)
                .userState(UserState.ACTIVE)
                .role(Role.INVESTOR_ADMIN)
                .build();
        userMyPageRepository.save(user);
        return user;
    }

    @Test
    @DisplayName("회원 정보 조회 - DB에 Email이 있을 경우.")
    public void provideUserInfoTest1() {
        User oneUser = createOneUser();

        UserProfileDto userProfileDto = userMyPageService.provideUserInfo(oneUser.getEmail());

        assertTrue(oneUser.getEmail().equals(userProfileDto.getEmail()));
        assertTrue(oneUser.getImageUrl().equals(userProfileDto.getImageUrl()));
        assertTrue(oneUser.getPhone().equals(userProfileDto.getPhone()));
    }


    @Test
    @DisplayName("회원 정보 조회 - DB에 Email이 없을 경우")
    public void provideUserInfoTest2() {

        User oneUser = createOneUser(); // 1명 DB에 생성

        UserProfileDto presentUserProfile = userMyPageService.provideUserInfo(oneUser.getEmail());
        assertTrue(presentUserProfile != null); // DB에 방금 만든 1명이 있는지.

        BusinessException e = assertThrows(BusinessException.class,
                () -> userMyPageService.provideUserInfo("asdf@hanmail.com"));
        assertTrue(e.getErrorCode().getMessage().equals(ErrorCode.USER_INFO_NOT_FOUND.getMessage()));


    }


}