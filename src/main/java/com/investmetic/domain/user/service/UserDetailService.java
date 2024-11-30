package com.investmetic.domain.user.service;

import com.investmetic.global.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserDetailService {

    CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
