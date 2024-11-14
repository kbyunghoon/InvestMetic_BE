package com.investmetic.domain.user.repository;

import com.investmetic.domain.user.dto.response.UserProfileDto;
import java.util.Optional;

public interface UserRepositoryCustom {

    Optional<UserProfileDto> findByEmailUserInfo(String userName);


}
