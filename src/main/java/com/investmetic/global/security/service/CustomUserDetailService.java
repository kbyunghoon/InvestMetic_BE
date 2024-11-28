package com.investmetic.global.security.service;

import com.investmetic.domain.user.dto.response.CustomUserDetails;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserRepository;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(username);

        // Optional 객체에서 User를 꺼내오거나 예외를 던지기
        User user = userOptional.orElseThrow(() ->
                new UsernameNotFoundException("User not found with email: " + username)
        );

        // CustomUserDetails 생성 후 반환
        return new CustomUserDetails(user);
    }
}
