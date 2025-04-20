package com.example.demo.service;

import com.example.demo.dto.SignUpRequestDto;
import com.example.demo.dto.SignUpResponseDto;
import com.example.demo.global.constants.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.global.exception.CustomException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Spy // Spy를 사용하여 일부 메서드는 실제로 실행되게 함
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService; // 여기에 테스트 대상 서비스를 넣으세요

    private SignUpRequestDto requestDto;
    private final String RAW_PASSWORD = "password123";
    private final String ENCODED_PASSWORD = "encodedPassword123";

    @BeforeEach
    void setUp() {
        // 테스트 데이터 설정
        requestDto = SignUpRequestDto.builder()
                .username("testuser")
                .password(RAW_PASSWORD)
                .nickname("Test User")
                .role(Role.USER)
                .build();

        doCallRealMethod().when(userRepository).save(any(User.class));

        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);
    }

    @Test
    void signUp_shouldCreateUserWithEncodedPassword() {
        SignUpResponseDto responseDto = userService.signUp(requestDto);

        verify(passwordEncoder).encode(RAW_PASSWORD);

        assertNotNull(responseDto);
        assertEquals("testuser", responseDto.getUsername());
        assertEquals("Test User", responseDto.getNickname());
        assertEquals(Role.USER, responseDto.getRole());

        User savedUser = userRepository.findByUsername("testuser");
        assertNotNull(savedUser);
        assertEquals(ENCODED_PASSWORD, savedUser.getPassword());
    }

    @Test
    void signUp_shouldCreateAdminUser() {
        // Given
        requestDto = SignUpRequestDto.builder()
                .username("adminuser")
                .password(RAW_PASSWORD)
                .nickname("Admin User")
                .role(Role.ADMIN)
                .build();

        SignUpResponseDto responseDto = userService.signUp(requestDto);

        verify(passwordEncoder).encode(RAW_PASSWORD);

        assertNotNull(responseDto);
        assertEquals("adminuser", responseDto.getUsername());
        assertEquals("Admin User", responseDto.getNickname());
        assertEquals(Role.ADMIN, responseDto.getRole());

        User savedUser = userRepository.findByUsername("adminuser");
        assertNotNull(savedUser);
        assertEquals(Role.ADMIN, savedUser.getRole());
    }

    @Test
    void signUp_shouldThrowExceptionIfUsernameExists() {

        // 같은 username으로 두 번째 사용자 시도
        SignUpRequestDto duplicateRequest = SignUpRequestDto.builder()
                .username("testuser")  // 같은 username
                .password("different123")
                .nickname("Different User")
                .role(Role.USER)
                .build();

        // When & Then
        assertThrows(CustomException.class, () -> userService.signUp(duplicateRequest));
    }
}