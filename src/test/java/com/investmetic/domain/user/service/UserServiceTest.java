package com.investmetic.domain.user.service;

import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.UserState;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;


    //지금은 userRepository 사용하고 나중에 회원가입 생기면 userService로만 Test해보기.
    @Autowired
    private UserRepository userRepository;

    private User createOneUser() {
        User user = User.builder()
                .username("정룡우")
                .nickname("jeongRyongWoo")
                .email("jlwoo092513@gmail.com")
                .password("123456")
                .imageUrL("jrw_projectS3/profile/정룡우.img")
                .phone("01030913501")
                .birthDate("000925")
                .ipAddress("127.0.0.1")
                .infoAgreement(Boolean.FALSE)
                .joinDate(LocalDate.now())
                .withdrawalDate(null)
                .userState(UserState.ACTIVE)
                .withdrawalStatus(Boolean.FALSE)
                .role(Role.INVESTOR_ADMIN)
                .build();
        userRepository.save(user);
        return user;
    }

    @Test
    @DisplayName("회원 정보 조회 - DB에 Email이 있을 경우.")
    public void provideUserInfoTest1() {
        createOneUser();


    }


}