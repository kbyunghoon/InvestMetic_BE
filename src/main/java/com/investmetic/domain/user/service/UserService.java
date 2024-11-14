package com.investmetic.domain.user.service;

import com.investmetic.domain.user.dto.request.UserSignUpDto;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.exception.BusinessException;
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

    public UserSignUpDto signUp(UserSignUpDto userSignUpDto) {
        if (userRepository.findByNicknameUserInfo(userSignUpDto.getNickname()).isPresent()) {
            throw new BusinessException(ErrorCode.INVALID_NICKNAME);
        }
        if (userRepository.findByEmailUserInfo(userSignUpDto.getEmail()).isPresent()) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL);
        }
        if (userRepository.findByPhoneUserInfo(userSignUpDto.getPhone()).isPresent()) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL);
        }

        User createUser = UserSignUpDto.toEntity(userSignUpDto, bCryptPasswordEncoder);
        userRepository.save(createUser);

        return userSignUpDto;
    }

    public boolean checkNicknameDuplicate(String nickname) {
        if (!userRepository.existsByNickname(nickname)) {
            throw new BusinessException(ErrorCode.INVALID_NICKNAME);
        }
        return userRepository.existsByNickname(nickname);
    }

    public boolean checkEmailDuplicate(String email) {
       if(!userRepository.existsByEmail(email)) {
           throw new BusinessException(ErrorCode.INVALID_EMAIL);
       }
        return userRepository.existsByEmail(email);
    }

    public boolean checkPhoneDuplicate(String phone) {
        if(!userRepository.existsByPhone(phone)) {
            throw new BusinessException(ErrorCode.INVALID_PHONE);
        }
        return userRepository.existsByPhone(phone);
    }
}