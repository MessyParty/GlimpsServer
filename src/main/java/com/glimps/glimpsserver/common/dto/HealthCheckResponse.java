package com.glimps.glimpsserver.common.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HealthCheckResponse {

	@Schema(description = "서버 health 상태", example = "ok", required = true)
	private String health;

	@Schema(description = "현재 실행 중인 profile", example = "[prod]", required = true)
	private List<String> activeProfiles;

	@Schema(description = "현재 요청 서버 시간", example = "2023-01-23 16:31:04", required = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	private LocalDateTime currentTimeStamp;

	private String redirectUri;
}
