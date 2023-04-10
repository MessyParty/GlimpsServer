package com.glimps.glimpsserver.common.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

	// E-XXX
	EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "E-001", "서버 내부 오류입니다."),

	// U-XXX - Error about User Entity
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "M-001", "사용자를 찾을 수 없습니다."),
	ALREADY_REGISTERED_USER(HttpStatus.BAD_REQUEST, "M-002", "이미 가입된 회원 입니다."),

	// A-XXX - Authentication Error
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A-001", "유효하지 않은 토큰입니다."),
	NOT_EXISTS_AUTHORIZATION(HttpStatus.UNAUTHORIZED, "A-002", "Authorization Header 가 빈값입니다."),
	NOT_VALID_BEARER_GRANT_TYPE(HttpStatus.UNAUTHORIZED, "A-003", "인증 타입이 Bearer 타입이 아닙니다."),
	TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A-004", "토큰이 만료되었습니다."),
	INVALID_PROVIDER(HttpStatus.BAD_REQUEST, "A-005", "유효하지 않은 provider 입니다."),
	NOT_ACCESS_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "A-006", "토큰 종류가 ACCESS TOKEN이 아닙니다."),
	NO_AUTHORITY(HttpStatus.FORBIDDEN, "A-007", "해당 리소스에 대한 권한이 없습니다."),
	REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "A-008", "해당 refresh token은 존재하지 않습니다."),
	REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A-009", "해당 refresh token은 만료됐습니다."),

	// P-XXX - Error about Perfume
	PERFUME_NOT_FOUND(HttpStatus.NOT_FOUND, "P-001", "향수를 찾을 수 없습니다."),

	// R-XXX - Error about Review
	REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "R-001", "[ERROR] 리뷰를 찾을 수 없습니다."),

	// H-XXX - Error about Review Heart
	REVIEW_HEART_NOT_FOUND(HttpStatus.NOT_FOUND, "H-002", "[ERROR] 리뷰 하트를 찾을 수 없습니다."),

	// O-XXX - OAuth Login Fail
	OAUTH_LOGIN_FAIL(HttpStatus.NOT_FOUND, "O-001", "소셜 로그인에 실패했습니다.");


	private final HttpStatus status;
	private final String code;
	private final String message;

	ErrorCode(HttpStatus status, String errorCode, String message) {
		this.status = status;
		this.code = errorCode;
		this.message = message;
	}
}
