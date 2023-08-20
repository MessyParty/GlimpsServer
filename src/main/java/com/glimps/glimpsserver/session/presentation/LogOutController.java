package com.glimps.glimpsserver.session.presentation;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.glimps.glimpsserver.common.authentication.UserAuthentication;
import com.glimps.glimpsserver.session.application.AuthenticationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Authentication", description = "로그아웃/토큰재발급/인증 API")
@RequiredArgsConstructor
@RequestMapping("${api.prefix}")
@RestController
public class LogOutController {

	private final AuthenticationService authenticationService;

	@Tag(name = "Authentication")
	@Operation(summary = "로그아웃 API", description = "로그아웃시 서버 DB 내부 refresh token 만료 처리")
	@PostMapping("/logout")
	public Map<String, String> logout(UserAuthentication userAuthentication) {

		String email = userAuthentication.getEmail();
		authenticationService.logout(email);

		return Map.of("result", "User(" + email +") have successfully logged out.");
	}
}
