package com.investmetic.domain.user.repository;


import com.investmetic.domain.user.dto.request.UserAdminPageRequestDto;
import com.investmetic.domain.user.dto.response.UserProfileDto;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.UserState;
import com.investmetic.domain.user.model.entity.User;
import java.text.DecimalFormat;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class AdminPageUserRepositoryTest {

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    public void createUsers50() {
        for (int i = 0; i < 50; i++) {

            DecimalFormat dc = new DecimalFormat("##");

            User user = User.builder()
                    .userName("정룡우" + i)
                    .nickname("jeongRyongWoo" + i)
                    .email("jlwoo0925" + i + "@gmail.com")
                    .password("asdf" + i)
                    .imageUrl("jrw_projectS3/profile/정룡우.img")
                    .phone("010123456" + dc.format(i))
                    .birthDate("000925")
                    .ipAddress("127.0.0.1")
                    .infoAgreement(Boolean.FALSE)
                    .joinDate(LocalDate.now())
                    .userState(UserState.ACTIVE)
                    .role(Role.INVESTOR_ADMIN)
                    .build();
            userRepository.save(user);
        }
    }


    @Nested
    @DisplayName("관리자페이지 회원 목록")
    class UserList {

        @Test
        @DisplayName("정상 회원 조회")
        void adminUserListTest1() {

            UserAdminPageRequestDto requestDto = new UserAdminPageRequestDto();
            Pageable pageable = PageRequest.of(0, 10);

            Page<UserProfileDto> users = userRepository.getAdminUsersPage(requestDto, pageable);

            System.out.println(users.getContent());

        }


    }


}
