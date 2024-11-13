package com.investmetic.domain.user.service;

import com.investmetic.domain.user.dto.request.UserAdminPageRequestDto;
import com.investmetic.domain.user.dto.response.UserProfileDto;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.common.PageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public PageResponseDto<UserProfileDto> getUserList(UserAdminPageRequestDto pageRequestDto, Pageable pageable){

        /*
         * 회원 목록 페이지 네이션.
         * content가 null 일경우 UserReposirotyCustomImpl에서 BusinessException 던짐.
         * */
        Page<UserProfileDto> userPageList = userRepository.getAdminUsersPage(pageRequestDto, pageable);

        // Dto객체로 변환
        return new PageResponseDto<>(userPageList);
    }


}
