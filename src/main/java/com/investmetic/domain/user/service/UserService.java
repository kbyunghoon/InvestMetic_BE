package com.investmetic.domain.user.service;

import static com.investmetic.domain.user.dto.object.ColumnCondition.EMAIL;
import static com.investmetic.domain.user.dto.object.ColumnCondition.NICKNAME;
import static com.investmetic.domain.user.dto.object.ColumnCondition.PHONE;
import static com.investmetic.global.util.s3.FilePath.USER_PROFILE;

import com.investmetic.domain.user.dto.object.ColumnCondition;
import com.investmetic.domain.user.dto.request.UserSignUpDto;
import com.investmetic.domain.user.dto.response.TraderProfileDto;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.util.s3.S3FileService;
import com.investmetic.global.util.stibee.StibeeEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final StibeeEmailService emailService;
    private final S3FileService s3FileService;

    //회원 가입
    public void signUp(UserSignUpDto userSignUpDto) {

        // imageUrl 초기화.
        String presignedUrl = null;

        //중복 검증
        extracted(userSignUpDto);

        // 사진 저장시.
        if (userSignUpDto.getImageMetadata() != null) {
            presignedUrl = s3FileService.getS3Path(USER_PROFILE, userSignUpDto.getImageMetadata().getImageName(),
                    userSignUpDto.getImageMetadata().getSize());
        }

        User createUser = UserSignUpDto.toEntity(userSignUpDto, presignedUrl, bCryptPasswordEncoder);

        //명시적 세이브...
        userRepository.save(createUser);

        // 스티비 주소록에 회원 추가.
        emailService.addSubscriber(createUser);
    }

    // 회원 가입시에 새로 생겼을지도 모르는 중복 금지 데이터 다시 검증.
    private void extracted(UserSignUpDto userSignUpDto) {
        validateDuplicate(NICKNAME, userSignUpDto.getNickname(), userRepository::existsByNickname);
        validateDuplicate(EMAIL, userSignUpDto.getEmail(), userRepository::existsByEmail);
        validateDuplicate(PHONE, userSignUpDto.getPhone(), userRepository::existsByPhone);
    }

    //닉네임 중복
    @Transactional(readOnly = true)
    public void checkNicknameDuplicate(String nickname) {
        validateDuplicate(NICKNAME, nickname, userRepository::existsByNickname);
    }

    // 이메일 중복
    @Transactional(readOnly = true)
    public void checkEmailDuplicate(String email) {
        validateDuplicate(EMAIL, email, userRepository::existsByEmail);
    }

    // 핸드폰 번호 중복
    @Transactional(readOnly = true)
    public void checkPhoneDuplicate(String phone) {
        validateDuplicate(PHONE, phone, userRepository::existsByPhone);
    }


    /**
     * 트레이더 목록 조회
     *
     * @param orderBy null일 때 구독순
     * @param keyword null일 때 키워드 검색 x
     */
    @Transactional(readOnly = true)
    public PageResponseDto<TraderProfileDto> getTraderList(String orderBy, String keyword, Pageable pageable) {

        Page<TraderProfileDto> page = userRepository.getTraderListPage(orderBy, keyword, pageable);

        // 조회된 트레이더가 없을 때
        if (page.getContent().isEmpty()) {
            throw new BusinessException(ErrorCode.TRADER_LIST_RETRIEVAL_FAILED);
        }

        return new PageResponseDto<>(page);
    }


    // 중복 검증 공통 로직
    private void validateDuplicate(ColumnCondition columnName, String value, ValidationFunction validationFunction) {
        if (!validationFunction.exists(value)) {
            throw new BusinessException(getErrorCodeForField(columnName));
        }
    }


    // 필드 이름에 따른 에러 코드 매핑
    private ErrorCode getErrorCodeForField(ColumnCondition columnName) {
        return switch (columnName) {
            case NICKNAME -> ErrorCode.INVALID_NICKNAME;
            case EMAIL -> ErrorCode.INVALID_EMAIL;
            case PHONE -> ErrorCode.INVALID_PHONE;
            default -> throw new BusinessException(ErrorCode.INVALID_TYPE_VALUE);
        };
    }

    @FunctionalInterface
    private interface ValidationFunction {
        boolean exists(String value);
    }

}