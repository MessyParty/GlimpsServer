package com.glimps.glimpsserver.common.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;

import com.fasterxml.classmate.TypeResolver;
import com.glimps.glimpsserver.common.authentication.UserAuthentication;
import com.glimps.glimpsserver.common.config.swagger.SwaggerPageable;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {
	TypeResolver typeResolver = new TypeResolver();
	public static final String AUTH_HEADER = "Authorization";

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.OAS_30)
			.select()   // ApiSelectorBuilder 생성
			.apis(RequestHandlerSelectors.basePackage("com.glimps.glimpsserver"))   // API 패키지 경로
			// .paths(PathSelectors.ant("/api/**"))    //path 조건에 따라서 API 문서화
			.paths(PathSelectors.any())       // <- 모든 path 에 대해서 명세 표시
			.build()
			.apiInfo(apiInfo()) // API 문서에 대한 정보 추가
			.useDefaultResponseMessages(false)  // swagger 에서 제공하는 기본 응답 코드 설명 제거
			.securityContexts(Arrays.asList(securityContext()))
			.securitySchemes(Arrays.asList(apiKey()))
			.ignoredParameterTypes(UserAuthentication.class)
			.alternateTypeRules(AlternateTypeRules.newRule(typeResolver.resolve(Pageable.class),
				typeResolver.resolve((SwaggerPageable.class))));
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
			.title("Glims API 문서")
			.description("API 스펙에 대해서 설명해주는 문서입니다.")
			.version("0.9.2")
			.build();
	}

	private SecurityContext securityContext() {
		return SecurityContext.builder()
			.securityReferences(defaultAuth())
			.build();
	}

	private List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Arrays.asList(new SecurityReference(AUTH_HEADER, authorizationScopes));
	}

	private ApiKey apiKey() {
		return new ApiKey(AUTH_HEADER, AUTH_HEADER, "header");
	}

}
