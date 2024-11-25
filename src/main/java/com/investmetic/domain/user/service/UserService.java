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
import com.investmetic.global.util.RedisUtil;
import com.investmetic.global.util.s3.S3FileService;
import com.investmetic.global.util.stibee.StibeeEmailService;
import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {


    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final StibeeEmailService emailService;
    private final S3FileService s3FileService;
    private final RedisUtil redisUtil;
    private final SecureRandom secureRandom = new SecureRandom();


    //회원 가입
    @Transactional
    public String signUp(UserSignUpDto userSignUpDto) {

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

        return presignedUrl == null ? null : s3FileService.getPreSignedUrl(presignedUrl);
    }

    // 이메일 찾기 시 인증코드 발송.
    public void sendAuthenticationCode(String email) {
        // 인증 코드 생성.
        String code = createdCode();

        // 해당 이메일로 인증코드 발송.
        emailService.sendAuthenticationCode(email, code);

        // code를 redis에 저장(30 minute)
        redisUtil.setDataExpire(email, code, 60 * 30L);
    }


    //닉네임 중복
    public void checkNicknameDuplicate(String nickname) {
        validateDuplicate(NICKNAME, nickname, userRepository::existsByNickname);
    }

    // 이메일 중복
    public void checkEmailDuplicate(String email) {
        validateDuplicate(EMAIL, email, userRepository::existsByEmail);
    }

    // 핸드폰 번호 중복
    public void checkPhoneDuplicate(String phone) {
        validateDuplicate(PHONE, phone, userRepository::existsByPhone);
    }


    /**
     * 트레이더 목록 조회
     *
     * @param orderBy null일 때 구독순
     * @param keyword null일 때 키워드 검색 x
     */
    public PageResponseDto<TraderProfileDto> getTraderList(String orderBy, String keyword, Pageable pageable) {

        Page<TraderProfileDto> page = userRepository.getTraderListPage(orderBy, keyword, pageable);

        // 조회된 트레이더가 없을 때
        if (page.getContent().isEmpty()) {
            throw new BusinessException(ErrorCode.TRADER_LIST_RETRIEVAL_FAILED);
        }

        return new PageResponseDto<>(page);
    }


    // 회원 가입시에 새로 생겼을지도 모르는 중복 금지 데이터 다시 검증.
    private void extracted(UserSignUpDto userSignUpDto) {
        validateDuplicate(NICKNAME, userSignUpDto.getNickname(), userRepository::existsByNickname);
        validateDuplicate(EMAIL, userSignUpDto.getEmail(), userRepository::existsByEmail);
        validateDuplicate(PHONE, userSignUpDto.getPhone(), userRepository::existsByPhone);
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

    private String createdCode() {

        int leftLimit = 48; // number '0'
        int rightLimit = 122; // alphabet 'z'
        int targetStringLength = 6;

        return secureRandom.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    // 코드 검증
    public void verifyEmailCode(String email, String code) {

        // 저장된 인증코드 가져오기.
        String codeFoundByEmail = redisUtil.getData(email);

        // 입력코드된 인증코드가 저장된 인증코드와 다를때.
        if (!codeFoundByEmail.equals(code)) {
            throw new BusinessException(ErrorCode.VERIFICATION_FAILED);
        }

        //성공 하고 나면 해당 데이터 메모리에서 삭제
        redisUtil.deleteData(email);
    }


    @FunctionalInterface
    private interface ValidationFunction {
        boolean exists(String value);
    }

}