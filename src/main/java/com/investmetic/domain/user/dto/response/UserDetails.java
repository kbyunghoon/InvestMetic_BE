package com.investmetic.domain.user.dto.response;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public interface UserDetails {
    Collection<? extends GrantedAuthority> getAuthorities(); // 권한 반환

    String getPassword(); // 비밀번호 반환

    String getUsername(); // 사용자 이메일 반환

    boolean isAccountNonExpired(); // 계정 만료 여부

    boolean isAccountNonLocked(); // 계정 잠금 여부

    boolean isCredentialsNonExpired(); // 자격 증명(비밀번호) 만료 여부

    boolean isEnabled(); // 계정 활성화 여부
}

