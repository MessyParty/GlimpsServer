package com.glimps.glimpsserver.session.presentation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.glimps.glimpsserver.session.dto.OAuthStateResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Authentication", description = "로그아웃/토큰재발급/인증 API")
@RequestMapping("${api.prefix}")
@RestController
public class SessionController {
	private final DefaultOAuth2AuthorizationRequestResolver oAuth2AuthorizationRequestResolver;
	private final HttpSessionOAuth2AuthorizationRequestRepository oauth2Repository;

	public SessionController(ClientRegistrationRepository clientRegistrationRepository) {
		this.oAuth2AuthorizationRequestResolver = new DefaultOAuth2AuthorizationRequestResolver(
			clientRegistrationRepository, "/oauth2/authorization");
		this.oauth2Repository = new HttpSessionOAuth2AuthorizationRequestRepository();
	}

	@Tag(name = "Authentication")
	@Operation(summary = "OAuth 로그인 요청 전 필수적으로 요청해야 하는 API 입니다.",
		description = "클라이언트를 확인하고 클라이언트 확인에 필요한 state 값을 반환해줍니다. "
		+ "요청 시 withCredentials 옵션을 반드시 true로 해줘야 합니다.")
	@GetMapping("/session/state")
	public OAuthStateResponse session(HttpServletRequest request, HttpServletResponse response,
		@Parameter(description = "소셜 로그인 서비스의 이름", example = "kakao", required = true)
		@RequestParam String provider) {

		// OAuth 인가 요청을 생성
		OAuth2AuthorizationRequest oAuth2AuthorizationRequest = oAuth2AuthorizationRequestResolver.resolve(request,
			provider);

		// 요청을 임시 Session에 저장
		oauth2Repository.saveAuthorizationRequest(oAuth2AuthorizationRequest, request,
			response);

		// 반환된 state 값으로 클라이언트를 확인
		return new OAuthStateResponse(oAuth2AuthorizationRequest.getState());
	}
}
