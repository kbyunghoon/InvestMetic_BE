package com.investmetic.domain.notice.service;


import com.investmetic.domain.notice.dto.request.NoticeRegisterDto;
import static org.springframework.web.util.UriUtils.extractFileExtension;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.investmetic.domain.notice.dto.request.NoticeRegisterDto;
import com.investmetic.domain.notice.dto.response.NoticeDetailResponseDto;
import com.investmetic.domain.notice.dto.response.NoticeListDto;
import com.investmetic.domain.notice.model.entity.Notice;
import com.investmetic.domain.notice.model.entity.NoticeFile;
import com.investmetic.domain.notice.repository.NoticeFileRepository;
import com.investmetic.domain.notice.repository.NoticeRepository;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.global.dto.FileDownloadResponseDto;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.util.s3.S3FileService;
import jakarta.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import org.springframework.core.io.InputStreamResource;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final NoticeFileRepository noticeFileRepository;
    private final S3FileService s3FileService;

    @Transactional
    public List<String> saveNotice(NoticeRegisterDto noticeRegisterDto, Long userId) {

        User user = User.builder().userId(userId).build();

        Notice notice = noticeRepository.save(noticeRegisterDto.toEntity(user));

        List<NoticeFile> noticeFiles = noticeRegisterDto.toNoticeFiles(notice, s3FileService);
        noticeFileRepository.saveAll(noticeFiles);

        return noticeFiles.stream()
                .map(file -> s3FileService.getPreSignedUrl(file.getFileUrl()))
                .toList();
    }

    public NoticeDetailResponseDto getNoticeDetail(Long noticeId) {
        noticeRepository.findById(noticeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTICE_NOT_FOUND));
        return noticeRepository.findByNoticeId(noticeId);
    }


    // 일반 푸터 공지사항 목록
    public PageResponseDto<NoticeListDto> getUserNoticeList(Pageable pageable) {
        return new PageResponseDto<>(getNoticeList(null, pageable));
    }

    // 관리자 페이지 공지사항 목록
    public PageResponseDto<NoticeListDto> getAdminNoticeList(String keyword, Pageable pageable) {
        return new PageResponseDto<>(getNoticeList(keyword, pageable));
    }

    private Page<NoticeListDto> getNoticeList(String keyword, Pageable pageable) {
        return noticeRepository.findNoticelist(keyword, pageable);
    }

    @Transactional()
    public FileDownloadResponseDto downloadFileFromUrl(Long fileId, Long noticeId) {
        NoticeFile noticeFile=noticeFileRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTICE_FILE_NOT_FOUND));
        if(!noticeFile.getNotice().getNoticeId().equals(noticeId)) {
            throw new BusinessException(ErrorCode.NOTICE_FILE_NOT_FOUND);
        }
        String noticeFileUrl = noticeFile.getFileUrl();
        System.out.println(noticeFileUrl);
        try (
                S3Object s3Object = s3FileService.extractFileKeyFromUrl(noticeFileUrl);

                S3ObjectInputStream inputStream = s3Object.getObjectContent()
                ){
            byte[] fileData = IOUtils.toByteArray(inputStream);
            InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(fileData));

            return FileDownloadResponseDto
                    .builder()
                    .downloadFileName(noticeFile.getFileName())
                    .resource(resource)
                    .build();
        } catch (URISyntaxException | IOException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

    }

}
