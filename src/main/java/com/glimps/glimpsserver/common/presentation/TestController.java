package com.glimps.glimpsserver.common.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.glimps.glimpsserver.common.authentication.UserAuthentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Test", description = "Test API")
@RestController
public class TestController {


	@Tag(name = "Test", description = "Test API")
	@Operation(summary = "Test API", description = "권한이 필요한 Test API")
	@GetMapping("/test")
	public String test(UserAuthentication userAuthentication) {
		if (userAuthentication == null) {
			return "userAuthentication is null";
		}
		return userAuthentication.getEmail() + " has been authenticated.";
	}
}
