package com.glimps.glimpsserver.common.presentation;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.glimps.glimpsserver.common.dto.HealthCheckResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Health check", description = "서버 상태 체크 API")
@RestController
public class HealthCheckController {

	private final Environment environment;
	private final String redirectUri;

	public HealthCheckController(Environment environment,
		@Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}") String redirectUri) {
		this.environment = environment;
		this.redirectUri = redirectUri;
	}

	@Tag(name = "Health check")
	@GetMapping("/health")
	@Operation(summary = "서버 Health Check API", description = "현재 서버가 정상적으로 기동이 된 상태인지 검사하는 API")
	public HealthCheckResponse healthCheck() {
		return HealthCheckResponse.builder()
			.health("서버가 정상적인 상태입니다.")
			.activeProfiles(Arrays.asList(environment.getActiveProfiles()))
			.currentTimeStamp(LocalDateTime.now())
			.redirectUri(redirectUri)
			.build();
	}

}
