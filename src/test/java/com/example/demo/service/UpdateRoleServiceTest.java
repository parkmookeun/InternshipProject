package com.example.demo.service;

import com.example.demo.dto.UpdateRoleResponseDto;
import com.example.demo.entity.User;
import com.example.demo.global.constants.Role;
import com.example.demo.global.exception.AuthorizationException;
import com.example.demo.global.exception.InvalidTokenException;
import com.example.demo.jwt.JwtTokenProvider;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateRoleServiceTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Spy
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Long USER_ID = 1L;
    private final String ADMIN_TOKEN = "admin-token";
    private final String USER_TOKEN = "user-token";

    @BeforeEach
    void setUp() {
        // 테스트 사용자 생성
        testUser = new User("testuser", "password", "Test User", Role.USER);
        USER_ID++;
        // 실제 저장소에 사용자 추가
        userRepository.save(testUser);

    }

    @AfterEach
    void deleteUser(){
        userService.delete(testUser);
    }

    @Test
    void updateRole_Success_WithAdminToken() {
        // Given
        List<SimpleGrantedAuthority> adminAuthorities = new ArrayList<>();
        adminAuthorities.add(new SimpleGrantedAuthority("ADMIN"));

        when(jwtTokenProvider.validateToken(ADMIN_TOKEN)).thenReturn(true);
        when(jwtTokenProvider.getAuthorities(ADMIN_TOKEN)).thenReturn(adminAuthorities);

        // When
        UpdateRoleResponseDto responseDto = userService.updateRole(USER_ID, ADMIN_TOKEN);

        // Then
        assertNotNull(responseDto);
        assertEquals(Role.ADMIN, responseDto.getRole());
        assertEquals(Role.ADMIN, testUser.getRole());
    }

    @Test
    void updateRole_Failure_WithInvalidToken() {
        // Given
        when(jwtTokenProvider.validateToken(anyString())).thenReturn(false);

        // When & Then
        assertThrows(InvalidTokenException.class, () -> {
            userService.updateRole(USER_ID, "invalid-token");
        });

        // 역할이 변경되지 않았는지 확인
        assertEquals(Role.USER, testUser.getRole());
    }

    @Test
    void updateRole_Failure_WithNonAdminToken() {
        // Given
        List<SimpleGrantedAuthority> userAuthorities = new ArrayList<>();
        userAuthorities.add(new SimpleGrantedAuthority("USER"));

        when(jwtTokenProvider.validateToken(USER_TOKEN)).thenReturn(true);
        when(jwtTokenProvider.getAuthorities(USER_TOKEN)).thenReturn(userAuthorities);

        // When & Then
        assertThrows(AuthorizationException.class, () -> {
            userService.updateRole(USER_ID, USER_TOKEN);
        });

        // 역할이 변경되지 않았는지 확인
        assertEquals(Role.USER, testUser.getRole());
    }
}