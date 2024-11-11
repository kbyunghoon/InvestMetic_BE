package com.investmetic.domain.user.service;

import com.investmetic.domain.user.dto.request.UserModifyDto;
import com.investmetic.domain.user.dto.response.UserProfileDto;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.mypage.UserMyPageRepository;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.util.s3.FilePath;
import com.investmetic.global.util.s3.S3FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserMyPageService {

    private final UserMyPageRepository userMyPageRepository;

    private final S3FileService s3FileService;

    /**
     * 개인 정보 제공
     */
    public UserProfileDto provideUserInfo(String email) {

        //BaseResponse.fail를 사용할 만한 것들은 일단 다 예외로 던지기.
        return userMyPageRepository.findByEmailUserInfo(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
    }


    /**
     * 회원 정보 수정 Transactional(readOnly = false),
     * 이메일은 변경 불가합니다.
     */
    @Transactional
    public String changeUserInfo(UserModifyDto userModifyDto, String email) {

        // S3Path 받을 변수 설정.
        String s3Path = null;

        // TODO : jwt, securityContext 에서 email 가져오기.
        // TODO : bcryptpasswordencoder Bean으로 등록되면 확인 다시하기.

        // dto에 담긴 email과 토큰상의 email이 같은지 확인.
        if (!userModifyDto.getEmail().equals(email)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

        //DB에서 email에 해당하는 회원 정보 가져오기(Dirty Checking), 해당 회원이 있는지 확인.
        User user = userMyPageRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USERS_NOT_FOUND));



        // 1. 회원이 사진을 넣어서 보낸경우.
        if (userModifyDto.getImageDto() != null) {

            String filename = userModifyDto.getImageDto().getImageName(); // 확장자 포함 파일 이름 - 프론트랑 상의하기.
            int fileSize = userModifyDto.getImageDto().getSize(); // 일단 바이트

            /*
            * 이미지 유효성 검사 후 s3경로 반환, 불통 시 RuntimeException 반환
            * 유효성 검사에서 예외날 수 있으므로 이미지 파일 검사 수 s3delete 요청하기.
            * */
            s3Path = s3FileService.getS3Path(FilePath.USER_PROFILE, filename, fileSize);



            // 1.1.기존의 프로필 사진이 있다면 기존의 프로필 사진 제거.
            if (user.getImageUrl() != null) {
                s3FileService.deleteFromS3(user.getImageUrl()); //실패 시 RuntimeException
            }

            //1.2 기존의 프로필 사진이 없다면 그대로 통과 (s3Path 값을 가지고 있음)

        } else {
            // 2.회원이 사진을 안넣은 경우
            // 2.1기존 설정한 이미지가 있으면 기존 이미지를 삭제.
            if (user.getImageUrl() != null) {
                s3FileService.deleteFromS3(user.getImageUrl());
            }

            // 2.2 기존에 설정한 이미지가 없으면 그대로 통과. (s3Path 안가지고 있음)

        }
        // 영속화된 User 객체에 회원 정보 update - 더티체킹
        user.updateUser(userModifyDto, s3Path);

        return s3Path == null ? null : s3FileService.getPreSignedUrl(s3Path);
    }

}
