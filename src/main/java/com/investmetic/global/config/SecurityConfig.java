package com.investmetic.global.config;

import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.security.filter.JWTFilter;
import com.investmetic.global.security.filter.LoginFilter;
import com.investmetic.global.security.handler.CustomAuthenticationFailureHandler;
import com.investmetic.global.security.handler.CustomAuthenticationSuccessHandler;
import com.investmetic.global.security.service.CustomUserDetailService;
import com.investmetic.global.util.JWTUtil;
import com.investmetic.global.util.RedisUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final CustomAuthenticationSuccessHandler successHandler;
    private final CustomAuthenticationFailureHandler failureHandler;
    private final UserRepository userRepository;
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public static RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy("""
                        ROLE_SUPER_ADMIN > ROLE_TRADER_ADMIN
                        ROLE_SUPER_ADMIN > ROLE_INVESTOR_ADMIN
                        ROLE_TRADER_ADMIN > ROLE_TRADER
                        ROLE_INVESTOR_ADMIN > ROLE_INVESTOR
                """);
    }

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:3000"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                    config.setAllowCredentials(true);
                    config.addExposedHeader("access-token");
                    config.addAllowedHeader("*");
                    return config;
                }));
        http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // CSRF 비활성화 (jwt 토큰 인증방식이기 때문에 비활성화)
        http
                .csrf(AbstractHttpConfigurer::disable);

        // Form 로그인 방식 비활성화 (로그인 페이지를 사용하지 않음)
        http
                .formLogin((auth) -> auth.disable());

        // 기본 HTTP Basic 인증 비활성화 (헤더에 사용자명과 비밀번호를 노출하는 인증 방식)
        http
                .httpBasic((auth) -> auth.disable());

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/profile").authenticated() // /profile은 인증 필요
                        .anyRequest().permitAll() // 모든 요청 허용
                );

        LoginFilter loginFilter = new LoginFilter(authenticationManager(authenticationConfiguration));
        loginFilter.setAuthenticationSuccessHandler(successHandler); // 성공 핸들러 설정
        loginFilter.setAuthenticationFailureHandler(failureHandler); // 실패 핸들러 설정
        loginFilter.setFilterProcessesUrl("/api/users/login"); // 로그인 엔드포인트 변경


        http
                .addFilterBefore(new JWTFilter(jwtUtil,new CustomUserDetailService(userRepository)), LoginFilter.class);

        http
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}