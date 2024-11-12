package com.investmetic.domain.user.service;

import com.investmetic.domain.user.repository.mypage.UserMyPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final UserMyPageRepository userRepository;

}
