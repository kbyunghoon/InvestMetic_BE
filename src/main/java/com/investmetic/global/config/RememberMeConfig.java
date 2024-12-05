package com.investmetic.global.config;

import com.investmetic.global.security.repository.RedisRememberMeTokenRepository;
import com.investmetic.global.security.service.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@Configuration
@RequiredArgsConstructor
public class RememberMeConfig {

    private final RedisRememberMeTokenRepository redisRememberMeTokenRepository;
    private final CustomUserDetailService customUserDetailService;
    private static final int TOKEN_VALIDITY_SECONDS = 7 * 24 * 60 * 60; // 7일


    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        return redisRememberMeTokenRepository;
    }

    @Bean
    public RememberMeServices rememberMeServices(PersistentTokenRepository ptr) {
        PersistentTokenBasedRememberMeServices rememberMeServices = new PersistentTokenBasedRememberMeServices(
                "security", customUserDetailService, ptr);
        rememberMeServices.setTokenValiditySeconds(TOKEN_VALIDITY_SECONDS); // 7일
        rememberMeServices.setAlwaysRemember(true);
        rememberMeServices.setParameter("remember-me");
        rememberMeServices.setCookieName("remember-me");
        return rememberMeServices;
    }
}
