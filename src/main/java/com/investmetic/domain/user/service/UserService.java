package com.investmetic.domain.user.service;

import com.investmetic.domain.user.dto.request.UserSignUpDto;
import com.investmetic.domain.user.dto.response.UserProfileDto;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public BaseResponse<UserProfileDto> signUp(UserSignUpDto userSignUpDto) {
        if (userRepository.findByNicknameUserInfo(userSignUpDto.getNickname()).isPresent()){
            return BaseResponse.fail(ErrorCode.INVALID_NICKNAME);
        }
        if (userRepository.findByEmailUserInfo(userSignUpDto.getEmail()).isPresent()) {
            return BaseResponse.fail(ErrorCode.INVALID_EMAIL);
        }
        if (userRepository.findByPhoneUserInfo(userSignUpDto.getPhone()).isPresent()) {
            return BaseResponse.fail(ErrorCode.INVALID_EMAIL); //잠시 이걸로..
        }


        User createUser =UserSignUpDto.toEntity(userSignUpDto, bCryptPasswordEncoder);
        userRepository.save(createUser);

        return BaseResponse.success();
    }

}