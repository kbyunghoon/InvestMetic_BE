package com.investmetic.global.config;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class AuditConfig {

    /**
     * @CreatedBy와 @LastModifiedBy 에 들어갈 값들
     * TODO 스프링 시큐리티 적용완료후 사용
     */
//    @Bean
//    public AuditorAware<String> auditorProvider() {
//        return () -> {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            if (authentication == null || !authentication.isAuthenticated()) {
//                return Optional.empty();  // 인증되지 않은 사용자일 경우
//            }
//            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
//            return Optional.ofNullable(principal.getUsername());  // 인증된 사용자 이름 반환-> 시큐리티 설정에서 지정가능
//        };
//    }

    /**
     * @CreatedBy와 @LastModifiedBy 에 들어갈 값 임시 사용
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("user");
    }

}
