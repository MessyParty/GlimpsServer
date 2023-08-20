package com.glimps.glimpsserver.user.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPatchRequest {

	@NotBlank
	@Size(min = 2, max = 10)
	@Pattern(regexp = "^[ㄱ-ㅎ가-힣A-Za-z0-9]*$")
	@Schema(description = "새로 변경될 닉네임, 2-10자", example = "무지와춘식이", required = true)
	private String nickname;

}
