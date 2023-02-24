package com.glimps.glimpsserver.common.util;

import com.glimps.glimpsserver.common.error.ErrorCode;
import com.glimps.glimpsserver.common.error.InvalidTokenException;
import com.glimps.glimpsserver.common.oauth.dto.JwtDto;
import com.glimps.glimpsserver.user.domain.RoleType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
public class JwtUtil {

    private final String accessTokenExpirationTime;
    private final String refreshTokenExpirationTime;
    private final String tokenSecret;

    public Claims decode(String token) {
        if (token == null || token.isBlank()) {
            throw new InvalidTokenException(ErrorCode.INVALID_TOKEN, token);
        }
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(tokenSecret.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException e) {
            throw new InvalidTokenException(ErrorCode.INVALID_TOKEN, token);
        }
    }

    public JwtDto createJwtDto(String email, RoleType role) {
        Date accessTokenExpireTime = createAccessTokenExpireTime();
        Date refreshTokenExpireTime = createRefreshTokenExpireTime();

        String accessToken = createAccessToken(email, role, accessTokenExpireTime);
        String refreshToken = createRefreshToken(email, refreshTokenExpireTime);

        return JwtDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpireTime(accessTokenExpireTime)
                .refreshTokenExpireTime(refreshTokenExpireTime)
                .build();
    }

    private String createAccessToken(String email, RoleType role, Date expirationTime) {
        return Jwts.builder()
                .setHeaderParam("typ", "jwt")
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(expirationTime)
                .signWith(SignatureAlgorithm.HS512, tokenSecret.getBytes(StandardCharsets.UTF_8))
                .claim("token_type", "access_token")
                .claim("role", role)
                .compact();

    }

    private String createRefreshToken(String email, Date expirationTime) {
        return Jwts.builder()
                .setHeaderParam("typ", "jwt")
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(expirationTime)
                .signWith(SignatureAlgorithm.HS512, tokenSecret.getBytes(StandardCharsets.UTF_8))
                .claim("token_type", "access_token")
                .compact();
    }

    private Date createAccessTokenExpireTime() {
        return new Date(System.currentTimeMillis() + Long.parseLong(accessTokenExpirationTime));
    }

    private Date createRefreshTokenExpireTime() {
        return new Date(System.currentTimeMillis() + Long.parseLong(refreshTokenExpirationTime));
    }

}
