package com.investmetic.domain.user.repository.mypage;

import com.investmetic.domain.user.dto.response.UserProfileDto;
import java.util.Optional;

public interface UserMyPageRepositoryCustom {

    Optional<UserProfileDto> findByEmailUserInfo(String userName);

}
