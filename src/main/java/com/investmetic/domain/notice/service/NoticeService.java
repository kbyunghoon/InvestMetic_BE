package com.investmetic.domain.notice.service;


import com.investmetic.domain.notice.dto.request.NoticeRegistDto;
import com.investmetic.domain.notice.dto.response.NoticeDetailResponseDto;
import com.investmetic.domain.notice.model.entity.Notice;
import com.investmetic.domain.notice.model.entity.NoticeFile;
import com.investmetic.domain.notice.repository.NoticeFileRepository;
import com.investmetic.domain.notice.repository.NoticeRepository;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.security.CustomUserDetails;
import com.investmetic.global.util.s3.FilePath;
import com.investmetic.global.util.s3.S3FileService;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final NoticeFileRepository noticeFileRepository;
    private final S3FileService s3FileService;

    @Transactional
    public List<String> saveNotice(NoticeRegistDto noticeRegistDto, CustomUserDetails customUserDetails) {
        List<String> noticePresignedUrls = new ArrayList<>();
        Notice notice = noticeRepository.save(noticeRegistDto.toEntity(customUserDetails.getUserId()));
        List<String> filePaths = noticeRegistDto.getFilePaths();
        List<Integer> sizes = noticeRegistDto.getSizes();
        Iterator<String> filePathIterator = filePaths.iterator();
        Iterator<Integer> sizeListIterator = sizes.iterator();

        while (filePathIterator.hasNext() && sizeListIterator.hasNext()) {
            String url = filePathIterator.next();
            Integer size = sizeListIterator.next();

            String noticeFileUrl = s3FileService.getS3Path(FilePath.NOTICE, url, size);
            noticePresignedUrls.add(s3FileService.getPreSignedUrl(noticeFileUrl));
            noticeFileRepository.save(NoticeFile.builder()
                    .notice(notice)
                    .fileUrl(noticeFileUrl)
                    .fileName(url)
                    .build()
            );
        }
        return noticePresignedUrls;
    }

    public NoticeDetailResponseDto getNoticeDetail(Long noticeId) {
        noticeRepository.findById(noticeId).orElseThrow(() -> new BusinessException(ErrorCode.NOTICE_NOT_FOUND));
        return noticeRepository.findByNoticeId(noticeId);
    }
}
