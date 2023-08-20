package com.glimps.glimpsserver.session.dto;

import com.glimps.glimpsserver.user.domain.UserType;

public interface SignUpRequest {

	String getName();

	String getEmail();

	UserType getUserType();
}
