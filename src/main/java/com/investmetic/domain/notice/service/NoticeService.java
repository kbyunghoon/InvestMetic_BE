package com.investmetic.domain.notice.service;


import com.investmetic.domain.notice.dto.request.ImageRegistDto;
import com.investmetic.domain.notice.dto.request.NoticeRegistDto;
import com.investmetic.domain.notice.dto.response.ImageResponseDto;
import com.investmetic.domain.notice.dto.response.NoticeListDto;
import com.investmetic.domain.notice.model.entity.Notice;
import com.investmetic.domain.notice.model.entity.NoticeFile;
import com.investmetic.domain.notice.repository.NoticeFileRepository;
import com.investmetic.domain.notice.repository.NoticeRepository;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.util.s3.FilePath;
import com.investmetic.global.util.s3.S3FileService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private static final Logger log = LoggerFactory.getLogger(NoticeService.class);
    private final NoticeRepository noticeRepository;
    private final NoticeFileRepository noticeFileRepository;
    private final S3FileService s3FileService;
    private final UserRepository userRepository;

    public List<String> saveNotice(NoticeRegistDto noticeRegistDto) {
        List<String> noticePresignedUrls = new ArrayList<>();
        Notice notice = noticeRepository.save(noticeRegistDto.toEntity());
        System.out.println(notice.getNoticeId());
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
                    .build()
            );
        }
        return noticePresignedUrls;
    }

    public ImageResponseDto saveImage(ImageRegistDto imageRegistDto) {
        String imageFilePath = s3FileService.getS3Path(FilePath.NOTICE, imageRegistDto.getImageName(),
                imageRegistDto.getSize());
        return ImageResponseDto.builder()
                .imagefilePath(imageFilePath)
                .preSignedUrl(s3FileService.getPreSignedUrl(imageFilePath))
                .build();
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

    public void deleteNotice(Long noticeId, Long adminId){

         User user = userRepository.findById(adminId).orElseThrow(
                ()->new BusinessException(ErrorCode.USERS_NOT_FOUND));

        if(Role.isAdmin(user.getRole())){
            noticeRepository.deleteById(noticeId);
        }else{
            throw new BusinessException(ErrorCode.AUTHORIZATION_DENIED);
        }
    }
}
