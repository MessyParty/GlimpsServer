package com.glimps.glimpsserver.common.error;

import java.util.UUID;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends CustomException {
	// TODO Entity에 따라 보여줄 정보를 다르게 해야함
	private Long id;
	private String email;
	private UUID uuid;

	public EntityNotFoundException(ErrorCode errorCode, Long id, String email) {
		super(errorCode);
		this.id = id;
		this.email = email;
	}

	public EntityNotFoundException(ErrorCode errorCode, Long id) {
		super(errorCode);
		this.id = id;
	}

	public EntityNotFoundException(ErrorCode errorCode, String email) {
		super(errorCode);
		this.email = email;
	}

	public EntityNotFoundException(ErrorCode errorCode, UUID uuid) {
		super(errorCode);
		this.uuid = uuid;
	}
}
