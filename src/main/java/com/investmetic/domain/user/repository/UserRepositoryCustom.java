package com.investmetic.domain.user.repository;

import com.investmetic.domain.user.dto.object.TraderListSort;
import com.investmetic.domain.user.dto.request.UserAdminPageRequestDto;
import com.investmetic.domain.user.dto.response.TraderProfileDto;
import com.investmetic.domain.user.dto.response.UserProfileDto;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {

    Optional<UserProfileDto> findByEmailUserInfo(String email);

    Optional<UserProfileDto> findByNicknameUserInfo(String nickname);

    Optional<UserProfileDto> findByPhoneUserInfo(String phone);

    Optional<TraderProfileDto> findTraderInfoByUserId(Long userId);

    Page<UserProfileDto> getAdminUsersPage(UserAdminPageRequestDto requestDto, Pageable pageRequest);

    Page<TraderProfileDto> getTraderListPage(TraderListSort sort, String traderNickname, Pageable pageRequest);



    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByPhone(String phone);

}
