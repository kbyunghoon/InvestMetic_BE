package com.investmetic.domain.notice.controller;


import org.springframework.core.io.Resource;
import com.investmetic.domain.notice.dto.request.NoticeRegisterDto;
import com.investmetic.domain.notice.dto.response.NoticeDetailResponseDto;
import com.investmetic.domain.notice.service.NoticeService;
import com.investmetic.global.dto.FileDownloadResponseDto;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<BaseResponse<List<String>>> addNotice(@RequestBody NoticeRegisterDto noticeRegisterDto, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return BaseResponse.success(noticeService.saveNotice(noticeRegisterDto, customUserDetails.getUserId()));
    }

    @GetMapping("/notice/{noticeId}")
    @Operation(summary = "공지사항 상세 조회 기능",
    description = "<a href='https://www.notion.so/47f085979b85479f88d4ac8c3a534e09' target='_blank'>API 명세서</a>")
    public ResponseEntity<BaseResponse<NoticeDetailResponseDto>> getNotice(@PathVariable Long noticeId) {
        return BaseResponse.success(noticeService.getNoticeDetail(noticeId));
    }
    @GetMapping("/notice/{noticeId}/files/{noticeFileId}")
    @Operation(summary = "공지사항 파일 다운로드 기능",
    description = "<a href='https://field-sting-eff.notion.site/22a21659639540f688faf0c2818c31d7' target='_blank'>API 명세서</a>")
    public ResponseEntity<Resource> downloadNoticeFile(@PathVariable Long noticeId,@PathVariable Long noticeFileId) {
        FileDownloadResponseDto downloadNoticeFile = noticeService.downloadFileFromUrl(noticeFileId, noticeId);
        String encodedFileName = URLEncoder.encode(downloadNoticeFile.getDownloadFileName(), StandardCharsets.UTF_8)
                .replace("+", "%20");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + encodedFileName + "\"")
                .body(downloadNoticeFile.getResource());
    }
}
