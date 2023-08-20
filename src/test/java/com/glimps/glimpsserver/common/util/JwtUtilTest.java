package com.glimps.glimpsserver.common.util;

import static org.assertj.core.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.glimps.glimpsserver.common.error.ErrorCode;
import com.glimps.glimpsserver.common.error.InvalidTokenException;
import com.glimps.glimpsserver.common.jwt.JwtDto;
import com.glimps.glimpsserver.common.jwt.JwtUtil;
import com.glimps.glimpsserver.common.jwt.TokenType;
import com.glimps.glimpsserver.session.dto.AccessTokenDto;
import com.glimps.glimpsserver.user.domain.RoleType;

import io.jsonwebtoken.Claims;

class JwtUtilTest {

	private static final String SECRET = "ugKv3L25LnW1ndJvnUpTZQ77MxkVxqhpexelDc5mR5MmWMFyTnm8h12J8q3wn7dAE";
	private static final String ACCESS_TOKEN_EXP_TIME = "300000";
	private static final String REFRESH_TOKEN_EXP_TIME = "7200000";

	private static final String OTHER_SECRET = "CBzJlXC1G9MLOQz7T25ixUlfBctkXMV18kj8nDtB2mcqfRCY5YAl3XXz0o0kz2s4018DYl";
	private static final String EXPIRED_TIME = "-300000";

	private final JwtUtil jwtUtil = new JwtUtil(ACCESS_TOKEN_EXP_TIME, REFRESH_TOKEN_EXP_TIME, SECRET);

	private final JwtUtil jwtUtilWithInvalidKey = new JwtUtil(ACCESS_TOKEN_EXP_TIME, REFRESH_TOKEN_EXP_TIME,
		OTHER_SECRET);
	private final JwtUtil jwtUtilExpired = new JwtUtil(EXPIRED_TIME, EXPIRED_TIME, SECRET);

	private static final String EMAIL = "wnsvy607@naver.com";
	private static final RoleType ROLE = RoleType.USER;

	@Test
	@DisplayName("JwtToken 발급")
	void issueJWT() {
		//when
		JwtDto jwtDto = jwtUtil.createJwtDto(EMAIL, ROLE);

		//then
		assertThat(jwtDto.getAccessToken()).isNotNull().isNotBlank();
		assertThat(jwtDto.getRefreshToken()).isNotNull().isNotBlank();
		assertThat(jwtDto.getAccessTokenExpireTime()).isNotNull().isAfter(new Date());
		assertThat(jwtDto.getRefreshTokenExpireTime()).isNotNull().isAfter(new Date());
		assertThat(jwtDto.getGrantType()).isEqualTo("Bearer");
	}

	@Test
	@DisplayName("액세스 토큰 발급시 예외가 발생하지 않는다.")
	void issueAccessToken() {
		//when
		AccessTokenDto accessTokenDto = jwtUtil.createAccessTokenDto(EMAIL, ROLE);

		//then
		assertThat(accessTokenDto.getAccessToken()).isNotNull().isNotBlank();
		assertThat(accessTokenDto.getAccessTokenExpireTime()).isNotNull().isAfter(new Date());
		assertThat(accessTokenDto.getGrantType()).isEqualTo("Bearer");
	}

	@Test
	@DisplayName("유효한 JWT를 Decode하면 Encode한 것과 동일한 Claim을 얻는다.")
	void Given_ValidToken_When_DecodeToken_Then_GetEqualClaims() {
		//given
		JwtDto jwtDto = jwtUtil.createJwtDto(EMAIL, ROLE);

		//when
		Claims accessTokenClaims = jwtUtil.decode(jwtDto.getAccessToken());
		Claims refreshTokenClaims = jwtUtil.decode(jwtDto.getRefreshToken());

		//then
		assertThat(accessTokenClaims.getSubject()).isEqualTo(EMAIL);
		assertThat(accessTokenClaims.get("token_type")).isEqualTo(TokenType.ACCESS_TOKEN.getType());
		assertThat(refreshTokenClaims.getSubject()).isEqualTo(EMAIL);
		assertThat(refreshTokenClaims.get("token_type")).isEqualTo(TokenType.REFRESH_TOKEN.getType());
		assertThat(accessTokenClaims.get("role")).isEqualTo(ROLE.toString());
	}

	@Test
	@DisplayName("만료된 JWT를 Decode하면 에러 발생")
	void Given_ExpiredToken_When_DecodeToken_Then_Error() {
		//given
		JwtDto jwtDto = jwtUtilExpired.createJwtDto(EMAIL, ROLE);

		assertThatExceptionOfType(InvalidTokenException.class).isThrownBy(() -> {
				//when
				jwtUtil.decode(jwtDto.getAccessToken());
			})
			//then
			.withMessage(ErrorCode.TOKEN_EXPIRED.getMessage());

		assertThatExceptionOfType(InvalidTokenException.class).isThrownBy(() -> {
				//when
				jwtUtil.decode(jwtDto.getRefreshToken());
			})
			//then
			.withMessage(ErrorCode.TOKEN_EXPIRED.getMessage());

	}

	@Test
	@DisplayName("signature가 다른 JWT를 Decode하면 에러 발생")
	void Given_DifferentSignatureToken_When_DecodeToken_Then_Error() {
		//given
		JwtDto jwtDto = jwtUtilWithInvalidKey.createJwtDto(EMAIL, ROLE);

		assertThatExceptionOfType(InvalidTokenException.class).isThrownBy(() -> {
				//when
				jwtUtil.decode(jwtDto.getAccessToken());
			})
			//then
			.withMessage(ErrorCode.INVALID_TOKEN.getMessage());

		assertThatExceptionOfType(InvalidTokenException.class).isThrownBy(() -> {
				//when
				jwtUtil.decode(jwtDto.getRefreshToken());
			})
			//then
			.withMessage(ErrorCode.INVALID_TOKEN.getMessage());

	}

}
