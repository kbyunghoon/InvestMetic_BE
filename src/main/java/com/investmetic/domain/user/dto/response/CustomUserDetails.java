package com.investmetic.domain.user.dto.response;

import com.investmetic.domain.user.model.entity.User;
import java.util.Collection;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // User 객체에서 Role을 가져와 GrantedAuthority로 변환
        return Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().name())
        );
    }



    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();     //이메일을 고유식별자로 사용
    }
}
