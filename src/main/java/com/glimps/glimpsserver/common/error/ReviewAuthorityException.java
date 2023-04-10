package com.glimps.glimpsserver.common.error;

import lombok.Getter;

@Getter
public class ReviewAuthorityException extends CustomException {
	private final String email;
	public ReviewAuthorityException(ErrorCode errorCode, String email) {
		super(errorCode);
		this.email = email;
	}
}
