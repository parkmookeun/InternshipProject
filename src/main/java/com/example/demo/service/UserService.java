package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.User;
import com.example.demo.global.constants.Role;
import com.example.demo.global.exception.AuthorizationException;
import com.example.demo.global.exception.InvalidTokenException;
import com.example.demo.jwt.JwtTokenProvider;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SignUpResponseDto signUp(SignUpRequestDto dto) {

        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        User user = new User(dto.getUsername(),encodedPassword,dto.getNickname(), dto.getRole());

        userRepository.save(user);

        return new SignUpResponseDto(user);
    }

    public LoginResponseDto login(LoginRequestDto dto) {
        try {
                // 인증 시도
                Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getUsername(),
                        dto.getPassword()
                )
            );

            // 인증 성공 시 SecurityContext에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 사용자 정보 가져오기
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // 권한 정보 가져오기
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // 토큰 생성
            String accessToken = jwtTokenProvider.createAccessToken(userDetails.getUsername(), roles);

            // 토큰 응답
            return new LoginResponseDto(accessToken);

        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    public UpdateRoleResponseDto updateRole(Long userId, String token) {

        // 로그 추가
        List<String> authorities = jwtTokenProvider.getAuthorities(token).stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        System.out.println("Token authorities: " + authorities);

        if(!jwtTokenProvider.validateToken(token)){
            throw new InvalidTokenException();
        }

        boolean isAdmin = jwtTokenProvider.getAuthorities(token).stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equalsIgnoreCase("ADMIN")); // 대소문자 무시

        if(!isAdmin){
            throw new AuthorizationException();
        }

        //성공 플로우
        User user = userRepository.findByUserId(userId);

        user.setRole(Role.ADMIN);

        return new UpdateRoleResponseDto(user);
    }

    public void delete(User user){
        userRepository.delete(user);
    }
}
