package com.example.demo.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtTokenProvider {

    private final Key key;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidityInMilliseconds = jwtProperties.getAccessTokenValidityInMilliseconds();
        this.refreshTokenValidityInMilliseconds = jwtProperties.getRefreshTokenValidityInMilliseconds();
    }

    // 액세스 토큰 생성
    public String createAccessToken(String username, List<String> roles) {
        return createToken(username, roles, accessTokenValidityInMilliseconds);
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(String username) {
        return createToken(username, new ArrayList<>(), refreshTokenValidityInMilliseconds);
    }

    // 토큰 생성 로직
    private String createToken(String username, List<String> roles, long validityInMilliseconds) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰에서 사용자명 추출
    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            // Bearer 접두사 제거
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // 디버깅을 위해 토큰 출력 (실제 프로덕션에서는 제거)
            System.out.println("검증할 토큰: " + token);

            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            // 예외의 종류와 메시지 출력
            System.out.println("토큰 검증 실패: " + e.getClass().getName() + " - " + e.getMessage());
            return false;
        }
    }

    // 토큰에서 권한 정보 가져오기
    public List<SimpleGrantedAuthority> getAuthorities(String token) {
        // Bearer 접두사 제거
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 추가: 공백 문자 제거
        token = token.trim();

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        List<String> roles = (List<String>) claims.get("roles");
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
