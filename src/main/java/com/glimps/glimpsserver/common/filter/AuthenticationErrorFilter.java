package com.glimps.glimpsserver.common.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.glimps.glimpsserver.common.error.CustomException;

public class AuthenticationErrorFilter extends HttpFilter {
	@Override
	protected void doFilter(HttpServletRequest request,
		HttpServletResponse response,
		FilterChain chain)
		throws IOException, ServletException {
		try {
			chain.doFilter(request, response);
		} catch (CustomException e) {
			response.sendError(e.getErrorCode().getStatus().value());
		}
	}
}
