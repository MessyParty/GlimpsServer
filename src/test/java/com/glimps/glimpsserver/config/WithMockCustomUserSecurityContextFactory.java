package com.glimps.glimpsserver.config;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.glimps.glimpsserver.common.authentication.UserAuthentication;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

	@Override
	public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
		final SecurityContext context = SecurityContextHolder.getContext();
		UserAuthentication authentication = new UserAuthentication(annotation.userName(),
			List.of(new SimpleGrantedAuthority(annotation.role())));

		context.setAuthentication(authentication);
		return context;
	}

}
