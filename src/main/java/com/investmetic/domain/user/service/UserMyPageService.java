package com.investmetic.domain.user.service;

import com.investmetic.domain.user.dto.response.UserProfileDto;
import com.investmetic.domain.user.repository.mypage.UserMyPageRepository;
import com.investmetic.global.exception.BaseException;
import com.investmetic.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserMyPageService {

    private final UserMyPageRepository userMyPageRepository;


    /**
     * 개인 정보 제공
     */
    public UserProfileDto provideUserInfo(String email) {

        //BaseResponse.fail를 사용할 만한 것들은 일단 다 예외로 던지기.
        return userMyPageRepository.findByEmailUserInfo(email)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_INFO_NOT_FOUND));
    }

}
