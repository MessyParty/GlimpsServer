package com.glimps.glimpsserver.common.util;

import org.springframework.util.StringUtils;

import com.glimps.glimpsserver.common.error.AuthenticationException;
import com.glimps.glimpsserver.common.error.ErrorCode;

public class AuthorizationHeaderUtils {
	private AuthorizationHeaderUtils() {
	}

	public static void validateAuthorization(String authorizationHeader) {

		// 1. authorizationHeader 필수 체크
		if (!StringUtils.hasText(authorizationHeader)) {
			throw new AuthenticationException(ErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		// 2. authorizationHeader Bearer 체크
		String[] authorizations = authorizationHeader.split(" ");
		if (authorizations.length < 2 || (!"Bearer".equals(authorizations[0]))) {
			throw new AuthenticationException(ErrorCode.NOT_VALID_BEARER_GRANT_TYPE);
		}
	}
}
