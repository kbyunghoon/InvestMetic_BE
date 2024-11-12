package com.investmetic.domain.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.UserState;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.mypage.UserMyPageRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserMyPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
                .joinDate(LocalDate.now())
                .userState(UserState.ACTIVE)
                .role(Role.INVESTOR_ADMIN)
                .build();
        userMyPageRepository.save(user);
        return user;
    }


    @Test
    @DisplayName("회원 정보 조회 - DB에 Email 있는 경우")
    void provideUserInfoTest1() throws Exception {

        // DB에 유저 생성.
        User user = createOneUser();
//        ResultActions

        // DB에 생성한 유저의 Email로 파라미터 설정.
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("email", user.getEmail());

        // MockMvc 이용 회원 정보 요청.
        ResultActions resultActions = mockMvc.perform(get("/api/users/mypage/profile")
                .params(multiValueMap)
        );

        resultActions.andExpect(status().isOk()) // 정상 상태 확인
                .andExpect(jsonPath("$.result.userName").value(user.getUserName())) // body에서 이름이 DB에 저장된 이름과 같은지 확인
                .andDo(print());
    }

    @Test
    @DisplayName("회원 정보 조회 - DB에 Email 없는 경우")
    void provideUserInfoTest2() throws Exception {

        // DB에 유저 생성.
        User user = createOneUser();

        // DB에 생성한 유저의 Email로 파라미터 설정.
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("email", user.getEmail());

        // MockMvc 이용 회원 정보 요청.
        ResultActions resultActions1 = mockMvc.perform(get("/api/users/mypage/profile")
                .params(multiValueMap)
        );

        resultActions1.andExpect(status().isOk()) // 정상 상태 확인
                .andExpect(jsonPath("$.result.userName").value(user.getUserName())) // body에서 이름이 DB에 저장된 이름과 같은지 확인
                .andDo(print());

        // MockMvc 이용 회원 정보 요청. - DB에 없는 이메일
        ResultActions resultActions2 = mockMvc.perform(get("/api/users/mypage/profile")
                .param("email", "NotFound@Email.com")
        );

        resultActions2.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(2001))// 실패 상태 확인
                .andDo(print());
    }
}