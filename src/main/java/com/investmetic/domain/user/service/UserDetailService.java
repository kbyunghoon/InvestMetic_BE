package com.investmetic.domain.user.service;

import com.investmetic.domain.user.dto.response.CustomUserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserDetailService {

    CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
