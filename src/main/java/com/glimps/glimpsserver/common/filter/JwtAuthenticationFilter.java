package com.glimps.glimpsserver.common.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glimps.glimpsserver.common.authentication.UserAuthentication;
import com.glimps.glimpsserver.common.dto.ErrorResponse;
import com.glimps.glimpsserver.common.error.CustomException;
import com.glimps.glimpsserver.session.application.AuthenticationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final AuthenticationService authenticationService;
	private final ObjectMapper mapper;
	private final List<AntPathRequestMatcher> matchers;


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws
		IOException,
		ServletException {

		if (!requiresAuthentication(request, matchers) || SecurityContextHolder.getContext().getAuthentication() != null) {
			chain.doFilter(request, response);
		} else {
			String authorizationHeader = request.getHeader("Authorization");

			try {

				UserAuthentication authentication = authenticationService.authenticate(authorizationHeader);
				SecurityContextHolder.getContext().setAuthentication(authentication);

			} catch (CustomException e) {
				responseError(response, e.getErrorCode().getCode(), e);
			} catch (Exception e) {
				responseError(response, HttpStatus.UNAUTHORIZED.toString(), e);
			} finally {
				chain.doFilter(request, response);
			}
		}

	}

	private void responseError(HttpServletResponse response, String errorCode, Exception e) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		String convertedErrorResponse = mapper.writeValueAsString(
			ErrorResponse.of(errorCode, e.getMessage()));
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
		response.getWriter().write(convertedErrorResponse);
	}

	private boolean requiresAuthentication(HttpServletRequest request, List<AntPathRequestMatcher> matcher) {
		return matcher.stream().anyMatch(m -> m.matches(request));
	}

}
