package com.glimps.glimpsserver.session.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.glimps.glimpsserver.user.domain.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserInfoDto {


	@Schema(description = "이메일", example = "kim12345@naver.com", required = true)
	private String email;

	@Schema(description = "닉네임", example = "홍길동", required = true)
	private String nickname;
	@Schema(description = "리뷰 카운트", example = "8", required = true)
	private int reviewCnt;

	@Schema(description = "역할", example = "USER", required = true)
	private String role;
	@Schema(description = "유저가 가입한 플랫폼", example = "KAKAO", required = true)
	private String userType;

	@Schema(description = "access Token 만료 시간", example = "2023-03-01 16:31:04.019", required = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Seoul")
	private LocalDateTime createdAt;


	public static UserInfoDto of(User user) {
		return UserInfoDto.builder()
			.email(user.getEmail())
			.nickname(user.getNickname())
			.reviewCnt(user.getReviewCnt())
			.role(user.getRole().name())
			.userType(user.getUserType().name())
			.createdAt(user.getCreatedAt())
			.build();
	}
}
