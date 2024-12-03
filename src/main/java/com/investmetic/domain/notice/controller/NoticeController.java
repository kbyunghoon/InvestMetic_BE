package com.investmetic.domain.notice.controller;

import com.investmetic.domain.notice.dto.request.ImageRegistDto;
import com.investmetic.domain.notice.dto.request.NoticeRegistDto;
import com.investmetic.domain.notice.dto.response.ImageResponseDto;
import com.investmetic.domain.notice.service.NoticeService;
import com.investmetic.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "전략 관리 페이지 API", description = "전략 관리 페이지 관련 API")
public class NoticeController {
    private final NoticeService noticeService;

    @PostMapping("/admin/notices")
    @Operation(summary = "공지사항 등록 기능",
            description = "<a href='https://www.notion.so/47f085979b85479f88d4ac8c3a534e09' target='_blank'>API 명세서</a>")
    @PreAuthorize("hasAnyRole('ROLE_TRADER_ADMIN', 'ROLE_INVESTOR_ADMIN')")
    public ResponseEntity<BaseResponse<List<String>>> addNotice(@RequestBody NoticeRegistDto noticeRegistDto) {
        return BaseResponse.success(noticeService.saveNotice(noticeRegistDto));
    }

    @PatchMapping("/admin/notices/addImage")
    @Operation(summary = "이미지 등록 기능",
            description = "<a href='https://www.notion.so/18d905d3661846f897b9dc39f1a04b7a' target='_blank'>API 명세서</a>")
    @PreAuthorize("hasAnyRole('ROLE_TRADER_ADMIN', 'ROLE_INVESTOR_ADMIN')")
    public ResponseEntity<BaseResponse<ImageResponseDto>> addImage(@RequestParam ImageRegistDto imageRegistDto) {
        return BaseResponse.success(noticeService.saveImage(imageRegistDto));
    }

}
