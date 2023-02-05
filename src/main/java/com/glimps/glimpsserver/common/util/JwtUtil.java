package com.glimps.glimpsserver.common.util;

import java.security.Key;
import java.util.Date;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.glimps.glimpsserver.common.error.ErrorCode;
import com.glimps.glimpsserver.common.error.InvalidTokenException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtUtil {
	private final JwtBuilder jwtBuilder;
	private final JwtParser parser;

	public JwtUtil() {
		byte[] arr = new byte[32];
		new Random().nextBytes(arr);
		Key key = Keys.hmacShaKeyFor(arr);

		jwtBuilder = Jwts.builder()
			.signWith(key);

		parser = Jwts.parserBuilder()
			.setSigningKey(key)
			.build();
	}

	public String encode(String email) {
		// TODO claim에 Role을 추가해야 하는 지 의논 필요
		return jwtBuilder
			.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
			.setHeaderParam("type", "jwt")
			.claim("email", email)
			.compact();
	}

	public Claims decode(String token) {
		try {
			return parser
				.parseClaimsJws(token)
				.getBody();
		} catch (SignatureException e) {
			throw new InvalidTokenException(ErrorCode.INVALID_TOKEN, token);
		} catch (ExpiredJwtException e) {
			throw new InvalidTokenException(ErrorCode.TOKEN_EXPIRED, token);
		} catch (Exception e) {
			throw new InvalidTokenException(ErrorCode.INVALID_TOKEN, token);
		}

	}
}
