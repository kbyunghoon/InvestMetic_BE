package com.investmetic.domain.user.repository;


import static org.assertj.core.api.Assertions.assertThat;

import com.investmetic.domain.user.dto.object.ColumnCondition;
import com.investmetic.domain.user.dto.object.RoleCondition;
import com.investmetic.domain.user.dto.request.UserAdminPageRequestDto;
import com.investmetic.domain.user.dto.response.UserProfileDto;
import com.investmetic.domain.user.model.ActionType;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.UserState;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.model.entity.UserHistory;
import jakarta.persistence.EntityManager;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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

    @Autowired
    private EntityManager em;

    @Autowired
    private UserHistoryRepository userHistoryRepository;


    /**
     * null, "" ," " 요청 시 @Valid등으로도 test하도록.
     * */
    @Nested
    @DisplayName("관리자페이지 회원 목록")
    class UserList {

        private final List<Role> roles = new ArrayList<>(List.of(Role.INVESTOR
                , Role.INVESTOR_ADMIN
                , Role.TRADER
                , Role.TRADER_ADMIN
                , Role.SUPER_ADMIN));

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
                        .role(roles.get(i%5))
                        .build();
                userRepository.save(user);
            }
        }

        @Test
        @DisplayName("정상 회원 조회")
        void adminUserListTest1() {

            //given - 회원 목록 페이지 들어갔을때.
            UserAdminPageRequestDto requestDto = UserAdminPageRequestDto.createDto(null,null, RoleCondition.ALL);
            Pageable pageable = PageRequest.of(0, 9);

            //when
            Page<UserProfileDto> users = userRepository.getAdminUsersPage(requestDto, pageable);

            //then
            assertThat(users.getTotalElements()).isEqualTo(40L); // Super_admin 뺀값

            long higher =Long.MAX_VALUE;
                //최신순으로 정렬되어있는지 확인.
            for(UserProfileDto user : users){

                long lowwer = user.getUserId();
                assertThat(higher).isGreaterThan(lowwer);

                higher = lowwer;

                //SUPER_ADMIN 제외
                assertThat(user.getRole()).isNotEqualTo(Role.SUPER_ADMIN);
            }
        }


        @Test
        @DisplayName("회원 조회 ADMIN만")
        void adminUserListTest2() {

            // given - INVESTOR_ADMIN 과 TRADER_ADMIN 탐색.
            UserAdminPageRequestDto requestDto = UserAdminPageRequestDto.createDto(null,null,RoleCondition.ADMIN);

            Pageable pageable = PageRequest.of(0, 9);

            // when
            Page<UserProfileDto> users = userRepository.getAdminUsersPage(requestDto, pageable);

            // then
            users.getContent().forEach(u ->
                    assertThat(u.getRole()).isIn(Role.TRADER_ADMIN, Role.INVESTOR_ADMIN)
                    .isNotEqualTo(Role.SUPER_ADMIN)
            );
        }

        @Test
        @DisplayName("회원 조회 TRADER만, 닉네임 조회.")
        void adminUserListTest3() {

            String setKeyword = "3";

            // given - TRADER와 TRADER_ADMIN 탐색, 닉네임에 3이 들어가는 것만.
            UserAdminPageRequestDto requestDto = UserAdminPageRequestDto.createDto(setKeyword, ColumnCondition.NICKNAME, RoleCondition.TRADER);
            Pageable pageable = PageRequest.of(0, 9);

            // when
            Page<UserProfileDto> users = userRepository.getAdminUsersPage(requestDto, pageable);

            // then
            users.getContent().forEach(u -> {
                        // 트레이더 등급을 가지고 있고, SUPER_ADMIN이 아닌지.
                        assertThat(u.getRole()).isIn(Role.TRADER, Role.TRADER_ADMIN).isNotEqualTo(Role.SUPER_ADMIN);

                        // 닉네임에 3이 들어가는지.
                        assertThat(u.getNickname()).contains(setKeyword);
                    }
            );
        }


        @Test
        @DisplayName("회원 조회 INVESTOR만, 이름 조회.")
        void adminUserListTest4(){

            String setKeyword = "1";
            // given  TRADER와 TRADER_ADMIN 탐색, 닉네임에 3이 들어가는 것만.
            UserAdminPageRequestDto requestDto = UserAdminPageRequestDto.createDto(setKeyword,ColumnCondition.NAME,RoleCondition.INVESTOR);
            Pageable pageable = PageRequest.of(0, 9);

            // when
            Page<UserProfileDto> users = userRepository.getAdminUsersPage(requestDto, pageable);

            // then
            users.getContent().forEach(u ->{
                        // 투자자 등급을 가지고 있고, SUPER_ADMIN이 아닌지.
                        assertThat(u.getRole()).isIn(Role.INVESTOR_ADMIN, Role.INVESTOR).isNotEqualTo(Role.SUPER_ADMIN);

                        // 닉네임에 3이 들어가는지.
                        assertThat(u.getUserName()).contains(setKeyword);
                    }
            );
        }


        @Test
        @DisplayName("condition에 잘못된 값 입력 시")
        void adminUserListTest5(){

            // condition은 있는데 keyword가 null이면 기본 회원 조회.
            String setKeyword = "asdf";
            // given - condition이 잘못된 값일 경우 조건 검색 안들어감.
            UserAdminPageRequestDto requestDto = UserAdminPageRequestDto.createDto(setKeyword, ColumnCondition.ID,RoleCondition.ALL);
            Pageable pageable = PageRequest.of(0, 9);

            UserAdminPageRequestDto requestDtoDefault = UserAdminPageRequestDto.createDto(null,null,RoleCondition.ALL);

            // when
            Page<UserProfileDto> users = userRepository.getAdminUsersPage(requestDto, pageable);
            Page<UserProfileDto> usersDefault = userRepository.getAdminUsersPage(requestDtoDefault, pageable);

            // then
            for(int i = 0; i<users.getContent().size(); i++){
                assertThat(users.getContent().get(i).getNickname())
                        .isEqualTo(usersDefault.getContent().get(i).getNickname());
            }
        }

        @Test
        @DisplayName("role에 잘못된 값 입력 시")
        void adminUserListTest6(){

            String setKeyword = "3";
            // given - role값이 잘못된 값 일경우 모든 회원 조회와 같음.
            UserAdminPageRequestDto requestDto = UserAdminPageRequestDto.createDto(setKeyword, ColumnCondition.NICKNAME,RoleCondition.INVESTOR_ADMIN);
            Pageable pageable = PageRequest.of(0, 9);

            UserAdminPageRequestDto requestDtoALL = UserAdminPageRequestDto.createDto(setKeyword, ColumnCondition.NICKNAME,RoleCondition.ALL);

            // when
            Page<UserProfileDto> usersInvestorAdmin = userRepository.getAdminUsersPage(requestDto, pageable);
            Page<UserProfileDto> usersALL = userRepository.getAdminUsersPage(requestDtoALL, pageable);

            // then
            for(int i = 0; i<usersInvestorAdmin.getContent().size(); i++){
                assertThat(usersInvestorAdmin.getContent().get(i).getRole())
                        .isEqualTo(usersALL.getContent().get(i).getRole());

                assertThat(usersInvestorAdmin.getContent().get(i).getNickname())
                        .contains(setKeyword);
            }
        }
    }


    @Nested
    @DisplayName("회원 등급 변경.")
    class RoleChange{

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

        @ParameterizedTest
        @DisplayName("등급 변경 repository test")
        @EnumSource(value = Role.class, names = {"INVESTOR_ADMIN"})
        void adminUserRoleTest1(Role role) {
            //given
            User user = createOneUser();

            em.flush();

            user.changeRole(role);
            userHistoryRepository.save(UserHistory.createEntity(user, ActionType.PROMOTION));

            em.flush();
            em.close();

            // when, then
            assertThat(userHistoryRepository.findByUserUserId(user.getUserId()).get(0).getActionType()).isEqualTo(ActionType.PROMOTION);
            assertThat(userRepository.findByEmailUserInfo(user.getEmail()).isPresent()).isEqualTo(true);
            assertThat(userRepository.findByEmailUserInfo(user.getEmail()).get().getRole()).isEqualTo(role);
        }
    }
}
