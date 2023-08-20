package com.glimps.glimpsserver.common.jwt;

import lombok.Getter;

@Getter
public enum TokenType {

	ACCESS_TOKEN("access_token"),
	REFRESH_TOKEN("refresh_token");

	private final String type;

	TokenType(String type) {
		this.type = type;
	}

	public static boolean isAccessToken(String tokenType) {
		return TokenType.ACCESS_TOKEN.getType().equals(tokenType);
	}
}
