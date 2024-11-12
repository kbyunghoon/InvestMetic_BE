package com.investmetic.domain.user.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.investmetic.domain.user.dto.object.ImageMetadata;
import com.investmetic.domain.user.dto.request.UserModifyDto;
import com.investmetic.domain.user.dto.response.UserProfileDto;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.UserState;
import com.investmetic.domain.user.model.entity.User;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserMyPageRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private UserRepository userRepository;

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
        userRepository.save(user);
        return user;
    }


    @Test
    @DisplayName("회원 정보 조회 - DB에 Email이 있을 경우.")
    public void testProfile(){

        //유저 생성.
        User user = createOneUser();

        //Email은 jwt나 SecurityContext에서 가져오기
        Optional<UserProfileDto> userProfileDto = userRepository.findByEmailUserInfo(user.getEmail());

        assertTrue(userProfileDto.isPresent());
        assertTrue(userProfileDto.get().getUserId().equals(user.getUserId())); //UserId 검증
        assertTrue(userProfileDto.get().getEmail().equals(user.getEmail())); //Email 검증

        System.out.println(userProfileDto.get()); //확인 용.
    }

    @Test
    @DisplayName("회원 정보 조회 - 사용자 Email 조회 결과 없을 떄")
    public void testProfile2() {
        // 유저 생성
        User user = createOneUser();
        Optional<UserProfileDto> userProfileDto = userRepository.findByEmailUserInfo(user.getEmail());
        assertTrue(userProfileDto.isPresent());

        // DB에 없는 Email
        Optional<UserProfileDto> userNotFound = userRepository.findByEmailUserInfo(
                user.getEmail() + "@gmail.com");
        assertTrue(userNotFound.isEmpty());
    }


    @Nested
    @DisplayName("회원 정보 수정")
    class UpdateUser{

        //s3 경로 만들어줄 test용 메서드.
        private String testS3UrlCreate(String fileName){

            return "https://버킷이름.s3.region.com/user-profile/"+fileName;
        }

        @Test
        @DisplayName("- 모든 값 변경")
        public void testUpdateUser1(){

            User user = createOneUser();

            Optional<User> existUser = userRepository.findByEmail(user.getEmail());

            //생성한 유저 email로 DB를 찾아서 있는지 확인
            assertThat(existUser.isPresent()).isTrue();

            UserModifyDto userModifyDto = UserModifyDto.builder()
                    .nickname("테스트")
                    .infoAgreement(Boolean.TRUE)
                    .password("dirtyCheck!!")
                    .phone("01099999999")
                    .imageDto(new ImageMetadata("TestImage.jpa", "image/jpg", 5000))
                    .imageChange(Boolean.TRUE)
                    .build();

            //영속성에 있는 exixtUser의 값을 바꾸고 flush -> dirty checking
            existUser.get().updateUser(userModifyDto, testS3UrlCreate(userModifyDto.getImageDto().getImageName()));

            em.flush();
            em.clear();

            //저장된 user확인
            Optional<User> updateProfile = userRepository.findByEmail(user.getEmail());

            assertThat(updateProfile.isPresent()).isTrue();

            // existUser를 변경하고 flush한후 dirty checking 이 잘 되었는지 DB에서 검색해 보기.
            assertThat(updateProfile.get()).usingRecursiveComparison()
                    .comparingOnlyFields("userName", "nickname", "phone", "password", "imageUrl", "infoAgreement")
                    .isEqualTo(existUser.get());

            assertThat(updateProfile.get().getNickname()).isEqualTo(userModifyDto.getNickname());


        }


        // null 값에 대한 Test에서 사용될 인자.
        static Stream<Arguments> userModifyDtos() {
            return Stream.of(
                    // 이미지만 변경
                    Arguments.arguments(UserModifyDto.builder().email("jlwoo092513@gmail.com").imageChange(Boolean.TRUE).imageDto(new ImageMetadata("testImage.jpg", "image/jpg", 5000)).build()),
                    //닉네임만 변경
                    Arguments.arguments(UserModifyDto.builder().email("jlwoo092513@gmail.com").imageChange(Boolean.FALSE).nickname("테스트").build()),
                    // 핸드폰 번호 변경, 기존 이미지 삭제
                    Arguments.arguments(UserModifyDto.builder().email("jlwoo092513@gmail.com").imageChange(Boolean.TRUE).phone("01099999999").build()),
                    // 정보 수신 동의 변경
                    Arguments.arguments(UserModifyDto.builder().email("jlwoo092513@gmail.com").imageChange(Boolean.FALSE).infoAgreement(Boolean.FALSE).build()),
                    // 비밀 번호 변경
                    Arguments.arguments(UserModifyDto.builder().email("jlwoo092513@gmail.com").imageChange(Boolean.FALSE).password("testtest!!").build())
            );
        }



        @ParameterizedTest(name = "UserModifyDto : {0}")
        @MethodSource("userModifyDtos")
        @DisplayName(" - Entity update 로직 null 제외 검증.")
        //userModifyDto에 변경하지 않을 필드는 null로 들어옴.
        public void testUpdateUser2(UserModifyDto userModifyDto){

            User user = createOneUser();

            Optional<User> existUser = userRepository.findByEmail(user.getEmail());

            //생성한 유저 email로 DB를 찾아서 있는지 확인
            assertThat(existUser.isPresent()).isTrue();

            String s3Path = (userModifyDto.getImageDto() == null ? null : testS3UrlCreate(userModifyDto.getImageDto().getImageName()));

            //영속성에 있는 exixtUser의 값을 바꾸고 flush -> dirty checking
            existUser.get().updateUser(userModifyDto, s3Path);

            em.flush();
            em.clear();

            //저장된 user 확인.
            Optional<User> updateProfile = userRepository.findByEmail(user.getEmail());

            assertThat(updateProfile.isPresent()).isTrue();

            // existUser를 변경하고 flush한후 dirty checking 이 잘 되었는지 DB에서 검색해 보기.
            assertThat(updateProfile.get()).usingRecursiveComparison()
                    .comparingOnlyFields("userName", "nickname", "phone", "password", "imageUrl", "infoAgreement")
                    .isEqualTo(existUser.get());
        }
    }

    @Test
    void testest(){
        userRepository.save(User.builder().email("asdf").imageUrl("").build());

        Optional<User> user =userRepository.findByEmail("asdf");
        assertThat(user.isPresent()).isTrue();

        System.out.println(user.get().getImageUrl().isEmpty());
        assertThat(user.get().getImageUrl()).isEmpty();
        assertThat(user.get().getImageUrl()).isNotNull();

    }



}