package com.investmetic.domain.user.controller;

import com.investmetic.domain.user.dto.object.ColumnCondition;
import com.investmetic.domain.user.dto.object.RoleCondition;
import com.investmetic.domain.user.dto.request.AdminUserDeleteDto;
import com.investmetic.domain.user.dto.request.RoleUpdateRequestDto;
import com.investmetic.domain.user.dto.request.UserAdminPageRequestDto;
import com.investmetic.domain.user.service.UserAdminService;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.exception.SuccessCode;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/")
public class UserAdminController {

    private final UserAdminService userAdminService;

    /**
     * 관리자 페이지 회원 목록 조회.
     * @param role  ALL,TRADER,INVESTOR,ADMIN만 가능
     * @param condition NICKNAME, NAME, EMAIL, PHONE, null 가능
     * @param keyword condition의 조건으로 검색할 내용.
     *
     * @return param을 기반으로 나온 회원 목록 페이지 네이션 정보.
     * */
    @GetMapping("/users")
    // jakarta.validation.constraints.NotNull - HandlerMethodValidationException 500으로 빠짐.
    public ResponseEntity<?> getUserList(@RequestParam(required = false) @NotNull RoleCondition role,
                                         @RequestParam(required = false) ColumnCondition condition,
                                         @RequestParam(required = false) String keyword,
                                         @PageableDefault(size = 9) Pageable pageable) {
        // setter없이 객체 2개 바인딩 안됨.
        UserAdminPageRequestDto requestDto = UserAdminPageRequestDto.createDto(keyword, condition ,role);

        return BaseResponse.success(userAdminService.getUserList(requestDto, pageable));
    }


    /**
     * 강제 회원 탈퇴 기능
     * 회원 이력관리에서 회원을 탈퇴시키면 회원 이력관리에서 해당 회원에 관한 데이터도 싹 다 지울 것인지.
     * 지운다면 UserEntity 사용가능
     * 데이터 남긴다면 String UserId로 해야함.
     *
     * @param userId 탈퇴시키고자 하는 회원 id
     * */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") @NotNull Long userId,
                                        @RequestBody AdminUserDeleteDto adminUserDeleteDto){
        //TODO : email security
        userAdminService.deleteUser(userId, adminUserDeleteDto.getEmail());

        return BaseResponse.success(SuccessCode.DELETED);

    }

    /**
     * 회원 등급 변경
     * @param userId 변경시키고자 하는 회원 id
     * @param requestDto 해당 role로 회원의 등급을 변경함.
     * */
    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable("userId") Long userId,
                                            @RequestBody RoleUpdateRequestDto requestDto){

        userAdminService.modifyRole(userId, requestDto.getNewRole());

        return BaseResponse.success(SuccessCode.UPDATED);
    }

}
