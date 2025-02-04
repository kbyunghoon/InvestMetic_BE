package com.investmetic.domain.user.service;

import com.investmetic.domain.user.dto.object.RoleCondition;
import com.investmetic.domain.user.dto.request.UserAdminPageRequestDto;
import com.investmetic.domain.user.dto.response.UserProfileDto;
import com.investmetic.domain.user.model.ActionType;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.model.entity.UserHistory;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.domain.user.service.logic.UserCommonLogic;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.security.CustomUserDetails;
import com.investmetic.global.util.s3.S3FileService;
import com.investmetic.global.util.stibee.StibeeEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserAdminService {

    private final UserRepository userRepository;
    private final StibeeEmailService stibeeEmailService;
    private final UserCommonLogic userCommonLogic;
    private final S3FileService s3FileService;


    /**
     * 회원 목록 페이지 네이션.
     * <pre>
     *  설명은 userRepository.getAdminUsersPage에 적혀있습니다.
     *  조건 검색하는 필터 메서드는 다양한 서비스에서 재사용 가능하도록 Repositoy에 넣어 놓았습니다.
     * </pre>
     */
    public PageResponseDto<UserProfileDto> getUserList(UserAdminPageRequestDto pageRequestDto, Pageable pageable) {

        // role 조건 확인. 가독성 위해 Set말고 swich로
        switch (pageRequestDto.getRole()) {
            case ALL, INVESTOR, TRADER, ADMIN -> {
            }
            default -> throw new BusinessException(ErrorCode.HANDLE_ACCESS_DENIED);
        }

        // 검색 조건 확인.
        if (pageRequestDto.getCondition() != null) {
            switch (pageRequestDto.getCondition()) {
                case NICKNAME, NAME, EMAIL, PHONE -> {
                }
                default -> throw new BusinessException(ErrorCode.HANDLE_ACCESS_DENIED);
            }
        }

        // pageRequestDto에 따른 회원 조회.
        Page<UserProfileDto> userPageList = userRepository.getAdminUsersPage(pageRequestDto, pageable);

        if (userPageList.getContent().isEmpty()) {
            throw new BusinessException(ErrorCode.USERS_NOT_FOUND);
        }

        // Dto객체로 변환
        return new PageResponseDto<>(userPageList);
    }


    /**
     * 강제 유저 탈퇴
     *
     * @param userId 탈퇴시키고자하는 user id
     * @param admin  현재 관리자의 email, security에서 가져오기. 또는 role
     */
    @Transactional
    public void deleteUser(Long userId, CustomUserDetails admin) {

        // DB에 해당 email의 값이 없을경우.
        Role adminRole = userRepository.findRoleByUserUserId(admin.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PERMISSION_DENIED));

        // 토큰의 정보와 DB상의 Role 값이 다른경우.
        if (!adminRole.equals(admin.getRole())) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }

        // deleteById를 사용하여 조회 시 값이 없을 경우 EmptyResultDataAccessException 이 발생
        User deleteUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USERS_NOT_FOUND));

        userCommonLogic.deleteUser(deleteUser);

        //회원이 프로필을 가지고 있다면 s3 객체 삭제.
        if (deleteUser.getImageUrl() != null) {
            s3FileService.deleteFromS3(deleteUser.getImageUrl());
        }

        // 스티비 주소록에서 해당 회원 삭제.
        stibeeEmailService.deleteSubscriber(deleteUser.getEmail());

        // 유저 정보 삭제.
        userRepository.delete(deleteUser);
    }


    /**
     * 회원 등급 변경.
     * <pre>
     *     SUPER_ADMIN의 경우 관리자 회원 목록에서 보이지 않도록 조치함.
     * </pre>
     */
    @Transactional
    public void modifyRole(Long userId, RoleCondition role) {

        //변경시키려고 하는 회원이 없는경우.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USERS_NOT_FOUND));

        // 회원 등급 변경.
        switch (role) {
            // 1. INVESTOR로 받는경우 해당 회원이 INVESTOR_ADMIN이 아니면 Exception 발생.
            case INVESTOR -> {
                if (!Role.INVESTOR_ADMIN.equals(user.getRole())) {
                    throw new BusinessException(ErrorCode.INVALID_TYPE_VALUE);
                }
                user.changeRole(Role.INVESTOR);

                // 회원 변경 이력 저장.
                user.addUserHistory(UserHistory.createEntity(user, ActionType.DEMOTION));
                stibeeEmailService.releaseGroup(user.getEmail());
            }

            // 2. TRADER로 받는경우 해당 회원이 TRADER_ADMIN이 아니면 Exception 발생.
            case TRADER -> {
                if (!Role.TRADER_ADMIN.equals(user.getRole())) {
                    throw new BusinessException(ErrorCode.INVALID_TYPE_VALUE);
                }
                user.changeRole(Role.TRADER);
                user.addUserHistory(UserHistory.createEntity(user, ActionType.DEMOTION));
                stibeeEmailService.releaseGroup(user.getEmail());
            }

            // 3. ADMIN으로 받는경우 TRADER면 TRADER_ADMIN, INVESTOR면 INVESTOR_ADMIN으로
            case ADMIN -> {
                if (user.getRole().equals(Role.INVESTOR)) {
                    user.changeRole(Role.INVESTOR_ADMIN);

                } else if (user.getRole().equals(Role.TRADER)) {
                    user.changeRole(Role.TRADER_ADMIN);

                } else {
                    //이미 INVESTOR_ADMIN이거나 TRADER_ADMIN인데 ADMIN권한으로 변경해달라고 하는 경우.
                    throw new BusinessException(ErrorCode.INVALID_TYPE_VALUE);
                }
                user.addUserHistory(UserHistory.createEntity(user, ActionType.PROMOTION));
                stibeeEmailService.assignGroup(user.getEmail());
            }

            // 그 외의 값은 모두 예외처리.
            default -> throw new BusinessException(ErrorCode.INVALID_TYPE_VALUE);
        }

        userRepository.saveAndFlush(user);
    }

}
