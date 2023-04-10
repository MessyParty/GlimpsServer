package com.glimps.glimpsserver.common.util;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.glimps.glimpsserver.common.error.AuthenticationException;
import com.glimps.glimpsserver.common.error.ErrorCode;

class AuthorizationHeaderUtilsTest {

	@Test
	@DisplayName("검증하려는 Header 가 Null 이면 에러 발생")
	void given_HeaderNull_When_Validate_Then_Error() {
		//given
		final String NULL_HEADER = null;

		assertThatExceptionOfType(AuthenticationException.class).isThrownBy(() -> {
				//when
				AuthorizationHeaderUtils.validateAuthorization(NULL_HEADER);
			})
			//then
			.withMessage(ErrorCode.NOT_EXISTS_AUTHORIZATION.getMessage());
	}

	@Test
	@DisplayName("검증하려는 Header 가 빈 문자열 이면 에러 발생")
	void given_HeaderBlank_When_Validate_Then_Error() {
		//given
		final String BLANK_HEADER = "";

		assertThatExceptionOfType(AuthenticationException.class).isThrownBy(() -> {
				//when
				AuthorizationHeaderUtils.validateAuthorization(BLANK_HEADER);
			})
			//then
			.withMessage(ErrorCode.NOT_EXISTS_AUTHORIZATION.getMessage());
	}

	@Test
	@DisplayName("검증하려는 토큰의 타입이 Bearer가 아니면 에러 발생")
	void given_TypeInvalid_When_Validate_Then_Error() {
		//given
		final String INVALID_TYPE_TOKEN =
			"Basic eyJ0eXAiOiJqd3QiLCJhbGciOiJIUzUxMiJ9."
			+ "eyJzdWIiOiJ3bnN2eTYwN0BuYXZlci5jb20iLCJpYXQiOjE2Nzc0MjI0MzEsIm"
			+ "V4cCI6MTY3ODYzMjAzMSwidG9rZW5fdHlwZSI6InJlZnJlc2hfdG9rZW4ifQ."
			+ "XL52R6Ea00qWVG4RB78gbbSSo41sdbUbCWY_M2yLy8TBjcxaPjnHmeyf57FSsegZQ7qiR0BUTh5tGIRmoZO4jA";

		assertThatExceptionOfType(AuthenticationException.class).isThrownBy(() -> {
				//when
				AuthorizationHeaderUtils.validateAuthorization(INVALID_TYPE_TOKEN);
			})
			//then
			.withMessage(ErrorCode.NOT_VALID_BEARER_GRANT_TYPE.getMessage());
	}

	@Test
	@DisplayName("정상적인 입력은 에러를 발생시키지 않는다")
	void given_ValidToken_When_Validate_Then_Pass() {
		//given
		final String VALID_TOKEN =
			"Bearer eyJ0eXAiOiJqd3QiLCJhbGciOiJIUzUxMiJ9."
				+ "eyJzdWIiOiJ3bnN2eTYwN0BuYXZlci5jb20iLCJpYXQiOjE2Nzc0MjI0MzEsIm"
				+ "V4cCI6MTY3ODYzMjAzMSwidG9rZW5fdHlwZSI6InJlZnJlc2hfdG9rZW4ifQ."
				+ "XL52R6Ea00qWVG4RB78gbbSSo41sdbUbCWY_M2yLy8TBjcxaPjnHmeyf57FSsegZQ7qiR0BUTh5tGIRmoZO4jA";


		assertThatNoException().isThrownBy(() -> {
				//when
				AuthorizationHeaderUtils.validateAuthorization(VALID_TOKEN);
			}
		);
		//then
		// Nothing happens!
	}

}
