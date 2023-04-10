package com.glimps.glimpsserver.common.dto;

import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {
	private String errorCode;
	private String errorMessage;

	// TODO 타임스탬프 추가
	// private LocalDateTime timeStamp;

	public static ErrorResponse of(String errorCode, String errorMessage) {
		return new ErrorResponse(errorCode, errorMessage);
	}

	public static ErrorResponse of(String errorCode, BindingResult bindingResult) {
		return new ErrorResponse(errorCode, createErrorMessage(bindingResult));
	}

	private static String createErrorMessage(BindingResult bindingResult) {
		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		List<FieldError> fieldErrors = bindingResult.getFieldErrors();
		for (FieldError fieldError : fieldErrors) {
			if (!isFirst) {
				sb.append(",");
			} else {
				isFirst = false;
			}
			sb.append("[");
			sb.append(fieldError.getField());
			sb.append("]");
			sb.append(fieldError.getDefaultMessage());
		}
		return sb.toString();
	}

}
