package com.glimps.glimpsserver.common.jwt;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class JwtDto {

	private String grantType;
	private String accessToken;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Seoul")
	private Date accessTokenExpireTime;
	private String refreshToken;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Seoul")
	private Date refreshTokenExpireTime;
}
