package com.glimps.glimpsserver.common.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glimps.glimpsserver.common.filter.CustomAccessDeniedHandler;
import com.glimps.glimpsserver.common.filter.CustomAuthenticationEntryPoint;
import com.glimps.glimpsserver.common.filter.JwtAuthenticationFilter;
import com.glimps.glimpsserver.common.oauth.handler.OAuth2FailureHandler;
import com.glimps.glimpsserver.common.oauth.handler.OAuth2SuccessHandler;
import com.glimps.glimpsserver.common.oauth.service.CustomOAuth2UserService;
import com.glimps.glimpsserver.session.application.AuthenticationService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfig {

	private final OAuth2SuccessHandler successHandler;
	private final CustomOAuth2UserService oAuth2UserService;
	private final CustomAuthenticationEntryPoint authenticationEntryPoint;
	private final CustomAccessDeniedHandler accessDeniedHandler;
	private final AuthenticationService authenticationService;
	private final ObjectMapper mapper;
	private final List<AntPathRequestMatcher> matchers;
	private final OAuth2FailureHandler oAuth2FailureHandler;


	@Bean

	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.cors();

		http.csrf()
			.disable();

		http.exceptionHandling()
			.authenticationEntryPoint(authenticationEntryPoint)
			.accessDeniedHandler(accessDeniedHandler);

		http.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		http.formLogin()
			.disable();

		http.headers()
			.addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN));

		http.oauth2Login()
			.failureHandler(oAuth2FailureHandler)
			.successHandler(successHandler)
			.userInfoEndpoint()
			.userService(oAuth2UserService);


		http.authorizeRequests()
			.antMatchers(MatcherConfig.AuthURLs().toArray(new String[0])).authenticated()
			.antMatchers(HttpMethod.GET,MatcherConfig.GetURLs().toArray(new String[0])).authenticated()
			.antMatchers(HttpMethod.POST,MatcherConfig.PostURLs().toArray(new String[0])).authenticated()
			.antMatchers(HttpMethod.PATCH,MatcherConfig.PatchURLs().toArray(new String[0])).authenticated()
			.antMatchers(HttpMethod.DELETE,MatcherConfig.DeleteURLs().toArray(new String[0])).authenticated()
			.antMatchers(MatcherConfig.getAdminURL().toArray(new String[0])).hasRole("ADMIN")
			.anyRequest().permitAll();

		http.addFilterBefore(new JwtAuthenticationFilter(authenticationService, mapper, matchers),
			UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

}
