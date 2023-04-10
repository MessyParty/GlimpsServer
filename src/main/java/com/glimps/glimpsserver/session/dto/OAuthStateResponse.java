package com.glimps.glimpsserver.session.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OAuthStateResponse {

	@Schema(description = "인가 코드와 함께 서버로 보낼 임시 상태 값(쿼리 파라미터로 보내야 합니다.)",
		example = "VmYGCD0Pf1HbzhaNTd4laNu_-tvWZQjMXU1GYSlyFJA=", required = true)
	private String state;
}
