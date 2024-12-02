package com.investmetic.global.security;

import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.entity.User;
import java.util.Collection;
import java.util.Collections;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class CustomUserDetails implements UserDetails {

    private final String email;
    private final String password;
    private final Role role;
    private final Long userId;
    private final String nickname;

    public CustomUserDetails(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.role = user.getRole();
        this.userId = user.getUserId();
        this.nickname = user.getUserName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // User 객체에서 Role을 가져와 GrantedAuthority로 변환
        return Collections.singletonList(
                new SimpleGrantedAuthority(this.role.getRole())
        );
    }


    @Override
    public String getPassword() {
        return this.password;
    }

    /*
        anthentication에서 getName을 할때 getUsername을 호출하므로
        일단 getUsername은 getEmail을 호출하도록 하겠습니다.
    */

    @Override
    public String getUsername() {
        return this.getEmail();     //이메일을 고유식별자로 사용
    }

}
