package com.investmetic.domain.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.investmetic.domain.user.dto.object.ImageMetadata;
import com.investmetic.domain.user.dto.request.UserModifyDto;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.UserState;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserMyPageControllerTest {

    private static final String BUCKET_NAME = "fastcampus-team3";
    private static final String USER_EMAIL = "test@gmail.com";
    private final String userName = "test";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        createOneUser();
    }


    private void createOneUser() {
        User user = User.builder().userName(userName).nickname("jeongRyongWoo").email(USER_EMAIL).password("123456")
                .imageUrl("https://" + BUCKET_NAME + ".s3.ap-northeast-2.amazonaws.com/IMG-3925.JPG")
                .phone("01012345678").birthDate("000925").ipAddress("127.0.0.1").infoAgreement(Boolean.FALSE)
                .joinDate(LocalDate.now()).userState(UserState.ACTIVE).role(Role.INVESTOR_ADMIN).build();
        userRepository.save(user);
    }

    /*
     * TEST_EXECUTION을 해야 @BeforeEach다음에 @WithUserDetails가 적용됨.
     * */

    @Test
    @DisplayName("회원 정보 조회 - DB에 Email 있는 경우")
    @WithUserDetails(value = USER_EMAIL,
            userDetailsServiceBeanName = "customUserDetailService",
            setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void provideUserInfoTest1() throws Exception {

        // DB에 유저 생성.

        // MockMvc 이용 회원 정보 요청.
        ResultActions resultActions = mockMvc.perform(get("/api/users/mypage/profile"));

        resultActions.andExpect(status().isOk()) // 정상 상태 확인
                .andExpect(jsonPath("$.result.userName").value(userName)) // body에서 이름이 DB에 저장된 이름과 같은지 확인
                .andDo(print());
    }


    /**
     * 이미지 변경시 delete 수행 test는 하지 않도록 합니다. - api요청이 직접 가기 때문에.
     */
    @Nested
    @DisplayName("개인 정보 수정")
    class userUpdate {

        @Test
        @DisplayName("개인 정보 수정 정상 동작 - 이미지 미변경 시")
        @WithUserDetails(value = USER_EMAIL,
                userDetailsServiceBeanName = "customUserDetailService",
                setupBefore = TestExecutionEvent.TEST_EXECUTION)
        void updateUserInfo2() throws Exception {

            UserModifyDto userModifyDto = UserModifyDto.builder().email(USER_EMAIL).nickname("TTTEEESSS")
                    .password("asdf")
                    .phone("12345678912").imageChange(Boolean.FALSE).build();

            ResultActions resultActions = mockMvc.perform(
                    patch("/api/users/mypage/profile").contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userModifyDto)));

            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess")
                            .value(true)).andDo(print());
        }

        @Test
        @DisplayName("imageChange 가 null로 들어오는 경우")
        void updateUserInfo3() throws Exception {

            // imageChange null로 보냄
            UserModifyDto userModifyDto = UserModifyDto.builder().email(USER_EMAIL).nickname("테스트").password("asdf")
                    .phone("01012345678").build();

            //Valid Test throw MethodArgumentNotValidException
            ResultActions resultActions = mockMvc.perform(
                    patch("/api/users/mypage/profile").contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userModifyDto)));

            resultActions.andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        @DisplayName("imageDto filname null, 빈 문자 테스트")
        void updateUserInfo4() throws Exception {

            // imageDto Valid Test
            UserModifyDto userModifyDto = UserModifyDto.builder().email(USER_EMAIL)
                    .imageDto(new ImageMetadata("asdf.jpg", 1024 * 1024 * 5)).nickname("테스트").password("asdf")
                    .phone("01012345678").imageChange(true) // primitive 타입으로
                    .build();

            //Valid Test throw MethodArgumentNotValidException
            ResultActions resultActions = mockMvc.perform(
                    patch("/api/users/mypage/profile").contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userModifyDto)));

            resultActions.andExpect(status().isBadRequest())
                    .andDo(print());
        }
    }
}