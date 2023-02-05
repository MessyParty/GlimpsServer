package com.glimps.glimpsserver.common.util;

import org.springframework.util.StringUtils;

import com.glimps.glimpsserver.common.constant.GrantType;
import com.glimps.glimpsserver.common.error.ErrorCode;
import com.glimps.glimpsserver.common.error.InvalidTokenException;

public class AuthorizationHeaderUtils {

	public static void validateAuthorization(String authorizationHeader) {

		// 1. authorizationHeader 필수 체크
		if (!StringUtils.hasText(authorizationHeader)) {
			throw new InvalidTokenException(ErrorCode.NOT_EXISTS_AUTHORIZATION, authorizationHeader);
		}

		// 2. authorizationHeader Bearer 체크
		String[] authorizations = authorizationHeader.split(" ");
		if (authorizations.length < 2 || (!GrantType.BEARER.getType().equals(authorizations[0]))) {
			throw new InvalidTokenException(ErrorCode.NOT_VALID_BEARER_GRANT_TYPE, authorizationHeader);
		}
	}

}
