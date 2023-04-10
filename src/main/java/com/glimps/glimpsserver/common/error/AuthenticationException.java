package com.glimps.glimpsserver.common.error;

public class AuthenticationException extends CustomException {

	public AuthenticationException(ErrorCode errorCode) {
		super(errorCode);
	}
}
