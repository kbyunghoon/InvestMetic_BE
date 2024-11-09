package com.investmetic.domain.user.service;

import com.investmetic.domain.user.dto.response.UserProfileDto;
import com.investmetic.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;


    /**
     * 개인 정보 제공
     */
    public UserProfileDto provideUserInfo(String email) {

        //JPA DB 에러는 전역으로..
        return userRepository.findByEmailUserInfo(email).orElse(null);
    }

}
