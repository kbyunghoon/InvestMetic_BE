package com.investmetic.domain.user.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
@Transactional
class UserMyPageRepositoryTest {
    @Autowired
    private UserRepository userReposiroty;

    private User createOneUser() {
        User user = User.builder().userName("정룡우").nickname("jeongRyongWoo").email("jlwoo092513@gmail.com")
                .password("123456").imageUrl("jrw_projectS3/profile/정룡우.img").phone("01012345678").birthDate("000925")
                .ipAddress("127.0.0.1").infoAgreement(Boolean.FALSE).joinDate(LocalDate.now())
                .userState(UserState.ACTIVE).role(Role.INVESTOR_ADMIN).build();
        userReposiroty.save(user);
        return user;
    }


    @Test
    @DisplayName("회원 정보 조회 - DB에 Email이 있을 경우.")
    public void testProfile() throws InterruptedException {

        //유저 생성.
        User user = createOneUser();

        //Email은 jwt나 SecurityContext에서 가져오기
        Optional<UserProfileDto> userProfileDto = userReposiroty.findByEmailUserInfo(user.getEmail());

        assertTrue(userProfileDto.isPresent());
        assertEquals(userProfileDto.get().getUserId(), user.getUserId()); //UserId 검증
        assertEquals(userProfileDto.get().getEmail(), user.getEmail()); //Email 검증

        System.out.println(userProfileDto.get()); //확인 용.
    }

    @Test
    @DisplayName("회원 정보 조회 - 사용자 Email 조회 결과 없을 떄")
    public void testProfile2() {
        // 유저 생성
        User user = createOneUser();
        Optional<UserProfileDto> userProfileDto = userReposiroty.findByEmailUserInfo(user.getEmail());
        assertTrue(userProfileDto.isPresent());

        // DB에 없는 Email
        Optional<UserProfileDto> userNotFound = userReposiroty.findByEmailUserInfo(user.getEmail() + "@gmail.com");
        assertTrue(userNotFound.isEmpty());
    }

}