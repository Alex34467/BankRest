package com.example.bankcards.service;

import com.example.bankcards.model.AuthResponse;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void signInTest_success() {
        Mockito.when(authenticationManager.authenticate(any()))
                .thenReturn(TestUtil.getAuthentification());
        Mockito.when(userService.getUserDetailsService())
                .thenReturn(username -> TestUtil.getUserDetails());
        Mockito.when(jwtService.generateToken(any()))
                .thenReturn("token");

        AuthResponse response = authenticationService.signIn(TestUtil.getAuthRequest());

        assertNotNull(response);
        assertTrue(StringUtils.isNotBlank(response.getToken()));
        Mockito.verify(authenticationManager, Mockito.times(1)).authenticate(any());
        Mockito.verify(userService, Mockito.times(1)).getUserDetailsService();
        Mockito.verify(jwtService, Mockito.times(1)).generateToken(any());
    }
}