package com.investmetic.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.investmetic.domain.user.dto.object.ImageMetadata;
import com.investmetic.domain.user.dto.request.UserModifyDto;
import com.investmetic.domain.user.dto.response.UserProfileDto;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.UserState;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.mypage.UserMyPageRepository;
import com.investmetic.global.config.S3MockConfig;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import io.findify.s3mock.S3Mock;
import jakarta.persistence.EntityManager;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Import(S3MockConfig.class)
class UserMyPageServiceTest {

    private static final String BUCKET_NAME = "jrw-toyproject-imgs";

    @Autowired
    private UserMyPageService userMyPageService;

    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private EntityManager em;


    //지금은 userMyPageRepository 사용하고 나중에 회원가입 생기면 userService로만 Test해보기.
    @Autowired
    private UserMyPageRepository userMyPageRepository;

    private User createOneUser() {
        User user = User.builder()
                .userName("정룡우")
                .nickname("jeongRyongWoo")
                .email("jlwoo092513@gmail.com")
                .password("123456")
                .imageUrl("https://jrw-toyproject-imgs.s3.ap-northeast-2.amazonaws.com/IMG-3925.JPG")
                .phone("01012345678")
                .birthDate("000925")
                .ipAddress("127.0.0.1")
                .infoAgreement(Boolean.FALSE)
                .userState(UserState.ACTIVE)
                .role(Role.INVESTOR_ADMIN)
                .build();
        userMyPageRepository.save(user);
        return user;
    }

    @Test
    @DisplayName("회원 정보 조회 - DB에 Email이 있을 경우.")
    public void provideUserInfoTest1() {
        User oneUser = createOneUser();

        UserProfileDto userProfileDto = userMyPageService.provideUserInfo(oneUser.getEmail());

        assertTrue(oneUser.getEmail().equals(userProfileDto.getEmail()));
        assertTrue(oneUser.getImageUrl().equals(userProfileDto.getImageUrl()));
        assertTrue(oneUser.getPhone().equals(userProfileDto.getPhone()));
    }


    @Test
    @DisplayName("회원 정보 조회 - DB에 Email이 없을 경우")
    public void provideUserInfoTest2() {

        User oneUser = createOneUser(); // 1명 DB에 생성

        UserProfileDto presentUserProfile = userMyPageService.provideUserInfo(oneUser.getEmail());
        assertTrue(presentUserProfile != null); // DB에 방금 만든 1명이 있는지.

        BusinessException e = assertThrows(BusinessException.class,
                () -> userMyPageService.provideUserInfo("asdf@hanmail.com"));
        assertTrue(e.getErrorCode().getMessage().equals(ErrorCode.USER_INFO_NOT_FOUND.getMessage()));


    }


    @Nested
    @DisplayName("회원 정보 수정")
    class UpdateUser {


        @BeforeAll
        static void setUp(@Autowired S3Mock s3Mock, @Autowired AmazonS3 amazonS3) {
            s3Mock.start();
            amazonS3.createBucket("jrw-toyproject-imgs");
        }

        @AfterAll
        static void tearDown(@Autowired S3Mock s3Mock, @Autowired AmazonS3 amazonS3) {
            amazonS3.shutdown();
            s3Mock.stop();
        }


        /**
         *
         * S3Mock에서 Delete시 Inmemory에 해당 키와 일치하는 데이터가 없으면 오류 반환.
         * <br>
         * 실제 S3Client에서는 delete시에 S3버킷에 해당 키와 일치하는 데이터가 없어도 정상 응답 옴.
         * */
        @Test
        @DisplayName("모든 값이 들어있을 떄.")
        void updateUserInfoTest1() {
            User oneUser = createOneUser();

            ImageMetadata imageMetadata = new ImageMetadata("test.jpg", "image/jpg", 1024 * 500);

            UserModifyDto userModifyDto = UserModifyDto.builder()
                    .email(oneUser.getEmail())
                    .imageDto(imageMetadata)
                    .infoAgreement(Boolean.FALSE)
                    .nickname("자자ㅏㅈ")
                    .phone("01012345678")
                    .password("9999")
                    .build();

            //기존 회원 프로필 s3이미지 객체의 key
            String key = "IMG-3925.JPG";
            String contentType = "image/jpg";

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(contentType);

            //기존의 이미지 파일 s3에 있다고 생성, 인메모리상에 저장.
            amazonS3.putObject(new PutObjectRequest(BUCKET_NAME, key,
                    new ByteArrayInputStream("IMG-3925".getBytes(StandardCharsets.UTF_8)),
                    objectMetadata));

            //presigned url에 test.jpg가 들어가 있는지 확인(presigned url api제작할 때 aws에 요청 보내지 않음. 내부에서 제작함.)
            assertThat(userMyPageService.changeUserInfo(userModifyDto, oneUser.getEmail())).contains("test.jpg");

//            em.flush();
//            em.close();


            assertThat(userMyPageService.provideUserInfo(oneUser.getEmail()).getNickname()).isEqualTo(
                    userModifyDto.getNickname());

            // 실제로 파일을 올리는 것은 front에서 실행하므로 이미지 파일이 올라갔는지에 대한 기능 추가하여 test
//            amazonS3.getObject(new GetObjectRequest(BUCKET_NAME, key));
        }





        // null 값에 대한 Test에서 사용될 인자.
        static Stream<Arguments> userModifyDtos() {
            return Stream.of(
                    Arguments.arguments(UserModifyDto.builder().email("jlwoo092513@gmail.com").imageDto(new ImageMetadata("testImage.jpg", "image/jpg", 5000)).build()),
                    Arguments.arguments(UserModifyDto.builder().email("jlwoo092513@gmail.com").nickname("테스트").build()),
                    Arguments.arguments(UserModifyDto.builder().email("jlwoo092513@gmail.com").phone("01099999999").build()),
                    Arguments.arguments(UserModifyDto.builder().email("jlwoo092513@gmail.com").infoAgreement(Boolean.TRUE).build()),
                    Arguments.arguments(UserModifyDto.builder().email("jlwoo092513@gmail.com").password("testtest!!").build())
            );
        }

        @ParameterizedTest(name = "UserModifyDto : {0}")
        @MethodSource("userModifyDtos")
        @DisplayName("null 값에 대한 Test")
        public void updateUserInfoTest2(UserModifyDto userModifyDto) {

            User oneUser = createOneUser();


            //기존 회원 프로필 s3이미지 객체의 key
            String key = "IMG-3925.JPG";
            String contentType = "image/jpg";

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(contentType);

            //기존의 이미지 파일 s3에 있다고 생성, 인메모리상에 저장.
            amazonS3.putObject(new PutObjectRequest(BUCKET_NAME
                    , key
                    , new ByteArrayInputStream("IMG-3925".getBytes(StandardCharsets.UTF_8))
                    , objectMetadata));


            // 이미지 변경을 하는 경우에.
            if(userModifyDto.getImageDto() != null) {
                //새롭게 저장될 s3객체 URL에 새로운 이미지의 이름이 들어가 있는지 확인.
                assertThat(userMyPageService.changeUserInfo(userModifyDto, oneUser.getEmail()))
                        .contains(userModifyDto.getImageDto().getImageName());
            }else{
                //이미지 변경을 안하는 경우.
                assertThat(userMyPageService.changeUserInfo(userModifyDto, oneUser.getEmail()))
                        .isNull();
            }
            //presigned url에 test.jpg가 들어가 있는지 확인(presigned url api제작할 때 aws에 요청 보내지 않음. 내부에서 제작함.)


//            em.flush();
//            em.close();


            if(userModifyDto.getNickname() != null) {
                assertThat(userMyPageService.provideUserInfo(userModifyDto.getEmail()).getNickname()).isEqualTo(userModifyDto.getNickname());
            }

            if(userModifyDto.getInfoAgreement() != null) {
                assertThat(userMyPageService.provideUserInfo(userModifyDto.getEmail()).getInfoAgreement()).isEqualTo(userModifyDto.getInfoAgreement());
            }

            if(userModifyDto.getImageDto()!=null){
                assertThat(userMyPageService.provideUserInfo(userModifyDto.getEmail()).getImageUrl()).contains(userModifyDto.getImageDto().getImageName());
            }

            // TODO : bcryptpasswordencoder Bean으로 등록되면 확인 다시하기.
            if(userModifyDto.getPassword() != null) {
                Optional<User> dbUser =  userMyPageRepository.findByEmail(userModifyDto.getEmail());
                assertThat(dbUser.isPresent()).isTrue();
                assertThat(dbUser.get().getPassword()).isEqualTo(userModifyDto.getPassword());
            }

            if(userModifyDto.getPhone() != null) {
                assertThat(userMyPageService.provideUserInfo(userModifyDto.getEmail()).getPhone()).isEqualTo(userModifyDto.getPhone());

            }
        }



        @Test
        @DisplayName(" 데이터 저장 중 예외 발생시.")
        void updateUserInfoTest3() {
            User oneUser = createOneUser();

            // Image size 예외.
            ImageMetadata imageMetadata = new ImageMetadata("test.jpg", "image/jpg", 1024 * 1024 * 1024);

            UserModifyDto userModifyDto = UserModifyDto.builder()
                    .email(oneUser.getEmail())
                    .imageDto(imageMetadata)
                    .infoAgreement(Boolean.FALSE)
                    .nickname("자자ㅏㅈ")
                    .phone("01012345678")
                    .password("9999")
                    .build();

            //기존 회원 프로필 s3이미지 객체의 key
            String key = "IMG-3925.JPG";
            String contentType = "image/jpg";

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(contentType);

            // 이미지 저장 빼기.


            //이미지 사이즈에서 예외 터짐.
            assertThatThrownBy(()-> userMyPageService.changeUserInfo(userModifyDto, oneUser.getEmail()))
                    .isInstanceOf(RuntimeException.class);


            em.flush();
            em.close();


            assertThat(userMyPageService.provideUserInfo(oneUser.getEmail()).getNickname()).isNotEqualTo(userModifyDto.getNickname());

            // 실제로 파일을 올리는 것은 front에서 실행하므로 이미지 파일이 올라갔는지에 대한 기능 추가하여 test
//            amazonS3.getObject(new GetObjectRequest(BUCKET_NAME, key));
        }




    }


}