package com.investmetic.domain.user.repository;

import com.investmetic.domain.user.dto.response.UserProfileDto;
import java.util.Optional;

public interface UserRepositoryCustom {

    Optional<UserProfileDto> findByEmailUserInfo(String userName);
    Optional<UserProfileDto> findByNicknameUserInfo(String nickname);
    Optional<UserProfileDto> findByPhoneUserInfo(String phone);

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByPhone(String phone);
}

