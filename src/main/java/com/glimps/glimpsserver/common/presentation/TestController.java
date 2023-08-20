package com.glimps.glimpsserver.common.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.glimps.glimpsserver.common.authentication.UserAuthentication;
import com.glimps.glimpsserver.common.jwt.JwtDto;
import com.glimps.glimpsserver.common.jwt.JwtUtil;
import com.glimps.glimpsserver.user.domain.RoleType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Test", description = "Test API")
@RequiredArgsConstructor
@RestController
public class TestController {

	private final JwtUtil jwtUtil;

	@Tag(name = "Test", description = "Test API")
	@Operation(summary = "Test API", description = "권한이 필요한 Test API")
	@GetMapping("/test")
	public String test(UserAuthentication userAuthentication) {
		if (userAuthentication == null) {
			return "userAuthentication is null";
		}
		return userAuthentication.getEmail() + " has been authenticated.";
	}
	@Tag(name = "Test", description = "Test API")
	@Operation(summary = "Test API", description = "jwt 발급 API")
	@GetMapping("/test/jwt")
	public JwtDto getTestJwt() {
		return jwtUtil.createJwtDto("wnsvy607@naver.com", RoleType.USER);
	}
}
