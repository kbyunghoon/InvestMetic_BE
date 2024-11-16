package com.investmetic.domain.user.service;


import com.investmetic.domain.user.repository.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AdminPageUserServiceTest {
    @Spy
    private UserRepository userRepository;

    @InjectMocks
    private UserAdminService userAdminService;


}
