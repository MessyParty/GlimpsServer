package com.glimps.glimpsserver.common.presentation;

import static org.springframework.http.HttpStatus.*;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.glimps.glimpsserver.common.dto.ErrorResponse;
import com.glimps.glimpsserver.common.error.CustomException;
import com.glimps.glimpsserver.common.error.EntityNotFoundException;
import com.glimps.glimpsserver.common.error.InvalidTokenException;
import com.glimps.glimpsserver.common.error.UserDuplicationException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class ControllerErrorAdvice {
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception e) {
		log.error("Exception occurs: {}", e.getMessage());
		ErrorResponse errorResponse = ErrorResponse.of(INTERNAL_SERVER_ERROR.toString(), e.getMessage());
		return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(errorResponse);
	}

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
		log.error("Exception occurs: {}", e.getMessage());
		ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode().getCode(), e.getMessage());
		return ResponseEntity.status(e.getErrorCode().getStatus()).body(errorResponse);
	}

	@ExceptionHandler(BindException.class)
	public ResponseEntity<ErrorResponse> handleBindException(BindException e) {
		log.error("Exception occurs: {}", e.getMessage());
		ErrorResponse errorResponse = ErrorResponse.of(BAD_REQUEST.toString(), e.getBindingResult());
		return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
	}

	/**
	 * Entity 를 DB로부터 못찾을 경우 발생하는 에러 처리
	 * TODO 어떤 Entity 인지 나타내도록 코드 수정 필요
	 */
	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
		// 로그에는 ID, EMAIL 전부 노출, 반환값으로는 EMAIL만 노출
		log.error("Exception occurs: {}, Id: {}, Email: {}", e.getMessage(), e.getId(), e.getEmail());
		ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode().getCode(),
			e.getMessage() + " Email: " + e.getEmail());
		return ResponseEntity.status(e.getErrorCode().getStatus()).body(errorResponse);
	}

	/**
	 * 유효하지 않은 토큰에 대한 에러 처리
	 */
	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<ErrorResponse> handleInvalidTokenException(InvalidTokenException e) {
		log.error("Exception occurs: {}, Invalid token: {}", e.getMessage(), e.getToken());
		ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode().getCode(),
			e.getMessage() + " Token: " + e.getToken());
		return ResponseEntity.status(e.getErrorCode().getStatus()).body(errorResponse);
	}

	/**
	 * 회원가입시 같은 이메일을 가진 유저가 이미 있을 때 어디로 가입되어 있는지 반환
	 */
	@ExceptionHandler(UserDuplicationException.class)
	protected ResponseEntity<ErrorResponse> handleDuplicateRequestException(UserDuplicationException e) {
		log.error("Exception occurs: {}, User type: {}", e.getMessage(), e.getUserType());
		ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode().getCode(),
			e.getMessage() + " UserType: " + e.getUserType());
		return ResponseEntity.status(e.getErrorCode().getStatus()).body(errorResponse);
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e) {
		log.error("Exception occurs: {}", e.getMessage());
		ErrorResponse errorResponse = ErrorResponse.of(UNAUTHORIZED.toString(), e.getMessage());
		return ResponseEntity.status(UNAUTHORIZED).body(errorResponse);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handlerAccessDeniedException(AccessDeniedException e) {
		log.error("Exception occurs: {}", e.getMessage());
		ErrorResponse errorResponse = ErrorResponse.of(FORBIDDEN.toString(), e.getMessage());
		return ResponseEntity.status(FORBIDDEN).body(errorResponse);
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException e) {
		log.error("Exception occurs: {}", e.getMessage());
		ErrorResponse errorResponse = ErrorResponse.of(NOT_FOUND.toString(), e.getMessage());
		return ResponseEntity.status(NOT_FOUND).body(errorResponse);
	}

	@ExceptionHandler(IOException.class)
	public ResponseEntity<ErrorResponse> handleIOException(IOException e) {
		log.error("Exception occurs: {}", e.getMessage());
		ErrorResponse errorResponse = ErrorResponse.of("[ERROR] S3가 응답하지 않습니다.", e.getMessage());
		return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(errorResponse);
	}

	@ExceptionHandler(AmazonS3Exception.class)
	public ResponseEntity<ErrorResponse> handleAmazonS3Exception(AmazonS3Exception e) {
		log.error("Exception occurs: {}", e.getMessage());
		ErrorResponse errorResponse = ErrorResponse.of("[ERROR] S3가 응답하지 않습니다.", e.getMessage());
		return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(errorResponse);
	}

}
