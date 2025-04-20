package com.example.demo.service;

import com.example.demo.dto.LoginRequestDto;
import com.example.demo.dto.LoginResponseDto;
import com.example.demo.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserService userService; // 테스트할 서비스

    @Test
    void login_Success() {
        // 테스트 데이터 준비
        String username = "testuser";
        String password = "password123";
        String token = "test.jwt.token";
        LoginRequestDto loginRequestDto = new LoginRequestDto(username, password);

        // UserDetails 준비
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails userDetails = new User(username, password, authorities);

        // Authentication 객체 준비
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // 모킹 설정
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.createAccessToken(any(String.class), any(List.class)))
                .thenReturn(token);

        // 테스트 실행
        LoginResponseDto responseDto = userService.login(loginRequestDto);

        // 검증
        assertNotNull(responseDto);
        assertEquals(token, responseDto.getToken());
    }

    @Test
    void login_InvalidCredentials() {
        // 테스트 데이터 준비
        String username = "testuser";
        String password = "wrongpassword";
        LoginRequestDto loginRequestDto = new LoginRequestDto(username, password);

        // 인증 실패 모킹
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        // 테스트 실행 및 검증
        assertThrows(BadCredentialsException.class, () -> {
            userService.login(loginRequestDto);
        });
    }
}