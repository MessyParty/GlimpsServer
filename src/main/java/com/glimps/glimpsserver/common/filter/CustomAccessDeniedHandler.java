package com.glimps.glimpsserver.common.filter;

import static com.glimps.glimpsserver.common.error.ErrorCode.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glimps.glimpsserver.common.dto.ErrorResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	private final ObjectMapper mapper;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws IOException, ServletException {

		response.setStatus(403);

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
		ErrorResponse of = ErrorResponse.of(NO_AUTHORITY.getCode(), NO_AUTHORITY.getMessage() );
		String convertedError = mapper.writeValueAsString(of);

		PrintWriter writer = response.getWriter();
		writer.write(convertedError);
	}
}
