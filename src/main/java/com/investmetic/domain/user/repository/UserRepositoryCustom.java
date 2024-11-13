package com.investmetic.domain.user.repository;

import com.investmetic.domain.user.dto.request.UserAdminPageRequestDto;
import com.investmetic.domain.user.dto.response.UserProfileDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserRepositoryCustom {

    Optional<UserProfileDto> findByEmailUserInfo(String userName);

    Page<UserProfileDto> getAdminUsersPage(UserAdminPageRequestDto pageRequestDto, Pageable pageRequest);


}