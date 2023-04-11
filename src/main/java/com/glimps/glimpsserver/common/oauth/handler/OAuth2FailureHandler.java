package com.glimps.glimpsserver.common.oauth.handler;

import static com.glimps.glimpsserver.common.error.ErrorCode.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glimps.glimpsserver.common.dto.ErrorResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

	private final ObjectMapper mapper;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException {
		response.setStatus(401);

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
		ErrorResponse of = ErrorResponse.of(OAUTH_LOGIN_FAIL.getCode(), exception.getMessage() + OAUTH_LOGIN_FAIL.getMessage() );
		String convertedError = mapper.writeValueAsString(of);

		PrintWriter writer = response.getWriter();
		writer.write(convertedError);
	}
}
