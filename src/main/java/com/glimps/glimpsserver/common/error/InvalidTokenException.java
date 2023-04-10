package com.glimps.glimpsserver.common.error;

import lombok.Getter;

@Getter
public class InvalidTokenException extends CustomException {
	
	String token;

	public InvalidTokenException(ErrorCode errorCode, String token) {
		super(errorCode);
		this.token = token;
	}
}
