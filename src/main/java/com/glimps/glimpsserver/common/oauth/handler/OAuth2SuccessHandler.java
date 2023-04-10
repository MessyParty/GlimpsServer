package com.glimps.glimpsserver.common.oauth.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glimps.glimpsserver.common.jwt.JwtDto;
import com.glimps.glimpsserver.session.application.AuthenticationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

	private final AuthenticationService authenticationService;
	private final ObjectMapper mapper;

	/**
	 * OAuth 로그인 성공 시 JWT 발급 및 회원 가입
	 */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {

		OAuth2User oauth2User = (OAuth2User)authentication.getPrincipal();
		JwtDto jwtDto = authenticationService.oauthLogin(oauth2User);

		responseJwt(response, jwtDto);

	}

	private void responseJwt(HttpServletResponse response, JwtDto jwtDto) throws IOException {
		String convertedDto = mapper.writeValueAsString(jwtDto);
		PrintWriter writer = response.getWriter();

		response.setStatus(HttpStatus.ACCEPTED.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.toString());

		writer.write(convertedDto);
	}
}
