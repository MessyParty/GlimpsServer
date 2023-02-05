package com.glimps.glimpsserver.common.config;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import com.glimps.glimpsserver.common.filter.AuthenticationErrorFilter;
import com.glimps.glimpsserver.common.filter.JwtAuthenticationFilter;
import com.glimps.glimpsserver.session.application.AuthenticationService;

@EnableWebSecurity
public class SpringSecurityConfig {
	@Autowired
	private AuthenticationService authenticationService;

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		AuthenticationManager authenticationManager = authenticationManager(
			http.getSharedObject(AuthenticationConfiguration.class));
		Filter authenticationFilter = new JwtAuthenticationFilter(authenticationManager, authenticationService);
		Filter authenticationErrorFilter = new AuthenticationErrorFilter();

		http.csrf().disable();

		http.formLogin().disable();

		http.addFilter(authenticationFilter);

		http.addFilterAfter(authenticationErrorFilter, JwtAuthenticationFilter.class);

		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.exceptionHandling()
			.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));

		return http.build();
	}

}
