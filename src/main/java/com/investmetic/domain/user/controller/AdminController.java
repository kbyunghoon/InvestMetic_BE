package com.investmetic.domain.user.controller;

import com.investmetic.domain.user.dto.request.UserAdminPageRequestDto;
import com.investmetic.domain.user.service.AdminService;
import com.investmetic.global.exception.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/")
public class AdminController {


    private final AdminService adminService;

    @GetMapping("/admin/a")
    public ResponseEntity<?> a(Pageable pageable, UserAdminPageRequestDto userAdminPageRequestDto) {

        return BaseResponse.success(adminService.getUserList(userAdminPageRequestDto, pageable));
    }
}
