package com.glimps.glimpsserver.common.error;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends CustomException {
	public EntityNotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}
}
