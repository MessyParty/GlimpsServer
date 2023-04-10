package com.glimps.glimpsserver.common.config;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class MatcherConfig {

	private static final List<String> ALL_URL = List.of("/test");
	private static final List<String> GET_URL = List.of("${api.prefix}/users");
	private static final List<String> POST_URL = List.of("${api.prefix}/logout");
	private static final List<String> PATCH_URL = List.of("${api.prefix}/users");
	private static final List<String> DELETE_URL = List.of("${api.prefix}/mock");
	private static final List<String> ADMIN_URL = List.of("/admin/**");

	@Bean
	public List<AntPathRequestMatcher> matcher() {

		List<AntPathRequestMatcher> result = ALL_URL.stream()
			.map(AntPathRequestMatcher::new)
			.collect(Collectors.toList());

		result.addAll(GET_URL.stream()
			.map((pattern) -> new AntPathRequestMatcher(pattern, HttpMethod.GET.name()))
			.collect(Collectors.toList()));

		result.addAll(POST_URL.stream()
			.map((pattern) -> new AntPathRequestMatcher(pattern, HttpMethod.POST.name()))
			.collect(Collectors.toList()));

		result.addAll(PATCH_URL.stream()
			.map((pattern) -> new AntPathRequestMatcher(pattern, HttpMethod.PATCH.name()))
			.collect(Collectors.toList()));

		result.addAll(DELETE_URL.stream()
			.map((pattern) -> new AntPathRequestMatcher(pattern, HttpMethod.DELETE.name()))
			.collect(Collectors.toList()));

		result.addAll(ADMIN_URL.stream()
			.map(AntPathRequestMatcher::new)
			.collect(Collectors.toList()));

		return result;
	}


	public static List<String> AuthURLs() {
		return ALL_URL;
	}

	public static List<String> GetURLs() {
		return GET_URL;
	}

	public static List<String> PostURLs() {
		return POST_URL;
	}

	public static List<String> PatchURLs() {
		return PATCH_URL;
	}

	public static List<String> DeleteURLs() {

		return DELETE_URL;
	}

	public static List<String> getAdminURL() {
		return ADMIN_URL;
	}

}
