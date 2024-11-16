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
public class UserAdminService {

    private final UserRepository userRepository;


    /**
     * 회원 목록 페이지 네이션.
     * <pre>
     *  설명은 userRepository.getAdminUsersPage에 적혀있습니다.
     *  조건 검색하는 필터 메서드는 다양한 서비스에서 재사용 가능하도록 Repositoy에 넣어 놓았습니다.
     * </pre>
     * */
    @Transactional(readOnly = true)
    public PageResponseDto<UserProfileDto> getUserList(UserAdminPageRequestDto pageRequestDto, Pageable pageable){


        // pageRequestDto에 따른 회원 조회.
        Page<UserProfileDto> userPageList = userRepository.getAdminUsersPage(pageRequestDto, pageable);

        // Dto객체로 변환
        return new PageResponseDto<>(userPageList);
    }








}
