package com.glimps.glimpsserver.common.error;

import com.glimps.glimpsserver.user.domain.UserType;

import lombok.Getter;

@Getter
public class UserDuplicationException extends CustomException {
	private final UserType userType;

	public UserDuplicationException(ErrorCode errorCode, UserType userType) {
		super(errorCode);
		this.userType = userType;
	}
}
