package com.investmetic.domain.notice.service;


import static org.springframework.web.util.UriUtils.extractFileExtension;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.investmetic.domain.notice.dto.request.NoticeRegisterDto;
import com.investmetic.domain.notice.dto.response.NoticeDetailResponseDto;
import com.investmetic.domain.notice.model.entity.Notice;
import com.investmetic.domain.notice.model.entity.NoticeFile;
import com.investmetic.domain.notice.repository.NoticeFileRepository;
import com.investmetic.domain.notice.repository.NoticeRepository;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.global.dto.FileDownloadResponseDto;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.security.CustomUserDetails;
import com.investmetic.global.util.s3.FilePath;
import com.investmetic.global.util.s3.S3FileService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import nonapi.io.github.classgraph.json.JSONUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final NoticeFileRepository noticeFileRepository;
    private final S3FileService s3FileService;

    @Transactional
    public List<String> saveNotice(NoticeRegisterDto noticeRegisterDto, Long userId) {
        List<String> noticePresignedUrls = new ArrayList<>();
        User user = User.builder().userId(userId).build();
        Notice notice = noticeRepository.save(noticeRegisterDto.toEntity(user));
        List<String> filePaths = noticeRegisterDto.getFilePaths();
        List<Integer> sizes = noticeRegisterDto.getSizes();
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

    @Transactional(readOnly = true)
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
