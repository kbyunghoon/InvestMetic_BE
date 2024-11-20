package com.investmetic.domain.user.service;

import com.investmetic.domain.user.dto.request.UserSignUpDto;
import com.investmetic.domain.user.dto.response.TraderProfileDto;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.common.PageResponseDto;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
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

    //회원가입
    public void signUp(UserSignUpDto userSignUpDto) {
        extracted(userSignUpDto);

        User createUser = UserSignUpDto.toEntity(userSignUpDto, bCryptPasswordEncoder);
        userRepository.save(createUser);
    }

    private void extracted(UserSignUpDto userSignUpDto) {

        if (userRepository.findByNicknameUserInfo(userSignUpDto.getNickname()).isPresent()) {
            throw new BusinessException(ErrorCode.INVALID_NICKNAME);
        }

        if (userRepository.findByEmailUserInfo(userSignUpDto.getEmail()).isPresent()) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL);
        }

        if (userRepository.findByPhoneUserInfo(userSignUpDto.getPhone()).isPresent()) {
            throw new BusinessException(ErrorCode.INVALID_PHONE);
        }

    }

    public boolean checkNicknameDuplicate(String nickname) {

        if (!userRepository.existsByNickname(nickname)) {
            throw new BusinessException(ErrorCode.INVALID_NICKNAME);
        }

        return userRepository.existsByNickname(nickname);
    }

    public boolean checkEmailDuplicate(String email) {

        if (!userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL);
        }

        return userRepository.existsByEmail(email);
    }

    public boolean checkPhoneDuplicate(String phone) {

        if (!userRepository.existsByPhone(phone)) {
            throw new BusinessException(ErrorCode.INVALID_PHONE);
        }

        return userRepository.existsByPhone(phone);
    }

    /**
     * 트레이더 목록 조회
     *
     * @param orderBy null일 때 구독순
     * @param keyword null일 때 키워드 검색 x
     * */
    @Transactional(readOnly = true)
    public PageResponseDto<TraderProfileDto> getTraderList(String orderBy, String keyword, Pageable pageable){

        Page<TraderProfileDto> page =  userRepository.getTraderListPage(orderBy, keyword, pageable);

        // 조회된 트레이더가 없을 때
        if(page.getContent().isEmpty()){
            throw new BusinessException(ErrorCode.TRADER_LIST_RETRIEVAL_FAILED);
        }

        return new PageResponseDto<>(page);
    }
}