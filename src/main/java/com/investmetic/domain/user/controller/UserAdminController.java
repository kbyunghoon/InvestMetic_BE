package com.investmetic.domain.user.controller;

import com.investmetic.domain.user.dto.object.ColumnCondition;
import com.investmetic.domain.user.dto.object.RoleCondition;
import com.investmetic.domain.user.dto.request.RoleUpdateRequestDto;
import com.investmetic.domain.user.dto.request.UserAdminPageRequestDto;
import com.investmetic.domain.user.dto.response.UserProfileDto;
import com.investmetic.domain.user.service.UserAdminService;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.SuccessCode;
import com.investmetic.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "관리자페이지 회원 기능 API", description = "관리자 페이지 회원 관련 권한 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/")
public class UserAdminController {

    private final UserAdminService userAdminService;

    /**
     * 관리자 페이지 회원 목록 조회.
     *
     * @param role      ALL,TRADER,INVESTOR,ADMIN만 가능
     * @param condition NICKNAME, NAME, EMAIL, PHONE, null 가능
     * @param keyword   condition의 조건으로 검색할 내용.
     * @return param을 기반으로 나온 회원 목록 페이지 네이션 정보.
     */
    @Operation(summary = "관리자 페이지 회원 목록 조회",
            description = "<a href='https://www.notion.so/a1eec994536c4e3a9fcf02d26863ce17' target='_blank'>API 명세서</a>")
    @PreAuthorize("hasAnyRole('ROLE_TRADER_ADMIN', 'ROLE_INVESTOR_ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<BaseResponse<PageResponseDto<UserProfileDto>>> getUserList(
            @RequestParam(required = false) @NotNull RoleCondition role,
            @RequestParam(required = false) ColumnCondition condition,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 9) Pageable pageable) {
        // setter만들기 좀 그래서 위와 같이 파라미터로 받았습니다.
        UserAdminPageRequestDto requestDto = UserAdminPageRequestDto.createDto(keyword, condition, role);

        return BaseResponse.success(userAdminService.getUserList(requestDto, pageable));
    }


    /**
     * 강제 회원 탈퇴 기능 회원 탈퇴시 회원 이력 테이블에 해당 회원 데이터 모두 삭제.
     *
     * @param userId 탈퇴시키고자 하는 회원 id
     */
    @Operation(summary = "강제 회원 탈퇴 기능",
            description = "<a href='https://www.notion.so/b4b41436a8414fc09cc350062770bf6f' target='_blank'>API 명세서</a>")
    @PreAuthorize("hasAnyRole('ROLE_TRADER_ADMIN', 'ROLE_INVESTOR_ADMIN')")
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<BaseResponse<Void>> deleteUser(@PathVariable("userId") @NotNull Long userId,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {

        userAdminService.deleteUser(userId, userDetails);

        return BaseResponse.success(SuccessCode.DELETED);

    }

    /**
     * 회원 등급 변경
     *
     * @param userId     변경시키고자 하는 회원 id
     * @param requestDto 해당 role로 회원의 등급을 변경함.
     */
    @Operation(summary = "회원 등급 변경",
            description = "<a href='https://www.notion.so/40f133634e07445293933bf9e8a34934' target='_blank'>API 명세서</a>")
    @PreAuthorize("hasAnyRole('ROLE_TRADER_ADMIN', 'ROLE_INVESTOR_ADMIN') and (#userId != authentication.getPrincipal().userId)")
    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<BaseResponse<Void>> updateUserRole(@PathVariable("userId") Long userId,
                                                             @RequestBody RoleUpdateRequestDto requestDto) {

        userAdminService.modifyRole(userId, requestDto.getNewRole());

        return BaseResponse.success(SuccessCode.UPDATED);
    }

}
