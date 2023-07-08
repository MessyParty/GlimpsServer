package com.glimps.glimpsserver.common.config;

import static org.springframework.http.HttpMethod.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class MatcherConfig {
	private static final List<String> ALL_URL = new ArrayList<>();
	private static final List<String> GET_URL = new ArrayList<>();
	private static final List<String> POST_URL = new ArrayList<>();
	private static final List<String> PATCH_URL = new ArrayList<>();
	private static final List<String> DELETE_URL = new ArrayList<>();
	private static final List<String> ADMIN_URL = new ArrayList<>();

	public MatcherConfig(@Value("${api.prefix}") String prefix) {
		ALL_URL.add("/test");
		GET_URL.add(prefix + "/users");
		GET_URL.add(prefix + "/reviews/myReviews");

		POST_URL.add(prefix + "/logout");
		POST_URL.add(prefix + "/reviews");
		POST_URL.add(prefix + "/reviews/*/heart");
		POST_URL.add(prefix + "/reviews/photos/*");

		PATCH_URL.add(prefix + "/users");
		PATCH_URL.add(prefix + "/reviews/*");

		DELETE_URL.add(prefix + "/mock");
		DELETE_URL.add(prefix + "/reviews/*/heart");
		DELETE_URL.add(prefix + "/reviews/*");
		ADMIN_URL.add("/admin/**");
	}

	@Bean
	public List<AntPathRequestMatcher> matcher() {

		List<AntPathRequestMatcher> result = ALL_URL.stream()
			.map(AntPathRequestMatcher::new)
			.collect(Collectors.toList());

		GET_URL.forEach(pattern -> result.add(new AntPathRequestMatcher(pattern, GET.name())));
		POST_URL.forEach(pattern -> result.add(new AntPathRequestMatcher(pattern, POST.name())));
		PATCH_URL.forEach(pattern -> result.add(new AntPathRequestMatcher(pattern, PATCH.name())));
		DELETE_URL.forEach(pattern -> result.add(new AntPathRequestMatcher(pattern, DELETE.name())));
		ADMIN_URL.forEach(pattern -> result.add(new AntPathRequestMatcher(pattern)));

		return result;
	}


	public static List<String> authURLs() {
		return ALL_URL;
	}

	public static List<String> getURLs() {
		return GET_URL;
	}

	public static List<String> postURLs() {
		return POST_URL;
	}

	public static List<String> patchURLs() {
		return PATCH_URL;
	}

	public static List<String> deleteURLs() {
		return DELETE_URL;
	}

	public static List<String> getAdminURL() {
		return ADMIN_URL;
	}

}
