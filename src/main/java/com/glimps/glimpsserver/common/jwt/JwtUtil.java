package com.glimps.glimpsserver.common.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.glimps.glimpsserver.common.error.ErrorCode;
import com.glimps.glimpsserver.common.error.InvalidTokenException;
import com.glimps.glimpsserver.session.dto.AccessTokenDto;
import com.glimps.glimpsserver.user.domain.RoleType;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {

	private final String accessTokenExpirationTime;
	private final String refreshTokenExpirationTime;
	private final String tokenSecret;

	public JwtUtil(
		@Value("${token.access-token-expiration-time}") String accessTokenExpirationTime,
		@Value("${token.refresh-token-expiration-time}") String refreshTokenExpirationTime,
		@Value("${token.secret}") String tokenSecret) {
		this.accessTokenExpirationTime = accessTokenExpirationTime;
		this.refreshTokenExpirationTime = refreshTokenExpirationTime;
		this.tokenSecret = tokenSecret;
	}

	public Claims decode(String token) {
		try {
			return Jwts.parserBuilder()
				.setSigningKey(tokenSecret.getBytes(StandardCharsets.UTF_8))
				.build()
				.parseClaimsJws(token)
				.getBody();
		} catch (ExpiredJwtException e) {
			throw new InvalidTokenException(ErrorCode.TOKEN_EXPIRED, token);
		} catch (JwtException e) {
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

	public AccessTokenDto createAccessTokenDto(String email, RoleType role) {
		Date accessTokenExpireTime = createAccessTokenExpireTime();
		String accessToken = createAccessToken(email, role, accessTokenExpireTime);

		return AccessTokenDto.builder()
			.grantType("Bearer")
			.accessToken(accessToken)
			.accessTokenExpireTime(accessTokenExpireTime)
			.build();
	}

	public String createAccessToken(String email, RoleType role, Date expirationTime) {
		return Jwts.builder()
			.setHeaderParam("typ", "jwt")
			.setSubject(email)
			.setIssuedAt(new Date())
			.setExpiration(expirationTime)
			.signWith(SignatureAlgorithm.HS512, tokenSecret.getBytes(StandardCharsets.UTF_8))
			.claim("token_type", TokenType.ACCESS_TOKEN.getType())
			.claim("role", role.toString())
			.compact();
	}

	private String createRefreshToken(String email, Date expirationTime) {
		return Jwts.builder()
			.setHeaderParam("typ", "jwt")
			.setSubject(email)
			.setIssuedAt(new Date())
			.setExpiration(expirationTime)
			.signWith(SignatureAlgorithm.HS512, tokenSecret.getBytes(StandardCharsets.UTF_8))
			.claim("token_type", TokenType.REFRESH_TOKEN.getType())
			.compact();
	}

	private Date createAccessTokenExpireTime() {
		return new Date(System.currentTimeMillis() + Long.parseLong(accessTokenExpirationTime));
	}

	private Date createRefreshTokenExpireTime() {
		return new Date(System.currentTimeMillis() + Long.parseLong(refreshTokenExpirationTime));
	}

}
