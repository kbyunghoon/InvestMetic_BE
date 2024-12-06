package com.investmetic.domain.notice.service;


import com.investmetic.domain.notice.dto.request.NoticeRegisterDto;
import com.investmetic.domain.notice.dto.response.NoticeDetailResponseDto;
import com.investmetic.domain.notice.model.entity.Notice;
import com.investmetic.domain.notice.model.entity.NoticeFile;
import com.investmetic.domain.notice.repository.NoticeFileRepository;
import com.investmetic.domain.notice.repository.NoticeRepository;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.util.s3.S3FileService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
