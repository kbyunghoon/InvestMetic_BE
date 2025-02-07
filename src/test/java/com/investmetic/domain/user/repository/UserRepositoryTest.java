package com.investmetic.domain.user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.investmetic.domain.user.dto.response.UserProfileDto;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.entity.User;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserRepositoryTest {

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
    void findByEmailUserInfo_성공() {
        User user = createOneUser();

        Optional<UserProfileDto> result = userRepository.findByEmailUserInfo(user.getEmail());

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    }


    @Test
    void existsByEmail_성공() {
        createOneUser();

        boolean exists = userRepository.existsByEmail("test@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByNickname_성공() {
        createOneUser();
        boolean exists = userRepository.existsByNickname("testNickname");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByPhone_성공() {
        createOneUser();
        boolean exists = userRepository.existsByPhone("01012345678");

        assertThat(exists).isTrue();
    }
}