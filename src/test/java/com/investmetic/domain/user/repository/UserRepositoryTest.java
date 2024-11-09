package com.investmetic.domain.user.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.investmetic.domain.user.dto.response.UserProfileDto;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.UserState;
import com.investmetic.domain.user.model.entity.User;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class UserRepositoryTest {
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
    @Transactional
    @DisplayName("회원 정보 조회 - DB에 Email이 있을 경우.")
    public void testProfile() throws InterruptedException {

        //유저 생성.
        User user = createOneUser();

        //Email은 jwt나 SecurityContext에서 가져오기
        Optional<UserProfileDto> user1 = userRepository.findByEmailUserInfo(user.getEmail());

        assertTrue(user1.isPresent());
        assertTrue(user1.get().getUserId().equals(user.getUserId())); //UserId 검증
        assertTrue(user1.get().getEmail().equals(user.getEmail())); //Email 검증

        System.out.println(user1.get()); //확인 용.
    }

    @Test
    @Transactional
    @DisplayName("회원 정보 조회 - 사용자 Email 조회 결과 없을 떄")
    public void testProfile2() {
        // 유저 생성
        User user = createOneUser();
        Optional<UserProfileDto> userTest = userRepository.findByEmailUserInfo(user.getEmail());
        assertTrue(userTest.isPresent());

        // DB에 없는 Email
        Optional<UserProfileDto> userNotFound = userRepository.findByEmailUserInfo(user.getEmail() + "@gmail.com");
        assertTrue(userNotFound.isEmpty());
    }

}