package com.investmetic.domain.notice.service;


import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
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
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.dto.FileDownloadResponseDto;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.util.s3.S3FileService;
import jakarta.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final NoticeFileRepository noticeFileRepository;
    private final S3FileService s3FileService;
    private final UserRepository userRepository;

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

    @Transactional
    public List<String> updateNotice(Long noticeId, NoticeRegisterDto noticeRegisterDto) {
        // 1. 공지사항 조회
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTICE_NOT_FOUND));

        // 2. 공지사항 제목과 내용 수정 (더티 체킹 활용)
        notice.updateNotice(noticeRegisterDto.getTitle(), noticeRegisterDto.getContent());

        // 3. 기존 파일 삭제
        List<NoticeFile> existingFiles = noticeFileRepository.findByNotice(notice);
        for (NoticeFile file : existingFiles) {
            s3FileService.deleteFromS3(file.getFileUrl()); // S3에서 파일 삭제
        }
        noticeFileRepository.deleteAll(existingFiles);

        // 4. 새 파일 추가 (더티 체킹 적용)
        List<NoticeFile> newFiles = noticeRegisterDto.toNoticeFiles(notice, s3FileService);
        noticeFileRepository.saveAll(newFiles);

        // 5. 업데이트된 파일 presignedUrl 반환
        return newFiles.stream()
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
        NoticeFile noticeFile = noticeFileRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTICE_FILE_NOT_FOUND));
        if (!noticeFile.getNotice().getNoticeId().equals(noticeId)) {
            throw new BusinessException(ErrorCode.NOTICE_FILE_NOT_FOUND);
        }
        String noticeFileUrl = noticeFile.getFileUrl();
        System.out.println(noticeFileUrl);
        try (
                S3Object s3Object = s3FileService.extractFileKeyFromUrl(noticeFileUrl);

                S3ObjectInputStream inputStream = s3Object.getObjectContent()
        ) {
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


    /**
     * 공지사항 삭제
     *
     * @param noticeId 삭제하려는 공지사항 Id
     * @param adminId  삭제를 시도하는 관리자 Id
     */
    @Transactional
    public void deleteNotice(Long noticeId, Long adminId) {

        User user = userRepository.findById(adminId).orElseThrow(
                () -> new BusinessException(ErrorCode.USERS_NOT_FOUND));

        // 필터와 컨트롤러 사이 시간에 회원이 변경 되었다면...?
        if (Role.isAdmin(user.getRole())) {
            noticeRepository.deleteById(noticeId);
            s3NoticeFileDelete(noticeId);
        } else {
            throw new BusinessException(ErrorCode.AUTHORIZATION_DENIED);
        }
    }

    /*
     * s3파일 삭제 메서드
     * */
    private void s3NoticeFileDelete(Long noticeId) {

        List<NoticeFile> noticeFileList = noticeFileRepository.findAllByNoticeNoticeId(noticeId);

        if (!noticeFileList.isEmpty()) {
            List<KeyVersion> keysToDelete = new ArrayList<>();

            for (NoticeFile noticeFile : noticeFileList) {
                // s3키 추출, 변수 안쓸래요..
                keysToDelete.add(new KeyVersion(noticeFile.getFileUrl()
                        .substring(noticeFile.getFileUrl().lastIndexOf(".com/") + 5))
                );
            }
            // 삭제 요청. 공지사항 파일은 1000개 미만이므로 반복 안함.
            try {
                s3FileService.deleteByKeyList(keysToDelete);
            } catch (SdkClientException e) {
                log.error("noticeFile delete failed : notice Id = {}", noticeId);
                throw new BusinessException(ErrorCode.FILE_DELETE_FAILED);
            }
        }

    }
}
