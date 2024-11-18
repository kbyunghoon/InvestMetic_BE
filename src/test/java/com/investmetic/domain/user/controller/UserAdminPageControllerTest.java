package com.investmetic.domain.user.controller;


import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.investmetic.domain.user.service.UserAdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class UserAdminPageControllerTest {

    @MockBean
    private UserAdminService userAdminService;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("회원 목록 조회")
    class AdminUserList {

        @Test
        @DisplayName("role @NotNull 확인.")
        void adminUserList1() throws Exception {

            // given
            MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
            multiValueMap.add("page", String.valueOf(0));
            multiValueMap.add("size", String.valueOf(9));

            // when
            ResultActions resultActions1 = mockMvc.perform(get("/api/admin/users").params(multiValueMap));

            // then
            resultActions1.andExpect(status().isBadRequest())
                    .andExpect(status().reason(containsString("Validation failure"))).andDo(print());

        }
    }


}
